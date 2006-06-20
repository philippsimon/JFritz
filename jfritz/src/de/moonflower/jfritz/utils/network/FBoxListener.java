package de.moonflower.jfritz.utils.network;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.utils.Debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Thread. Connects to FritzBox Port 1012. Captures Callermessages.
 * 
 * @author Robert Palmer
 * 
 */

public abstract class FBoxListener extends Thread implements CallMonitor {

    protected JFritz jfritz;

    protected BufferedReader in;

    protected Socket clientSocket;

    protected String[] ignoredMSNs;
    
    protected Random zufallszahl;

    public FBoxListener(JFritz jfritz) {
        super();
        this.jfritz = jfritz;
        Debug.msg("Starting FBoxListener"); //$NON-NLS-1$
        start();
        zufallszahl = new Random();
    }

    public abstract void run();

    protected boolean connect() {
        try {
            Debug.msg("Trying to connect to " //$NON-NLS-1$
                    + jfritz.getFritzBox().getAddress() + ":1012"); //$NON-NLS-1$,  //$NON-NLS-2$
            clientSocket = new Socket(jfritz.getFritzBox().getAddress(), 1012); //$NON-NLS-1$
            clientSocket.setKeepAlive(true);
            return true;
        } catch (UnknownHostException uhe) {
            Debug.msg("Unknown host exception: " + uhe.toString()); //$NON-NLS-1$
            Debug.errDlg(JFritz.getMessage("error_fritzbox_callmonitor_no_connection"). //$NON-NLS-1$
            		replaceAll("%A", jfritz.getFritzBox().getAddress())); //$NON-NLS-1$,  //$NON-NLS-2$
            jfritz.stopCallMonitor();
        } catch (IOException ioe) {
            Debug.msg("IO exception: " + ioe.toString()); //$NON-NLS-1$
            Debug.errDlg(JFritz.getMessage("error_fritzbox_callmonitor_no_connection"). //$NON-NLS-1$
            		replaceAll("%A", jfritz.getFritzBox().getAddress())); //$NON-NLS-1$,  //$NON-NLS-2$
            jfritz.stopCallMonitor();
        }
        return false;
    }

    protected void readOutput() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket
                    .getInputStream()));
            String currentLine;
            while (!isInterrupted()) {
                // lese n�chste Nachricht ein
                currentLine = in.readLine();
                parseOutput(currentLine);
            }
        } catch (IOException ioe) {
            Debug.msg("IO exception: " + ioe.toString()); //$NON-NLS-1$
        }
    }

    protected void initIgnoreList() {
        String ignoreMSNString = JFritz.getProperty(
                "option.callmonitor.ignoreMSN", ""); //$NON-NLS-1$,  //$NON-NLS-2$
        if (ignoreMSNString.length() > 0 && ignoreMSNString.indexOf(";") == -1) { //$NON-NLS-1$
            ignoreMSNString = ignoreMSNString + ";"; //$NON-NLS-1$
        }
        ignoredMSNs = ignoreMSNString.split(";"); //$NON-NLS-1$
        Debug.msg("Ignored MSNs: "); //$NON-NLS-1$
        for (int i = 0; i < ignoredMSNs.length; i++) {
            Debug.msg(ignoredMSNs[i]);
        }
    }

    protected abstract void parseOutput(String line);
    
    public void stopCallMonitor() {
        Debug.msg("Stopping FBoxListener"); //$NON-NLS-1$
        try {
            if (clientSocket != null)
                clientSocket.close();
            this.interrupt();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}