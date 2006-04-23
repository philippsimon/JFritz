/*
 * Created on 03.06.2005
 *
 */
package de.moonflower.jfritz.dialogs.phonebook;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.struct.Person;

/**
 * @author Arno Willig
 *  
 */
public class PersonDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1;
	private JFritz jfritz;

	private Person person;

	private PersonPanel personPanel;

	JButton okButton, cancelButton;

	private boolean pressed_OK = false;

	/**
	 * 
	 * @param jfritz JFritz object
	 * @param person Person object
	 * @throws HeadlessException
	 */
	public PersonDialog(JFritz jfritz, Person person) throws HeadlessException {
		super();
		this.jfritz = jfritz;
		this.person = new Person(person);
		if (this.person == null)
			person = new Person();
		this.setLocationRelativeTo(jfritz.getJframe());
		drawDialog();
	}

	private void drawDialog() {
		super.dialogInit();
		setTitle(JFritz.getMessage("dialog_title_phonebook_edit_person")); //$NON-NLS-1$
		setModal(true);
		getContentPane().setLayout(new BorderLayout());

		JPanel topPane = new JPanel();
		JPanel bottomPane = new JPanel();

		// Top Pane
		JLabel label = new JLabel(JFritz.getMessage("dialog_title_phonebook_edit_person")); //$NON-NLS-1$
		topPane.add(label);

		// Main Pane
		personPanel = new PersonPanel(jfritz, person);

		// Bottom Pane
		okButton = new JButton(JFritz.getMessage("okay")); //$NON-NLS-1$
		okButton.setActionCommand("ok"); //$NON-NLS-1$
		okButton.addActionListener(this);

		cancelButton = new JButton(JFritz.getMessage("cancel")); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
		cancelButton.addActionListener(this);

		bottomPane.add(okButton);
		bottomPane.add(cancelButton);

		getContentPane().add(topPane, BorderLayout.NORTH);
		getContentPane().add(personPanel, BorderLayout.CENTER);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		setSize(new Dimension(350, 400));

	}

	public boolean okPressed() {
		return pressed_OK;
	}

	public boolean showDialog() {
		setVisible(true);
		person.setPrivateEntry(personPanel.isPrivateEntry());
		person.setFirstName(personPanel.getFirstName());
		person.setCompany(personPanel.getCompany());
		person.setLastName(personPanel.getLastName());
		person.setCity(personPanel.getCity());
		person.setEmailAddress(personPanel.getEmail());
		person.setStreet(personPanel.getStreet());
		person.setPostalCode(personPanel.getPostalCode());
		return okPressed();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok")) { //$NON-NLS-1$
			pressed_OK = true;
			setVisible(false);
		} else if (e.getActionCommand().equals("cancel")) { //$NON-NLS-1$
			pressed_OK = false;
			setVisible(false);
		}
	}

	/**
	 * @return Returns the person.
	 */
	public final Person getPerson() {
		return person;
	}
}
