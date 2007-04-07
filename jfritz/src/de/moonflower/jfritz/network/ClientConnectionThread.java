package de.moonflower.jfritz.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

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


public class ClientConnectionThread extends Thread implements CallerListListener,
			PhoneBookListener {

	private Socket socket;
	
	private Login login;
	
	private int count = 0;
	
	private InetAddress remoteAddress;
	
	private PrintWriter writer;
	
	private BufferedReader reader;
	
	private ObjectInputStream objectIn;
	
	private ObjectOutputStream objectOut;
	
	private DataChange<Call> callsAdd, callsRemove;
	
	private DataChange<Person> contactsAdd, contactsRemove;
	
	private boolean quit = false;
	
	public ClientConnectionThread(Socket socket){
		super("Client connection for "+socket.getInetAddress());
		this.socket = socket;
		remoteAddress = socket.getInetAddress();
	}
	
	public void run(){
		
		Debug.msg("Accepted incoming connection from "+remoteAddress);
		//login = JFritzProtocolV1.autheticateClient(socket);
		
		try{
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			Debug.msg("Objout not blocking");
			objectIn = new ObjectInputStream(socket.getInputStream());
			Debug.msg("Objin not blocking");
			
			Debug.msg("Made it here!");
			if((login = authenticateClient()) != null){
			
				Debug.msg("Authentication for client successful!");
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
	
	public void waitForClientRequest(){
		Object o;
		
		while(!quit){
			try{
				
				//callsAdd.data = JFritz.getCallerList().getUnfilteredCallVector();
				//objectOut.writeObject(callsAdd);
				//objectOut.flush();
				//Debug.msg("Wrote out test call data");
				//quit = true;
				o = objectIn.readObject();
			
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
			}
			catch (IOException e){
				Debug.msg("IOException occured reading client request");
				e.printStackTrace();
				break;
			}
	
		}
	}
	
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
	
	public Vector<Call> getCompleteCallList(){
		return JFritz.getCallerList().getUnfilteredCallVector();
	}
	
	public Vector<Person> getCompleteTelephoneBook(){
		return JFritz.getPhonebook().getUnfilteredPersons();
	}
	
	public PrintWriter getPrintWriter(){
		return writer;
	}
	
	public BufferedReader getBufferedReader(){
		return reader;
	}
	
	public void closeConnection(){
		quit = true;
		try{
			socket.close();
		}catch(IOException e){
			Debug.err("Error closing socket!");
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
	
	public void callsAdded(Vector<Call> newCalls){
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
	
	public void callsRemoved(Vector<Call> removedCalls){
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
