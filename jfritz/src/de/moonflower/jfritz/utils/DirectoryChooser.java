package de.moonflower.jfritz.utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import de.moonflower.jfritz.JFritz;

public class DirectoryChooser  {

	private JFileChooser chooser;
	public final static String CHOOSER_TITLE = "Zielverzeichnis ausw�hlen";
	
	/**
	 * @return the choosen directory
	 * 
	 */
	public File getDirectory(JFrame frame) {

		chooser = new JFileChooser();
		chooser.setApproveButtonText("Speichern");
		chooser.setCurrentDirectory(new java.io.File(JFritzUtils.deconvertSpecialChars(JFritz.getProperty("backup.path", "."))));
		chooser.setDialogTitle(CHOOSER_TITLE);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			Debug.msg("getCurrentDirectory(): " + chooser.getSelectedFile());
			JFritz.setProperty("backup.path", JFritzUtils.convertSpecialChars(chooser.getSelectedFile().toString()));
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

}
