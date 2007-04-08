package de.moonflower.jfritz.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;

import java.util.Vector;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.utils.Debug;

/**
 * This class is responsible for seting up a connection to a 
 * JFritz server. All communication between server and client
 * are asynchronus in nature. All communication from client to server
 * uses either ClientRequest or String objects, whereas the String objects
 * are intended only to pass messages to the server (like client closing
 * the connection). All communication from server to client uses either
 * DataChange or String objects, where the String objects are also
 * used ot pass messages.
 * 
 * @author brian
 *
 */
public class ServerConnectionThread extends Thread {

	private static boolean isConnected = false;
	
	private static boolean connect = false;
	
	private Socket socket;
	
	private ObjectInputStream objectIn;
	
	private ObjectOutputStream objectOut;
	
	private ClientRequest<Call> callListRequest;
	
	private ClientRequest<Person> phoneBookRequest;
	
	private boolean quit = false;
	
	/**
	 * Returns the current state of this thread
	 * 
	 * @return the state of the connection to the server
	 */
	public static boolean isConnected(){
		return isConnected;
	}
	
	/**
	 * Starts the thread and attempts to build a connection to the 
	 * user specified server
	 *
	 */
	public synchronized void connectToServer(){
		connect = true;
		notify();
	}
	
	/**
	 * Is used to cleanly kill a connection and put the current
	 * thread into sleep mode
	 *
	 */
	public synchronized void disconnectFromServer(){
		try{
			Debug.msg("Writing disconnect message to the server");
			objectOut.writeObject("JFRITZ CLOSE");
			objectOut.flush();
			objectOut.close();
			objectIn.close();
			socket.close();
		}catch(IOException e){
			Debug.err("Error writing disconnect message to server");
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(!quit){
			if(!connect){
				try{
					synchronized(this){
						wait();
					}
				}catch(InterruptedException e){
					Debug.err("SeverConnection Thread was interrupted!");
				}
			}else{
				String server, user, password;
				int port;
				
				server = Main.getProperty("server.name", "");
				port = Integer.parseInt(Main.getProperty("server.port", "0"));
				user = Main.getProperty("server.login", "");
				password = Main.getProperty("server.password", "");
				
				Debug.msg("Attempting to connect to server");
				Debug.msg("Server: "+ server);
				Debug.msg("Port: "+port);
				Debug.msg("User: "+user);
				Debug.msg("Pass: "+password);
				
				try{
					socket = new Socket(server, port);
					Debug.msg("successfully connected to server, authenticating");
					objectOut = new ObjectOutputStream(socket.getOutputStream());
					objectIn = new ObjectInputStream(socket.getInputStream());

					if(authenticateWithServer(user, password)){
						Debug.msg("Successfully authenticated with server");
						isConnected = true;
						NetworkStateMonitor.clientStateChanged();
						
						callListRequest = new ClientRequest<Call>();
						callListRequest.destination = ClientRequest.Destination.CALLLIST;
						
						phoneBookRequest = new ClientRequest<Person>();
						phoneBookRequest.destination = ClientRequest.Destination.PHONEBOOK;
						
						synchronizeWithServer();
						listenToServer();
						isConnected = false;
						NetworkStateMonitor.clientStateChanged();
						
					}else
						Debug.msg("Authentication failed!");
					
					objectOut.close();
					objectIn.close();
					socket.close();
					
				}catch(IOException e){
					Debug.err(e.toString());
					e.printStackTrace();
				}
				
				connect = false;
				
			}
			
			Debug.msg("Connection to server closed");
			//TODO: Cleanup code here!
		}
	}
	
	/**
	 * function attempts to login to the user specified server
	 * 
	 * @param user username of the account on the server
	 * @param password password of the account on the server
	 * @return whether the client successfully connected to the server or not
	 */
	private boolean authenticateWithServer(String user, String password){
		Object o;
		String response;
		try{
			o = objectIn.readObject();
			if(o instanceof String){
				
				response = (String) o;
				Debug.msg("Connected to JFritz Server: "+response);
				
				for(int i=0; i < 3; i++){
					objectOut.writeObject(user);
					objectOut.writeObject(password);
					objectOut.flush();
					o = objectIn.readObject();
					
					if(o instanceof String){
						response = (String) o;
						if(response.equals("JFRITZ 1.0 OK"))
							return true;
						else if(response.equals("JFRITZ 1.0 INVALID"))
							Debug.msg("login attempt refused by server");
						else
							Debug.msg("unrecognized response from server: "+response);
					}else
						Debug.msg("unexpected object received from server: "+o.toString());
					
				}
			}else
				Debug.msg("Server identification invalid, canceling login attempt: "+o.toString());
			
		}catch(ClassNotFoundException e){
			Debug.err("Server authentication response invalid!");
			Debug.err(e.toString());
			e.printStackTrace();
		}catch(EOFException e){
			Debug.err("Server closed Stream unexpectedly!");
			Debug.err(e.toString());
			e.printStackTrace();
		}catch(IOException e){
			Debug.err("Error reading response during authentication!");
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * function gets all calls newer than the newest call in the call list
	 * and gets all contacts from the server.
	 *
	 */
	private synchronized void synchronizeWithServer(){

		Debug.msg("Requesting updates from server");
		try{
			callListRequest.operation = ClientRequest.Operation.GET;
			callListRequest.timestamp = JFritz.getCallerList().getLastCallDate();
			objectOut.writeObject(callListRequest);
			objectOut.flush(); 
			objectOut.reset(); //reset the streams object cache!
			
			phoneBookRequest.operation = ClientRequest.Operation.GET;
			objectOut.writeObject(phoneBookRequest);
			objectOut.flush();
			objectOut.reset();
			
		}catch(IOException e){
			Debug.err("Error writing synchronizing request to server!");
			Debug.err(e.toString());
			e.printStackTrace();
		}

	}
	
	/**
	 * function listens to commands issued by the server, can only
	 * be exited by closing the object streams or receiving a 
	 * close request from the server
	 *
	 */
	private void listenToServer(){
		Vector<Call> vCalls;
		Vector<Person> vPersons;
		DataChange change;
		Object o;
		String message;
		
		Debug.msg("Listening for commands from server");
		while(true){
			try{
				o = objectIn.readObject();
				if(o instanceof DataChange){
				
					change = (DataChange) o;
						if(change.destination == DataChange.Destination.CALLLIST){
							if(change.operation == DataChange.Operation.ADD){
								vCalls = (Vector<Call>) change.data;
								Debug.msg("Received request to add "+vCalls.size()+" calls");
								JFritz.getCallerList().addEntries(vCalls);
							}else if(change.operation == DataChange.Operation.REMOVE){
								vCalls = (Vector<Call>) change.data;
								Debug.msg("Received request to remove "+vCalls.size()+" calls");
								JFritz.getCallerList().removeEntries(vCalls);
							}else{
								Debug.msg("Operation not chosen for incoming data, ignoring!");
							}
				
						}else if(change.destination == DataChange.Destination.PHONEBOOK){
							if(change.operation == DataChange.Operation.ADD){
								vPersons = (Vector<Person>) change.data;
								Debug.msg("Received request to add "+vPersons.size()+" contacts");
								JFritz.getPhonebook().addEntries(vPersons);
							}else if(change.operation == DataChange.Operation.REMOVE){
								vPersons = (Vector<Person>) change.data;
								Debug.msg("Received request to remove "+vPersons.size()+" contacts");
								JFritz.getPhonebook().removeEntries(vPersons);
							}else{
								Debug.msg("Operation not chosen for incoming data, ignoring");
							}
						}else{
							Debug.msg("destination not chosen for incoming data, ignoring!");
						}
				}else if(o instanceof String){ //message received from the server
					message = (String) o;
					Debug.msg("Received message from server: "+message);
					if(message.equals("JFRITZ CLOSE")){ 
						Debug.msg("Closing connection with server!");
						disconnect();
						return;
					} //TODO: Add other messages here if necessary
					
				
				}else {
					Debug.msg(o.toString());
					Debug.msg("received unexpected object, ignoring!");
				}
			
			}catch(ClassNotFoundException e){
				Debug.err("Response from server contained unkown object!");
				Debug.err(e.toString());
				e.printStackTrace();
			}catch(SocketException e){
				if(e.getMessage().equals("Socket closed")){
					Debug.msg("Socket closed");	//we closed the socket as requested by the user
				}else{
					Debug.err(e.toString());
					e.printStackTrace();
				}
				return;
			}catch(EOFException e ){
				Debug.err("Server closed stream unexpectedly!");
				Debug.err(e.toString());
				e.printStackTrace();
				return;
			}catch(IOException e){
				Debug.err(e.toString());
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Called when the server send a close request. This code makes sure that
	 * we aren't writing a request to the server as the streams are closed
	 *
	 */
	private synchronized void disconnect(){
		try{
			objectOut.close();
			objectIn.close();
			socket.close();
		}catch(IOException e){
			Debug.err("Error disconnecting from server");
			Debug.err(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Function used to quit this thread, should be called on application exit
	 *
	 */
	public synchronized void quitThread(){
		quit = true;
		notify();
	}
	
}
