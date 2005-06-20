/*
 * Created on 24.05.2005
 *
 */
package de.moonflower.jfritz.utils.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.utils.Debug;

/**
 * @author Arno Willig
 *  
 */
public class SyslogListener extends Thread {

	private final String PATTERN_TELEFON = "IncomingCall[^:]*: ID ([^,]*), caller: \"([^\"]*)\" called: \"([^\"]*)\"";
	private DatagramSocket socket;

	public SyslogListener() {
		super();
		start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		startSyslogListener();
	}

	public void startSyslogListener() {
		int port = 4711;
		byte[] log_buffer = new byte[2048];
		int received_messages = 0;
		DatagramPacket packet = new DatagramPacket(log_buffer,
				log_buffer.length);

		try {
			socket = new DatagramSocket(port);
			Debug.msg("Starting SyslogListener on port " + port);
			while (!isInterrupted()) {
				socket.receive(packet);
				received_messages++;
				String msg = new String(packet.getData());
				Debug.msg("Syslog: "+msg);
				Pattern p = Pattern.compile(PATTERN_TELEFON);
				Matcher m = p.matcher(msg);
				if (m.find()) {
					String id = m.group(1);
					String caller = m.group(2);
					String called = m.group(3);
					Debug.msg("NEW CALL " + id + ": " + caller + " -> "
							+ called);

					// POPUP Messages to JFritz
					JFritz.callMsg(caller, called);
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopSyslogListener() {
		interrupt();
		socket.close();
	}
}
