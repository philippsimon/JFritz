package de.moonflower.jfritz.autoupdate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Update {
	
	private final static String className = "(Update) ";

	private static String USER_HOME = System.getProperty("user.home")
			+ File.separator;

	private static String UPDATE_FILE = ".update";

	private String propertiesDirectory = "";

	private String programVersion = "0";

	private Locale locale = null;

	private boolean updateOnStart = true;

	public Update(String propertiesDirectory) {
		this.propertiesDirectory = propertiesDirectory;
	}

	/**
	 * Setzt den Parameter f�r das automatische Updaten beim Start
	 * 
	 * @param update
	 */
	public void setUpdateOnStart(boolean update) {
		updateOnStart = update;
	}

	/**
	 * Setzt die Programmversion
	 * @param version
	 */
	public void setProgramVersion(String version) {
		programVersion = version;
	}
	
	/**
	 * Setzt die gew�nschte Sprache
	 * @param locale
	 */
	public void setLocale(String locale) {
		this.locale = new Locale(locale);
		UpdateLocale.loadMessages(this.locale);
	}
	
	/**
	 * Liest den Parameter f�r das automatische Updaten beim Start aus
	 * 
	 * @return updateOnStart
	 */
	public boolean getUpdateOnStart() {
		return updateOnStart;
	}

	/**
	 * Speichert alle Einstellungen
	 * 
	 */
	public void saveSettings() {
		try {

			System.out.print(className + "Saving update-properties...");

			// if $HOME/saveDirectory doesn't exist create it
			File file = new File(USER_HOME + propertiesDirectory);
			if (!file.isDirectory() && !file.isFile())
				file.mkdir();

			BufferedWriter bw = new BufferedWriter(
					new FileWriter(USER_HOME + propertiesDirectory
							+ File.separator + UPDATE_FILE, false));

			bw.write("updateOnStart=" + updateOnStart);
			bw.newLine();
			bw.write("locale=" + locale.toString());
			bw.newLine();
			bw.write("programVersion=" + programVersion.toString());
			bw.close();
			System.out.println("...done");

		} catch (Exception e) {
			System.err.println(className + "ERROR while saving update-properties");
		}
	}

	/**
	 * Liest alle Einstellungen
	 * 
	 */
	public void loadSettings() {
		Locale locale = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(USER_HOME
					+ propertiesDirectory + File.separator + UPDATE_FILE));
			String line = null;
			do {
				line = br.readLine();
				if (line != null) {
					String[] entries = line.split("=");
					if (entries[0].equals("updateOnStart")) {
						if (!entries[1].equals(""))
							updateOnStart = Boolean.parseBoolean(entries[1]);
					} else if (entries[0].equals("locale")) {
						if (!entries[1].equals(""))
							locale = new Locale(entries[1]);
					} else if (entries[0].equals("programVersion")) {
						if (!entries[1].equals(""))
							programVersion = entries[1];
					}
				}
			} while (line != null);
		} catch (FileNotFoundException e) {
			System.err
					.println(className + "Could not load update-properties (File not found), using defaults");
		} catch (IOException ioe) {
			System.err
					.println(className + "Error processing update-properties, using defaults");
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ioe) {
				System.err.println("Error closing stream");
			}
		}
		System.out.println(className + "Program version: " + programVersion);
		System.out.println(className + "Locale: " + locale.toString());
		System.out.println(className + "Update on start: " + updateOnStart);
		if ( locale == null ) {
			locale = new Locale("en_US");
		}
		setLocale(locale.toString());
	}
	
	/**
	 * Liefert die Programmversion zur�ck
	 * @return Programmversion
	 */
	public String getProgramVersion() {
		return programVersion;
	}
}
