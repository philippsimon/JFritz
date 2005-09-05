package de.moonflower.jfritz.utils.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.io.DataOutputStream;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.utils.Debug;

/**
 * Thread, listens on a TCP-Port on Callmessage messages format:
 * 
 * @CALLname (number) or: message
 * 
 * @author Robert Palmer
 *  
 */
public class CallmessageListener extends Thread implements CallMonitor {

	private boolean isRunning = false;

	private int port;

	private JFritz jfritz;

	private ServerSocket serverSocket;

	public CallmessageListener(JFritz jfritz) {
		super();
		this.jfritz = jfritz;
		start();
		port = 23232;
	}

	public CallmessageListener(JFritz jfritz, int port) {
		super();
		this.jfritz = jfritz;
		start();
		this.port = port;
	}

	public void run() {
		startCallmessageListener();
	}

	public void startCallmessageListener() {
		isRunning = true;
		Debug.msg("Starting Callmessage-Monitor on Port " + port);
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			try {
				synchronized (this) {
					wait(5000);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			jfritz.stopCallMonitor();
		}
		while (isRunning) {
			try {
				// Client-Connection accepten, Extra-Socket �ffnen
				Socket connection = serverSocket.accept();
				// Eingabe lesen
				BufferedReader input = new BufferedReader(
						new InputStreamReader(connection.getInputStream(),
								"ISO-8859-1"));
				String msg = input.readLine();
				msg = URLDecoder.decode(msg, "ISO-8859-1");
				msg = msg.substring(5, msg.length() - 9);
				Debug.msg("Got message from callmessageMonitor: " + msg);

				if (msg.startsWith("@")) {
					// Call
					// Format: @NAME (NUMBER)
					// NAME: Name, ".." or "unbekannt"
					// NUMBER: Number or "Keine Rufnummer �bermittelt"
					// @unbekannt (01798279574)
					// @.. (Keine Rufnummer ?bermittelt)
					msg = msg.substring(1); // Entferne @
					String name = "";
					String number = "";
					String splitted[] = msg.split(" ", 2);

					if (splitted.length == 0) {
						Debug.msg("Split length 0");
						Debug.msg(splitted[0]);
					} else if (splitted.length == 1) {
						Debug.msg("Split length 1");
						name = splitted[0];
					} else if (splitted.length == 2) {
						Debug.msg("Split length 2");
						name = splitted[0];
						number = splitted[1];
						number = number.replaceAll("\\(", "");
						number = number.replaceAll("\\)", "");
					}
					if (name.equals("..") || name.equals("unbekannt")) {
						name = "";
					}
					if (number.equals("Keine Rufnummer �bermittelt")) {
						number = "";
					}
					jfritz.callInMsg(number, "", name);
				} else {
					// Message
					JFritz.infoMsg(JFritz.getMessage("yac_message") + ":\n"
							+ msg);
				}

				// No Content ausgeben, Client rauswerfen
				DataOutputStream output = new DataOutputStream(connection
						.getOutputStream());
				output.writeBytes("HTTP/1.1 204 No Content");
				connection.close();
			} catch (SocketException e) {
				Debug.err("SocketException: " + e);
				if (!e.toString().equals("java.net.SocketException: socket closed")) {
					jfritz.stopCallMonitor();
				}
			} catch (Exception e) {
				JFritz.infoMsg("Exception " + e);
				Debug.msg("CallmessageListener: Exception " + e);
				jfritz.stopCallMonitor();
				isRunning = false;
				//				break;
			}
		}
	}

	public void stopCallMonitor() {
		Debug.msg("Stopping CallmessageListener");
		try {
			if (serverSocket != null)
				serverSocket.close();
		} catch (Exception e) {
			Debug.msg("Fehler beim Schliessen des Sockets");
		}
		isRunning = false;
	}

}
