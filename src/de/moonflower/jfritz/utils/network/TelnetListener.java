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

    // IncomingCall: ID 0, caller: "017623352711" called: "592904"
    // IncomingCall from NT: ID 0, caller: "592904" called: "1815212"
    private final String PATTERN_TELEFON = "IncomingCall[^:]*: ID ([^,]*), caller: \"([^\"]*)\" called: \"([^\"]*)\""; //$NON-NLS-1$

    private Telnet telnet;

    private boolean isRunning = false;

    private JFritz jfritz;

    public TelnetListener(JFritz jfritz) {
        super();
        this.jfritz = jfritz;
        start();

    }

    public void run() {
        telnet = new Telnet(jfritz);
        Debug.msg("Starting TelnetListener"); //$NON-NLS-1$
        telnet.connect();
        if (telnet.isConnected()) {
            Debug.msg("run()"); //$NON-NLS-1$
            if (JOptionPane
                    .showConfirmDialog(
                            null,
                            "Der telefond muss neu gestartet werden.\n" // TODO: I18N
                                    + "Dabei wird ein laufendes Gespr�ch unterbrochen. Die Anrufliste wird vorher gesichert.\n" // TODO: I18N
                                    + "Soll der telefond neu gestartet werden?", // TODO: I18N
                            JFritz.PROGRAM_NAME, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                jfritz.getJframe().getFetchButton().doClick();
                restartTelefonDaemon();
                parseOutput();
            } else {
                jfritz.stopCallMonitor();
            }
        } else
            jfritz.stopCallMonitor();

    }

    private void restartTelefonDaemon() {
        telnet.write("killall telefon"); //$NON-NLS-1$
        telnet.readUntil("# "); //$NON-NLS-1$
        telnet.readUntil("# "); //$NON-NLS-1$
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            Debug.err("Fehler beim Schlafen: " + e); //$NON-NLS-1$
        }
        telnet.write("telefon &>&1 &"); //$NON-NLS-1$
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            Debug.err("Fehler beim Schlafen: " + e); //$NON-NLS-1$
        }
        Debug.msg("Telefon Daemon restarted."); //$NON-NLS-1$
        JFritz.setProperty("telefond.laststarted", "telnetMonitor"); //$NON-NLS-1$,  //$NON-NLS-2$
    }

    public void parseOutput() {
        isRunning = true;
        try {
            String currentLine = ""; //$NON-NLS-1$
            while (isRunning) {
                currentLine = telnet.readUntil("\n"); //$NON-NLS-1$
                Pattern p = Pattern.compile(PATTERN_TELEFON);
                Matcher m = p.matcher(currentLine);
                if (m.find()) {
                    String id = m.group(1);
                    String caller = m.group(2);
                    String called = m.group(3);
                    Debug.msg("NEW CALL " + id + ": " + caller + " -> " //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
                            + called);

                    jfritz.callInMsg(caller, called);
                    if (!isRunning)
                        break;
                }

            }
        } catch (Exception e) {
            Debug.err(e.toString());
            isRunning = false;
        }
        telnet.disconnect();
    }

    public void stopCallMonitor() {
        Debug.msg("Stopping TelnetListener"); //$NON-NLS-1$
        isRunning = false;
    }
}