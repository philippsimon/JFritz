package de.moonflower.jfritz.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
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

public class JFritzProtocolV1 {

	private static final String JFRITZ_SERVER_VERSION = "JFritz Server 1.0";
	
	private static final String LOGIN_STRING = "login:";
	
	private static final String PASSWORD_STRING ="password:";
	
	private static final String AUTHENTICATE_SUCCESS = "JFRITZ 1.0 OK";
	
	private static final String AUTHENTICATE_FAIL = "login failed";
	
	private static final String UNRECOGNIZED_REQUEST = "JFRITZ 1.0 UNRECOGNIZED REQUEST ERROR";
	
	public static Login autheticateClient(Socket socket){
	
		String user, password;
		Vector<Login> clientLogins = ClientLoginsTableModel.getClientLogins();
		
		try{
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		
			out.println(JFRITZ_SERVER_VERSION);
			for(int i = 0; i < 3; i++){
				out.println(LOGIN_STRING);
				if((user = in.readLine())!= null){

					out.println(PASSWORD_STRING);
					if((password = in.readLine())!= null){

						//Iterate over all logins, find a valid one
						for(Login login : clientLogins){
							if(login.getUser().equals(user) &&
									login.getPassword().equals(password)){
								
								out.println(AUTHENTICATE_SUCCESS);
								return login;
							}
						}
					}
				}
				out.println(AUTHENTICATE_FAIL);
			}
			
		}catch(SocketException e){
			if(e.getMessage().equals("Socket closed"))
				Debug.msg("Socket closed for host");
			else{
				Debug.err(e.toString());
				e.printStackTrace();
			}
		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
			
		return null;
	}
	
	public static boolean authenticateWithServer(Socket socket, String user, String pass){
		
		String inputLine;
		
		try{
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
			Debug.msg(in.readLine());
			
			for(int i = 0; i < 3; i++){
				inputLine = in.readLine();
				if(inputLine != null && inputLine.equals(LOGIN_STRING)){
					out.println(user);
					inputLine = in.readLine();
					if(inputLine != null && inputLine.equals(PASSWORD_STRING)){
						out.println(pass);
						inputLine = in.readLine();
						if(inputLine != null & inputLine.equals(AUTHENTICATE_SUCCESS))
							return true;
						
					}
				}
			}
			
		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
	
		return false;
	
	}
	
	
	public static void processClientRequest(String request, ClientConnectionThread connection){
		String[] parts  = request.split(" ");
		if(parts.length == 3){
			if(parts[0].equals("GET")){
				if(parts[1].equals("CALLLIST")){
					if(parts[2].equals("ALL")){
						connection.writeCompleteCallList();
					}
				}else if(parts[1].equals("TELEPHONEBOOK")){
					Vector<Person> contacts;
					if(parts[2].equals("ALL")){
						contacts = connection.getCompleteTelephoneBook();

					}
				}
			}
		}
	}
	
	public static void requestCallList(boolean complete, PrintWriter writer,
			ObjectInputStream objectIn){
		
		try{
			if(complete){
				writer.println("GET CALLLIST ALL");
				Object o = objectIn.readObject();
				if(o instanceof Vector){
					Vector<Call> v = (Vector<Call>) o;
					Debug.msg("Complete call list received from server, size: "+v.size());
					JFritz.getCallerList().addEntries(v);
				}else if(o instanceof String){
					String response = (String) o;
					if(response.equals(UNRECOGNIZED_REQUEST))
						Debug.msg("Server rejected Call list request!!");
				}else
					Debug.msg("Server sent an unrecognized response!");
			}
		
		
		}catch(IOException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			Debug.err(e.toString());
			e.printStackTrace();
		}
		
	}
	
	
}
