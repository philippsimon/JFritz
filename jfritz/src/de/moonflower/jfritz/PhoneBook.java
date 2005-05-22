/*
 * $Id$
 * 
 * Created on 06.05.2005
 *
 */
package de.moonflower.jfritz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.JFrame;

/**
 * Shows a phone book in which the entries can be edited.
 * 
 * @author Arno Willig
 * 
 * TODO: Tabelle mit Eintr�gen
 * - Eintrag suchen
 * - Neuer Eintrag
 * - Eintrag ver�ndern
 * - Reverse Lookup
 * - Eint�ge importieren (Outlook, Evolution)
 */
public class PhoneBook extends JFrame {

	JFritzWindow jfritz;
	ResourceBundle messages;
	
	public PhoneBook(JFritzWindow jfritz) {
		super();
		this.jfritz = jfritz;
		messages = jfritz.getMessages();
		createAndShowGUI();
	}

	private void createAndShowGUI() {
		setDefaultLookAndFeelDecorated(true);

		setTitle(messages.getString("phonebook"));
		setSize(new Dimension(480, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new BorderLayout());
/*
		createMenu();
		createToolbar();
		createTable();
		createStatusbar();
*/
		// pack();
		// FIXME: Works only in J2SE 1.5: this.setLocationByPlatform(true);
		setVisible(true);
	}
}
