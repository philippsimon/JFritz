/*
 * Created on 16.07.2005
 *
 */
package de.moonflower.jfritz.utils.network;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.ConnectException;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.dialogs.config.TelnetConfigDialog;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.firmware.FritzBoxFirmware;

import org.apache.commons.net.telnet.*;

/**
 * Manages Telnetfunctions: Connect, Login, Disconnect, sendCommand
 * 
 * @author rob
 *  
 */
public class Telnet {

	private JFritz jfritz;

	private boolean connected = false;

	private TelnetClient telnet;

	private InputStream in;

	private PrintStream out;

	private final char prompt = '#';

	private static final int LOGIN_OK = 0;

	private static final int LOGIN_CANCELED = 1;

	public Telnet(JFritz jfritz) {
		this.jfritz = jfritz;
		telnet = new TelnetClient();
	}

	/**
	 * Connects to FritzBox-IP
	 * 
	 * TODO: Einbau der Abfrage der IP, User, Passwort, wenn keine Verbindung
	 * aufgebaut werden kann.
	 */
	public void connect() {
		boolean isdone = false;
		int connectionFailures = 0;
		while (!isdone) {
			String server = JFritz.getProperty("box.address");

			String password;
			if (JFritz.getProperty("telnet.password", "").equals("")) {
				// Noch kein Passwort gesetzt. Zeige Einstellungsdialog
				TelnetConfigDialog telnetConfigDialog = new TelnetConfigDialog(
						jfritz.getJframe(), jfritz);
				telnetConfigDialog.setModal(true);
				if (telnetConfigDialog.showConfigDialog() == TelnetConfigDialog.CANCEL_OPTION) {
					//ABBRUCH
					return;
				}
			}

			String user = JFritz.getProperty("telnet.user", "");
			password = Encryption
					.decrypt(JFritz.getProperty("telnet.password"));
			int port = 23;
			try {
				Debug.msg("Verbinde mit Telnet ...");
				if (jfritz.getJframe() != null) {
					jfritz.getJframe().setStatus("Verbinde mit Telnet ...");
				}
				telnet.connect(server, port); // Connect to the specified server
				in = telnet.getInputStream();
				out = new PrintStream(telnet.getOutputStream());
				if (login(user, password) == LOGIN_OK) {
					connected = true;
				}
				isdone = true;
				Debug.msg("Done");
			} catch (ConnectException e) { // Connection Timeout
				Debug.msg("Telnet connection timeout ...");
				// Warten, falls wir von einem Standby aufwachen,
				// oder das Netzwerk tempor�r nicht erreichbar ist.
				if (connectionFailures < 5) {
					Debug.msg("Waiting for FritzBox, retrying ...");
					connectionFailures++;
				} else {
					Debug.msg("FritzBox not found. Get new IP ...");
					jfritz.getJframe().setStatus(
							JFritz.getMessage("box_not_found"));
					Debug.err("Address wrong!");
					jfritz.getJframe().setBusy(false);
					String box_address = jfritz.getJframe().showAddressDialog(
							JFritz.getProperty("box.address", "fritz.box"));
					if (box_address == null) {
						jfritz.stopCallMonitor();
						isdone = true;
					} else {
						JFritz.setProperty("box.address", box_address);
					}
				}
			} catch (Exception e) {
				System.err.println("Error in Class Telnet");
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * login on a telnet session with user and password
	 * 
	 * @param user
	 * @param password
	 */

	private int login(String user, String password) {
		try {
			Debug.msg("Login to Telnet");
			String login = "ogin: ";
			String passwd = "assword: ";
			boolean firstLogin = true;
			boolean firstPassword = true;
			char lastCharLogin = login.charAt(login.length() - 1);
			char lastCharPasswd = passwd.charAt(passwd.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			while (true) {
				sb.append(ch); // FIXME This can be done better!!!
				if (ch == lastCharLogin || ch == lastCharPasswd || ch == prompt) {
					if (sb.toString().endsWith(login)) {
						// wenn Fehlgeschlagen, dann
						// mehrmaliges Login mit falschem
						// Username verhindern
						if (firstLogin) {
							Debug.msg("Writing Telnet User: " + user);
							write(user);
							firstLogin = false;
						} else {
							TelnetConfigDialog telnetConfigDialog = new TelnetConfigDialog(
									jfritz.getJframe(), jfritz);
							telnetConfigDialog.setModal(true);
							if (telnetConfigDialog.showConfigDialog() == TelnetConfigDialog.CANCEL_OPTION) {
								//ABBRUCH
								jfritz.stopCallMonitor();
								return LOGIN_CANCELED;
							}
						}
					}
					if (sb.toString().endsWith(passwd)) {
						// schauen, ob WebPasswort abgefragt wird
						if (sb.toString().endsWith("web password: ")) {
							password = Encryption.decrypt(JFritz
									.getProperty("box.password"));

							while (true) { // test WebPassword
								try {
									FritzBoxFirmware
											.detectFirmwareVersion(
													JFritz
															.getProperty("box.address"),
													Encryption
															.decrypt(JFritz
																	.getProperty("box.password")));
									password = Encryption.decrypt(JFritz
											.getProperty("box.password"));
									break; // go on with telnet login
								} catch (WrongPasswordException e1) {
									Debug.err("Password wrong!");
									jfritz
											.getJframe()
											.setStatus(
													JFritz
															.getMessage("password_wrong"));
									jfritz.getJframe().setBusy(false);

									String newPassword = jfritz
											.getJframe()
											.showPasswordDialog(
													Encryption
															.decrypt(JFritz
																	.getProperty(
																			"box.password",
																			"")));
									System.out
											.println("OLD PASS: "
													+ Encryption
															.decrypt(JFritz
																	.getProperty(
																			"box.password",
																			"")));
									if (newPassword == null) { // Dialog aborted
										jfritz.stopCallMonitor();
										return LOGIN_CANCELED;
									} else {
										JFritz
												.setProperty(
														"box.password",
														Encryption
																.encrypt(newPassword));
									}
								}
							}
						}
						// wenn Fehlgeschlagen, dann
						// mehrmaliges Login mit falschem
						// Passwort verhindern
						if (firstPassword) {
							Debug.msg("Writing Telnet Password: " + password);
							write(password);
							firstPassword = false;
						} else {
							TelnetConfigDialog telnetConfigDialog = new TelnetConfigDialog(
									jfritz.getJframe(), jfritz);
							telnetConfigDialog.setModal(true);
							if (telnetConfigDialog.showConfigDialog() == TelnetConfigDialog.CANCEL_OPTION) {
								//ABBRUCH
								return LOGIN_CANCELED;
							}
						}
					}
					if (ch == prompt) {
						System.err.println(sb.toString());
						break;
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			Debug.err(e.getMessage());
		}
		Debug.msg("Logged into Telnet connection.");
		return LOGIN_OK;
	}

	/**
	 * gets all Data until a pattern is reached
	 * 
	 * @param pattern
	 * @return data read until pattern
	 */
	public String readUntil(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			while (true) {
				sb.append(ch); // FIXME This can be done better!!!
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			Debug.err(e.getMessage());
		}
		return null;
	}

	/**
	 * Write value to telnet-session
	 * 
	 * @param value
	 */
	public void write(String value) {
		try {
			out.println(value);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * writes command on telnet session and waits till prompt
	 * 
	 * @param command
	 * @return data read until prompt
	 */
	public String sendCommand(String command) {
		try {
			write(command);
			String data = readUntil(prompt + " ");
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return telnetIsConnected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Disconnects Telnet from FritzBox
	 *  
	 */
	public void disconnect() {
		Debug.msg("Disconnect Telnet connection.");
		try {
			telnet.disconnect();
			connected = false;
		} catch (Exception e) {
		}
	}

}
