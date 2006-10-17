package de.moonflower.jfritz.autoupdate;

import java.io.File;

import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * Diese Klasse �berpr�ft, ob der Ordner update im Hauptverzeichnis existiert.
 * Ist der Ordner vorhanden, so existiert in dem update-Ordner eine
 * deletefiles-Datei, in denen alle Dateien stehen, die gel�scht werden m�ssen.
 * 
 * Alle Dateien und Ordner in der deletefiles-Datei werden gel�scht und der
 * Inhalt des update-Ordners wird ins JFritz Hauptverzeichnis kopiert.
 * Anschlie�end wird der update-Ordner gel�scht
 * 
 * @author Rob
 * 
 */
public class CheckUpdateFolder {
	
	private String jfritzHomedir;

	public CheckUpdateFolder() {
		jfritzHomedir = JFritzUtils.getFullPath(".update");
		jfritzHomedir = jfritzHomedir.substring(0, jfritzHomedir.length() - 7);
	}

	public boolean updateDirectoryExists() {
		File updateDirectory = new File("update");
		return updateDirectory.exists();
	}

	public void removeUpdateFiles() {

	}

	public void updateFiles() {

	}

	public void deleteUpdateDirectory() {

	}
	
	private void deleteFile(String fileName) {
		if (fileName.endsWith(".zip")) {
			// Es ist ein Verzeichnis. L�sche das Verzeichnis
			String dirName = fileName.substring(0, fileName.length() - 4);
			Debug.msg("Deleting directory " + jfritzHomedir
					+ System.getProperty("file.separator") + dirName);
			File dir = new File(jfritzHomedir
					+ System.getProperty("file.separator") + dirName);
			deleteTree(dir);
		} else {
			Debug.msg("Deleting file " + jfritzHomedir
					+ System.getProperty("file.separator") + fileName);
			File file = new File(jfritzHomedir
					+ System.getProperty("file.separator") + fileName);
			file.delete();
		}
	}

	private static void deleteTree(File path) {
		File[] fileList = path.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
			if (file.isDirectory())
				deleteTree(file);
			System.err.println("Deleting file: " + file.getAbsolutePath());
			file.delete();
		}
		path.delete();
	}

}
