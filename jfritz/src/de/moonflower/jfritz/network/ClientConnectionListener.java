package de.moonflower.jfritz.network;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.util.Vector;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.utils.Debug;

public class ClientConnectionListener extends Thread {

	private static Object lock = new Object();
	
	private static boolean isListening = false;
	
	private static boolean listen = false;
	
	private static Vector<ClientConnectionThread> connectedClients;
	
	private static ServerSocket serverSocket;
	
	public void run(){
		Debug.msg("client connection listener started");
		connectedClients = new Vector<ClientConnectionThread>();
		while(true){
			if(!listen){
				try{
					synchronized(this){
						wait();
					}
				
				}catch(InterruptedException e){
					Debug.err("Sever thread was interuppted!");
				}
			}else {
			
				try{
					serverSocket = new ServerSocket(Integer.parseInt(
							Main.getProperty("clients.port", "4444")));
					
					Debug.msg("Listening for client connections on: "+
							Main.getProperty("clients.port", "4444"));
					isListening = true;
			
					while(listen){
						
						ClientConnectionThread connection = new ClientConnectionThread(serverSocket.accept());
						
						synchronized(lock){
							connectedClients.add(connection);
						}
						
						connection.start();
					}
					
					isListening = false;
					serverSocket.close();
					
				}catch(SocketException e){
					if(e.getMessage().equals("Socket closed"))
						Debug.msg("Server socket closed!");
					else{
						Debug.err(e.toString());
						e.printStackTrace();
					}
						
				}catch(IOException e){
					Debug.err("Error binding to port:");
					Debug.err(e.toString());
					e.printStackTrace();
				}
				Debug.msg("Client connection listener stopped");
			}
		}
		
	}
	
	public static boolean isListening(){
		return isListening;
	}

	public synchronized void startListening(){
		listen = true;
		notify();
	}

	public static void clientConnectionEnded(ClientConnectionThread connection){
		synchronized(lock){
			connectedClients.remove(connection);
		}
	}
	
	public void stopListening(){
		listen = false;
		try{
			serverSocket.close(); 
		
		}catch(IOException e){
			Debug.err("Error closing server socket");
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
		synchronized(lock){
			for(ClientConnectionThread client: connectedClients)
				client.closeConnection();
		}
	}
	
}
