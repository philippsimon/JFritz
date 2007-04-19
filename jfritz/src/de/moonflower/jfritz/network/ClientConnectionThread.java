package de.moonflower.jfritz.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import java.util.Vector;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.callerlist.CallerListListener;
import de.moonflower.jfritz.phonebook.PhoneBookListener;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.utils.Debug;

/**
 * This class is responsible for interacting with a JFritz client.
 * All communication between client and server is asynchronus.
 * 
 * Communication between client and server is done using either
 * ClientRequest or String objects. Communication between server 
 * and client is done using DataChange or String objects.
 *
 * This thread exits automatically once the connection has been closed.
 * 
 * @author brian
 *
 */
public class ClientConnectionThread extends Thread implements CallerListListener,
			PhoneBookListener {

	private Socket socket;
	
	private Login login;
	
	private InetAddress remoteAddress;
	
	private ObjectInputStream objectIn;
	
	private ObjectOutputStream objectOut;
	
	private DataChange<Call> callsAdd, callsRemove;
	
	private DataChange<Person> contactsAdd, contactsRemove;
	
	private boolean callsAdded=false, callsRemoved=false,
		contactsAdded=false, contactsRemoved=false;
	
	public ClientConnectionThread(Socket socket){
		super("Client connection for "+socket.getInetAddress());
		this.socket = socket;
		remoteAddress = socket.getInetAddress();
	}
	
	public void run(){
		
		Debug.msg("Accepted incoming connection from "+remoteAddress);
		
		try{
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			objectIn = new ObjectInputStream(socket.getInputStream());
			
			if((login = authenticateClient()) != null){
			
				Debug.msg("Authentication for client "+remoteAddress+" successful!");
				callsAdd = new DataChange<Call>();
				callsAdd.destination = DataChange.Destination.CALLLIST;
				callsAdd.operation = DataChange.Operation.ADD;
				callsRemove = new DataChange<Call>();
				callsRemove.destination = DataChange.Destination.CALLLIST;
				callsRemove.operation = DataChange.Operation.REMOVE;
				
				contactsAdd = new DataChange<Person>();
				contactsAdd.destination = DataChange.Destination.PHONEBOOK;
				contactsAdd.operation = DataChange.Operation.ADD;
				contactsRemove = new DataChange<Person>();
				contactsRemove.destination = DataChange.Destination.PHONEBOOK;
				contactsRemove.operation = DataChange.Operation.REMOVE;
				
				JFritz.getCallerList().addListener(this);
				JFritz.getPhonebook().addListener(this);
				waitForClientRequest();
				
				JFritz.getCallerList().removeListener(this);
				JFritz.getPhonebook().removeListener(this);
			}
		
		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
		ClientConnectionListener.clientConnectionEnded(this);
	}
	
	/**
	 * this function listens for client requests until the 
	 * connection is ended.
	 *
	 */
	public void waitForClientRequest(){
		Object o;
		ClientDataRequest dataRequest;
		ClientActionRequest actionRequest;
		String message;
		
		while(true){
			try{
				
				//currently only call list and phone book update
				//requests are supported
				o = objectIn.readObject();
				Debug.msg("received request from "+remoteAddress);
				if(o instanceof ClientDataRequest){
					
					dataRequest = (ClientDataRequest) o;
					if(dataRequest.destination == ClientDataRequest.Destination.CALLLIST){
					
						if(dataRequest.operation == ClientDataRequest.Operation.GET){
							
							if(dataRequest.timestamp != null){
								Debug.msg("Received call list update request from "+remoteAddress);
								
							}else{
								Debug.msg("Received complete call list request from "+remoteAddress);
								callsAdded(JFritz.getCallerList().getUnfilteredCallVector());
							}
						
						}else if(dataRequest.operation == ClientDataRequest.Operation.ADD && login.allowAddList){
							Debug.msg("Received request to add "+dataRequest.data.size()+" calls from "+remoteAddress);
							synchronized(JFritz.getCallerList()){
								callsAdded = true;
								JFritz.getCallerList().addEntries(dataRequest.data);
								callsAdded = false;
							}
						}else if(dataRequest.operation == ClientDataRequest.Operation.REMOVE && login.allowRemoveList){
							Debug.msg("Received request to remove "+dataRequest.data.size()+" calls from "+remoteAddress);
							synchronized(JFritz.getCallerList()){
								callsRemoved = true;
								JFritz.getCallerList().removeEntries(dataRequest.data);
								callsRemoved = false;
							}
						}
					}else if(dataRequest.destination == ClientDataRequest.Destination.PHONEBOOK){
						
						if(dataRequest.operation == ClientDataRequest.Operation.GET){
							Debug.msg("Received complete phone book request from "+remoteAddress);
							contactsAdded(JFritz.getPhonebook().getUnfilteredPersons());
						}else{
							//TODO:
						}
					}else{
						Debug.msg("Request from "+remoteAddress+" contained no destination, ignoring");
					}
				}else if(o instanceof ClientActionRequest){
					//client has requested to perform an action
					actionRequest = (ClientActionRequest) o;
					if(actionRequest.doLookup && login.allowLookup){
						Debug.msg("Received request to do reverse lookup from "+remoteAddress);
						JFritz.getJframe().doLookupButtonClick();
					}
					if(actionRequest.getCallList && login.allowGetList){
						Debug.msg("Received request to get call from the box from "+remoteAddress);
						JFritz.getJframe().doFetchButtonClick();
					}
					
				}else if(o instanceof String){
					message = (String) o;
					Debug.msg("Received message from client "+remoteAddress+": "+message);
					if(message.equals("JFRITZ CLOSE")){
						Debug.msg("Client is closing the connection, closing this thread");
						disconnect();
					}
					
				}else{
					Debug.msg("Received unexpected object from "+remoteAddress+" ignoring");
				}
			
			
			}catch(ClassNotFoundException e){
				Debug.err("unrecognized class received as request from server");
				Debug.err(e.toString());
				e.printStackTrace();
			}catch(SocketException e){
				if(e.getMessage().equals("Socket closed")){
					Debug.msg("socket for "+remoteAddress+" was closed!");
				}else{
					Debug.err(e.toString());
					e.printStackTrace();
				}
				return;
			}catch(EOFException e){
				Debug.err("client "+remoteAddress+" closed stream unexpectedly");
				Debug.err(e.toString());
				e.printStackTrace();
				return;
			}catch (IOException e){
				Debug.msg("IOException occured reading client request");
				e.printStackTrace();
				return;
			}
	
		}
	}
	
	/**
	 * Authenticate client and record which login client used
	 * logins are used to determine permissions and eventually
	 * filter settings.
	 * 
	 * @return login used by client
	 */
	public Login authenticateClient(){
		
		Object o;
		//Login login;
		String user, password;
		Vector<Login> clientLogins = ClientLoginsTableModel.getClientLogins();
		
		try{
			objectOut.writeObject("JFRITZ SERVER 1.0");
			objectOut.flush();
			for(int i = 0; i < 3; i++){
				o = objectIn.readObject();
				if(o instanceof String){
					user = (String) o;
					o = objectIn.readObject();
					if(o instanceof String){
						password = (String) o;
						for(Login login: clientLogins){
							if(login.getUser().equals(user) &&
									login.getPassword().equals(password)){
								
								objectOut.writeObject("JFRITZ 1.0 OK");
								objectOut.flush();
								objectOut.reset();
								return login;
							}else{
								objectOut.writeObject("JFRITZ 1.0 INVALID");
								objectOut.flush();
								objectOut.reset();
							}
						}
					}else
						Debug.msg("received unexpected object from client: "+o.toString());
					
				}else
					Debug.msg("received unexpected object from client: "+o.toString());
			}

		}catch(ClassNotFoundException e){
			Debug.err("received unrecognized object from client!");
			Debug.err(e.toString());
			e.printStackTrace();
		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
		return null;
	
	}
	/**
	 * Called internally when client signals that it is going to end
	 * the connection. Is sychronized with all other write requests,
	 * so queued writes should still be written out.
	 *
	 */
	private synchronized void disconnect(){
		try{
			objectOut.close();
			objectIn.close();
			socket.close();

		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * called when the user has chosen to kill all network connections
	 *
	 */
	public synchronized void closeConnection(){
		try{
			Debug.msg("Notifying client "+remoteAddress+" to close connection");
			objectOut.writeObject("JFRITZ CLOSE");
			objectOut.flush();
			objectOut.close();
			objectIn.close();
			socket.close();
		}catch(SocketException e){
			Debug.msg("Error closing socket");
			Debug.err(e.toString());
			e.printStackTrace();
		}catch(IOException e){
			Debug.err("Error writing close request to client!");
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	public void writeCompleteCallList(){
		try{
			objectOut.writeObject(JFritz.getCallerList().getUnfilteredCallVector());
		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when new calls have been added to the call list.
	 * Eventually filters based on login will be applied
	 */
	public void callsAdded(Vector<Call> newCalls){
		
		//this thread added calls, no need to write them back
		if(callsAdded){
			callsAdded = false;
			return;
		}
		
		Debug.msg("Notifying client "+remoteAddress+" of added calls, size: "+newCalls.size());
		Vector<Call> filteredCalls = (Vector<Call>) newCalls.clone();
		callsAdd.data.addAll(filteredCalls);
		
		try{
			
			objectOut.writeObject(callsAdd);
			objectOut.flush();
			callsAdd.data.clear();
			objectOut.reset();
			
		}catch(IOException e){
			Debug.err("Error writing new calls to client!");
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when calls have been removed from the call list.
	 * Eventually filters based on login will be applied.
	 */
	public void callsRemoved(Vector<Call> removedCalls){
		
		//this thread removed calls no need to add them back
		if(callsRemoved){
			callsRemoved = false;
			return;
		}
		
		Debug.msg("Notifying client "+remoteAddress+" of removed calls, size:"+removedCalls.size());
		Vector<Call> filteredCalls = (Vector<Call>) removedCalls.clone();
		callsRemove.data.addAll(filteredCalls);
		Debug.msg("callsRemove size "+callsRemove.data.size());
		
		try{
			
			objectOut.writeObject(callsRemove);
			objectOut.flush();
			callsRemove.data.clear();
			objectOut.reset();
			
		}catch(IOException e){
			Debug.err("Error writing removed calls to client!");
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
		Debug.msg("callsRemove new size "+callsRemove.data.size());
	}

	/**
	 * Called when contacts have been added to the call list.
	 * Eventually filters will be applied based on login.
	 * 
	 */
	public void contactsAdded(Vector<Person> newContacts){
		Debug.msg("Notifying client "+remoteAddress+" of added contacts, size:"+newContacts.size());
		Vector<Person> filteredPersons = (Vector<Person>) newContacts.clone();
		contactsAdd.data.addAll(filteredPersons);
		
		try{
			
			objectOut.writeObject(contactsAdd);
			objectOut.flush();
			contactsAdd.data.clear();
			objectOut.reset();
		
		}catch(IOException e){
			Debug.err("Error while writing added contacts to client "+remoteAddress);
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when contacts have been removed from the call list.
	 * Eventually filters will be applied based on login.
	 * 
	 */
	public void contactsRemoved(Vector<Person> removedContacts){
		Debug.msg("Notifying client "+remoteAddress+" of removed contacts, size: "+removedContacts.size());
		Vector<Person> filteredPersons = (Vector<Person>) removedContacts.clone();
		contactsRemove.data.addAll(filteredPersons);
		
		try{
			
			objectOut.writeObject(contactsRemove);
			objectOut.flush();
			contactsRemove.data.clear();
			objectOut.reset();
			
		}catch(IOException e){
			Debug.err("Error while writing removed contacts to client "+remoteAddress);
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
}
