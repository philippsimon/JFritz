/*
 * Created on 24.05.2005
 *
 */
package de.moonflower.jfritz.utils.network;

import java.util.Vector;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;

/**
 * @author Arno Willig
 *  
 */
public class SSDPdiscoverThread extends Thread {

	int timeout;

	Vector devices;

	/**
	 * Constructs SSDPdiscoverThread
	 * 
	 * @param timeout
	 */
	public SSDPdiscoverThread(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		JFritz.getJframe().setStatus(Main.getMessage("detect_boxes")); //$NON-NLS-1$
		JFritz.getJframe().setBusy(true);

		devices = UPNPUtils.SSDP_discoverFritzBoxes(timeout);
		JFritz.getJframe().setBusy(false);
		JFritz.getJframe().setStatus();
	}

	/**
	 * @return Returns the fritz box devices.
	 */
	synchronized public final Vector getDevices() {
		return devices;
	}
}
