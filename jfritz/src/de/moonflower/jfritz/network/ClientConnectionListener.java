package de.moonflower.jfritz.network;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.SocketException;

import java.util.Vector;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.utils.Debug;

/**
 * This class is responsible for listening for new client connections
 * on the user specified port. On an incoming connection this thread
 * creates a new ClientConnectionThread to handle the communication
 * with the new client.

 * @author brian
 *
 */
public class ClientConnectionListener extends Thread {
	
	private static boolean isListening = false;
	
	private static boolean listen = false;
	
	private static Vector<ClientConnectionThread> connectedClients;
	
	private static ServerSocket serverSocket;
	
	private static boolean quit = false;
	
	public void run(){
		Debug.msg("client connection listener started");
		connectedClients = new Vector<ClientConnectionThread>();
		while(!quit){
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
					NetworkStateMonitor.serverStateChanged();
					
					while(listen){
						
						if(Integer.parseInt(Main.getProperty("max.connections", "1")) <= connectedClients.size()){

							synchronized(this){
								try{
									wait();
								}catch(InterruptedException e){
									Debug.err("client listener interrupted while waiting for connection to close!");
								}
							}
						}else{
							ClientConnectionThread connection = new ClientConnectionThread(serverSocket.accept(), this);
							
							synchronized(this){
								connectedClients.add(connection);
							}
							
							connection.start();
						}
					}
					
					serverSocket.close();
					
				}catch(SocketException e){
					if(e.getMessage().equals("Socket closed"))
						Debug.msg("Server socket closed!");
					else{
						Debug.err(e.toString());
						e.printStackTrace();
					}
						
				}catch(IOException e){
					Debug.errDlg(Main.getMessage("error_binding_port"));
					Debug.err("Error binding to port: "+Main.getProperty("clients.port", "4444"));
					Debug.err(e.toString());
					e.printStackTrace();
				}
				Debug.msg("Client connection listener stopped");
				isListening = false;
				listen = false;
				
				Debug.msg("Closing all open client connections");
				synchronized(this){
					for(ClientConnectionThread client: connectedClients)
						client.closeConnection();
				}
				
				NetworkStateMonitor.serverStateChanged();
			}
		}
		Debug.msg("ClientConnectionListener thread quitting");
		
	}
	
	/** Used by the gui code to determine if the server is listening
	 * 
	 * @return status of the client listener thread
	 */
	public static boolean isListening(){
		return isListening;
	}

	/**
	 * Wakes the thread up which proceeds to open a serverSocket
	 * connection to listen for new incoming connections.
	 *
	 */
	public synchronized void startListening(){
		listen = true;
		notify();
	}
	
	/**
	 * This function is called by  a ClientConnectionThread to indicate
	 * that a particular connection has ended
	 * 
	 * @param connection that has ended
	 */
	public synchronized void clientConnectionEnded(ClientConnectionThread connection){
			connectedClients.remove(connection);
			notify();
	}
	
	/**
	 * This function stops the ClientConnectionListioner and puts the
	 * thread to sleep 
	 *
	 */
	public synchronized void stopListening(){
		listen = false;
		Debug.msg("Stopping client listener!");
		try{
			serverSocket.close(); 
		
		}catch(IOException e){
			Debug.err("Error closing server socket");
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
		notify();
	}
	
	/**
	 * This function must be called to terminate the thread when the
	 * application is going to terminate
	 *
	 */
	public synchronized void quitThread(){
		quit = true;
		notify();
	}
	
}
