package de.moonflower.jfritz.network;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.ServerSocket;

import java.util.Vector;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.utils.Debug;

public class ServerConnectionThread extends Thread {

	private static boolean isConnected = false;
	
	private static boolean connect = false;
	
	private Socket socket;
	
	private PrintWriter writer;
	
	private BufferedReader reader;
	
	private ObjectInputStream objectIn;
	
	private ObjectOutputStream objectOut;
	
	private boolean quit = false;
	
	public static boolean isConnected(){
		return isConnected;
	}
	
	public synchronized void connectToServer(){
		connect = true;
		notify();
	}
	
	public void run(){
		while(true){
			Debug.msg("Server connection thread started");
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
					//writer = new PrintWriter(socket.getOutputStream(), true);
					//reader = new BufferedReader(new InputStreamReader(
					//	socket.getInputStream()));
					
					Debug.msg("successfully connected to server, authenticating");
					objectOut = new ObjectOutputStream(socket.getOutputStream());
					objectIn = new ObjectInputStream(socket.getInputStream());

					if(authenticateWithServer(user, password)){
						Debug.msg("Successfully authenticated with server");
						isConnected = true;
						
						//synchronizeWithServer();
						listenToServer();
						
						
					}else
						Debug.msg("Authentication failed!");
					
					isConnected = false;
					objectOut.close();
					objectIn.close();
					socket.close();
					
				}catch(IOException e){
					Debug.err(e.toString());
					e.printStackTrace();
				}
				
				connect = false;
				
			}
			
			//TODO: Cleanup code here!
			
			
		}
	}
	
	public boolean authenticateWithServer(String user, String password){
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
	
	public void synchronizeWithServer(){

	}
	

	private void listenToServer(){
		Vector<Call> vCalls;
		Vector<Person> vPersons;
		DataChange change;
//		DataChange<Call> cCall;
//		DataChange<Person> cPerson;
		Object o;
		
		Debug.msg("Listening for commands from server");
		while(!quit){
			try{
				o = objectIn.readObject();
				Debug.msg("received response from server!");
				if(o != null && o instanceof DataChange){
				
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
				} else {
					Debug.msg(o.toString());
					Debug.msg("received unexpected object, ignoring!");
				}
			
			
			}catch(ClassNotFoundException e){
				Debug.err("Response from server contained unkown object!");
				Debug.err(e.toString());
				e.printStackTrace();
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
}
