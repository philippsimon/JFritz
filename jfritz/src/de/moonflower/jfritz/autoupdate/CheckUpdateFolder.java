package de.moonflower.jfritz.autoupdate;

import java.io.File;

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
}
