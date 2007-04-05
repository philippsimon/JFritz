package de.moonflower.jfritz.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
					writer = new PrintWriter(socket.getOutputStream(), true);
					reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
					
					Debug.msg("successfully connected to server, authenticating");
					
					if(JFritzProtocolV1.authenticateWithServer(socket, user, password)){
						Debug.msg("Successfully authenticated with server");
						objectIn = new ObjectInputStream(socket.getInputStream());
						objectOut = new ObjectOutputStream(socket.getOutputStream());
						isConnected = true;
						
						//synchronizeWithServer();
						Debug.msg("I made it here!!");
						listenToServer();
						
						
					}else
						Debug.msg("Authentication failed!");
					
				}catch(IOException e){
					Debug.err(e.toString());
					e.printStackTrace();
				}
				
				connect = false;
				
			}
			
			//TODO: Cleanup code here!
			
			
		}
	}
	
	public void synchronizeWithServer(){
		
		Debug.msg("Requesting complete call list from server");
		JFritzProtocolV1.requestCallList(true, writer, objectIn);

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
							//ignoring for now
						}else{
							Debug.msg("destination not chosen for incoming data, ignoring!");
						}
				} else {
					Debug.msg("received unexpected object, ignoring!");
				}
			
			
			}catch(ClassNotFoundException e){
				Debug.err("Response from server contained unkown object!");
				Debug.err(e.toString());
				e.printStackTrace();
			}catch(IOException e){
				Debug.err(e.toString());
				e.printStackTrace();
			}
		}
	}
}
