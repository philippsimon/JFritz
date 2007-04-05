package de.moonflower.jfritz.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import java.util.Vector;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.utils.Debug;


public class ClientConnectionThread extends Thread {

	private Socket socket;
	
	private Login login;
	
	private int count = 0;
	
	private InetAddress remoteAddress;
	
	private PrintWriter writer;
	
	private BufferedReader reader;
	
	private ObjectInputStream objectIn;
	
	private ObjectOutputStream objectOut;
	
	private DataChange<Call> callsAdd, callsRemove;
	
	private DataChange<Person> personsAdd, personsRemove;
	
	private boolean quit = false;
	
	public ClientConnectionThread(Socket socket){
		super("Client connection for "+socket.getInetAddress());
		this.socket = socket;
		remoteAddress = socket.getInetAddress();
	}
	
	public void run(){
		
		Debug.msg("Accepted incoming connection from "+remoteAddress);
		//login = JFritzProtocolV1.autheticateClient(socket);

		
		//Login was successful, create a connection thread
		if(login != null){
			Debug.msg("Client authenication successful for "+socket.getInetAddress());
			

			try{
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				
				callsAdd.destination = DataChange.Destination.CALLLIST;
				callsAdd.operation = DataChange.Operation.ADD;
				callsRemove.destination = DataChange.Destination.CALLLIST;
				callsRemove.operation = DataChange.Operation.REMOVE;
				
				personsAdd.destination = DataChange.Destination.PHONEBOOK;
				personsAdd.operation = DataChange.Operation.ADD;
				personsRemove.destination = DataChange.Destination.PHONEBOOK;
				personsRemove.operation = DataChange.Operation.REMOVE;
				
				Debug.msg("Begin processing request for "+remoteAddress);
				waitForClientRequest();
				
				writer.close();
				reader.close();
				socket.close();
				
			}catch(IOException e){
				Debug.err(e.toString());
				e.printStackTrace();
			}
			
		}else{
			Debug.msg("Authentication for host "+socket.getInetAddress()+"Failed!");
			try{
				socket.close();
			}catch(IOException e){
				Debug.err("Error closing socket!");
			}
		}
		
		ClientConnectionListener.clientConnectionEnded(this);
	}
	
	public void waitForClientRequest(){
		String request;

		
		
		while(!quit){
			try{
				
				callsAdd.data = JFritz.getCallerList().getUnfilteredCallVector();
				objectOut.writeObject(callsAdd);
				quit = true;
			
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
	
	public boolean authenticateClient(){
		
		
		
		String lineIn, lineOut;
		
		try{
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());

		}catch(IOException e){
			
		}
		
		return true;
	
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
	
}
