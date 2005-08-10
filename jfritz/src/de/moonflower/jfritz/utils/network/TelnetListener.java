package de.moonflower.jfritz.utils.network;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.network.Telnet;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.JOptionPane;

/**
 * Thread. Logon on FritzBox via Telnet. Captures Callermessages via Telnet.
 * 
 * @author Arno Willig
 *  
 */

public class TelnetListener extends Thread implements CallMonitor {

	//IncomingCall: ID 0, caller: "017623352711" called: "592904"
	//IncomingCall from NT: ID 0, caller: "592904" called: "1815212"
	private final String PATTERN_TELEFON = "IncomingCall[^:]*: ID ([^,]*), caller: \"([^\"]*)\" called: \"([^\"]*)\"";

	//private final String PATTERN_VOIP_REQUEST = ">>> Request: INVITE ([^\\n]*)";

	//private final String PATTERN_VOIP_CALLTO_ESTABLISHED = "call to ([^ ]*) established";

	//private final String PATTERN_VOIP_CALLTO_TERMINATED = "call to ([^ ]*) terminated";

	//private final String PATTERN_VOIP_CALLTO_DISCONNECTED = "disconnected\\([^)]*\\):";

	private Telnet telnet;

	private boolean isRunning = false;

	private JFritz jfritz;

	public TelnetListener(JFritz jfritz) {
		// Fetch new calls
		this.jfritz = jfritz;
		telnet = new Telnet(jfritz);
		Debug.msg("Starting TelnetListener");
		telnet.connect();
		if (telnet.isConnected()) {
			start();
		}
	}

	public void run() {
		Debug.msg("run()");
		if (JOptionPane
				.showConfirmDialog(
						null,
						"Der telefond muss neu gestartet werden.\n"
								+ "Dabei wird ein laufendes Gespr�ch unterbrochen. Die Anrufliste wird vorher gesichert.\n"
								+ "Soll der telefond neu gestartet werden?",
						JFritz.PROGRAM_NAME, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			jfritz.getJframe().getFetchButton().doClick();
			restartTelefonDaemon();
			parseOutput();
		}
		else {
			jfritz.stopCallMonitor();
		}
	}

	private void restartTelefonDaemon() {
		telnet.write("killall telefon");
		telnet.readUntil("# ");
		telnet.readUntil("# ");
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			Debug.err("Fehler beim Schlafen: " + e);
		}
		telnet.write("telefon &>&1 &");
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			Debug.err("Fehler beim Schlafen: " + e);
		}
		Debug.msg("Telefon Daemon restarted.");
		JFritz.setProperty("telefond.laststarted", "telnetMonitor");
	}

	public void parseOutput() {
		isRunning = true;
		try {
			String currentLine = "";
			while (isRunning) {
				currentLine = telnet.readUntil("\n");
				Pattern p = Pattern.compile(PATTERN_TELEFON);
				Matcher m = p.matcher(currentLine);
				if (m.find()) {
					String id = m.group(1);
					String caller = m.group(2);
					String called = m.group(3);
					Debug.msg("NEW CALL " + id + ": " + caller + " -> "
							+ called);

					jfritz.callInMsg(caller, called);
					if (!isRunning)
						break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
		}
		telnet.disconnect();
	}

	public void stopCallMonitor() {
		Debug.msg("Stopping TelnetListener");
		isRunning = false;
	}
}