package de.moonflower.jfritz.autoupdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Dieser Thread �berpr�ft, ob eine neue Programmversion verf�gbar ist
 * 
 * Ist eine neue Version verf�gbar, werden die neuen Dateien in den
 * update-Ordner heruntergeladen und eine Datei deletefiles erstellt, in der die
 * zu l�schenden Dateien und Ordner drinstehen
 * 
 * @author Robert Palmer
 * 
 */
public class CheckVersionThread extends Thread {
	
	private final static String className = "(CheckVersionThread) ";

	// URL zum Update-Ordner auf der Homepage
	private String updateURL = "";

	// Datei, die die Versionsinformationen auf der Homepage enth�lt
	private String versionFile = "";

	// Enth�lt die aktuelle Versionsnummer
	private String programVersion = "";

	// Enth�lt die neue Versionsnummer
	private String newVersion = "";
	
	// Neue Version verf�gbar?
	private boolean newVersionAvailable = false;
	
	public CheckVersionThread(String programVersion, String updateURL, String versionFile) {
		this.programVersion = programVersion;
		this.updateURL = updateURL;
		this.versionFile = versionFile;
	}
	
	public void run() {
		System.out.println(className + "Check for new program version...");
		if (checkForNewVersion()) {
			newVersionAvailable = true;						
		} else {
			newVersionAvailable = false;
		}		
		System.out.println(className + "...done");
	}

	/**
	 * Setzt die URL zum Update-Ordner auf der Homepage
	 * 
	 * @param URL
	 *            zum Update-Ordner auf der Homepage
	 */
	public void setUpdateURL(String updateURL) {
		if (!updateURL.endsWith("/"))
			updateURL.concat("/");
		this.updateURL = updateURL;
	}

	/**
	 * Setzt den Dateiname auf die Datei, die die Versionsinformationen auf der
	 * Homepage enth�lt
	 * 
	 * @param Dateiname
	 */
	public void setVersionFile(String versionFile) {
		this.versionFile = versionFile;
	}

	/**
	 * Setzt die aktuelle Programmversionsnummer
	 * 
	 * @param programVersion
	 */
	public void setProgramVersion(String programVersion) {
		this.programVersion = programVersion;
	}
	
	/**
	 * �berpr�ft, ob eine neue Version verf�gbar ist
	 * 
	 * @return true, wenn neue Version verf�gbar
	 */
	private boolean checkForNewVersion() {
		// Don't check for new version, if programVersion is zero
		if (programVersion.equals("0"))
			return false;
		
		
		URL url = null;
		String urlstr = updateURL + versionFile; //$NON-NLS-1$

		boolean foundNewVersion = false;

		try {
			url = new URL(urlstr);
			if (url != null) {

				URLConnection con;
				try {
					con = url.openConnection();
					// 1 Sekunde-Timeout f�r den Verbindungsaufbau 
					con.setConnectTimeout(5000);
					
					// 30 Sekunde-Timeout f�r die Datenverbindung
					con.setReadTimeout(30000);
					BufferedReader d = new BufferedReader(
							new InputStreamReader(con.getInputStream()));

					// Get remote version
					String str = d.readLine();
					// Format remote version as 0.621
					String remoteVersion = str.replaceFirst("\\.", "\\|")
							.replaceAll("\\.", "").replaceFirst("\\|", "\\.");

					// Format local version as 0.621
					String localVersion = programVersion.replaceFirst("\\.",
							"\\|").replaceAll("\\.", "").replaceFirst("\\|",
							"\\.");

					if (Double.valueOf(remoteVersion).compareTo(
							Double.valueOf(localVersion)) > 0) {
						newVersion = str;
						foundNewVersion = true;
					}

					d.close();

				} catch (IOException e1) {
					System.err.println(className + "Error while retrieving "
							+ urlstr
							+ " (possibly no connection to the internet)"); //$NON-NLS-1$
				}
			}
		} catch (MalformedURLException e) {
			System.err.println(className + "URL invalid: " + urlstr); //$NON-NLS-1$
		}
		return foundNewVersion;
	}
	
	/**
	 * Ist eine neue Version verf�gbar?
	 * @return
	 */
	public boolean isNewVersionAvailable() {
		return newVersionAvailable;
	}
	
	/**
	 * Liefert die neue Version zur�ck
	 * @return
	 */
	public String getNewVersion() {
		return newVersion;
	}
}
