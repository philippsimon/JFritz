/*
 * Created on 09.09.2005
 *
 */
package de.moonflower.jfritz.dialogs.config;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.moonflower.jfritz.JFritz;

/**
 * @author Robert Palmer
 *  
 */
public class CallmessageConfigDialog extends JDialog implements CallMonitorConfigDialog{

	/**
	 * This avoids compiler warnings
	 * I don't know what it's for yet
	 */
	private static final long serialVersionUID = -8662130877265779872L;

	private int exitCode = 0;

	private JButton cancelButton, okButton;

	public static final int APPROVE_OPTION = 1;

	public static final int CANCEL_OPTION = 2;
	
	private JTextField callmessagePort;

	public CallmessageConfigDialog(JDialog parent, JFritz jfritz) {
		super(parent, true);
		if (parent != null) {
			setLocationRelativeTo(parent);
		}
		//		this.jfritz = jfritz;
		initDialog();
	}

	public void initDialog() {
		setTitle(JFritz.getMessage("dialog_title_callmessage_options")); //$NON-NLS-1$
		setSize(270, 140);
		drawDialog();
		setProperties();
	}

	private void setProperties() {
		callmessagePort.setText(JFritz.getProperty("option.callmessageport", "23232")); //$NON-NLS-1$,  //$NON-NLS-2$
	}

	private void storeProperties() {
		JFritz.setProperty("option.callmessageport", callmessagePort.getText()); //$NON-NLS-1$
	}

	public int showConfigDialog() {
//		super.show();
		super.setVisible(true);
		return exitCode;
	}

	private void drawDialog() {
		KeyListener keyListener = (new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// Cancel
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE
						|| (e.getSource() == cancelButton && e.getKeyCode() == KeyEvent.VK_ENTER)) {
					exitCode = CANCEL_OPTION;
					setVisible(false);
				}
				// OK
				if (e.getSource() == okButton
						&& e.getKeyCode() == KeyEvent.VK_ENTER) {
					storeProperties();
					exitCode = APPROVE_OPTION;
					setVisible(false);
				}
			}
		});
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source == callmessagePort || source == okButton) {
					//OK
					exitCode = APPROVE_OPTION;
					storeProperties();
				} else if (source == cancelButton) {
					exitCode = CANCEL_OPTION;
				}
				// Close Window
				if (source == callmessagePort || source == okButton
						|| source == cancelButton) {
					setVisible(false);
				}
			}
		};

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 5;
		c.insets.bottom = 5;
		c.anchor = GridBagConstraints.WEST;

		c.gridwidth = 1;
		c.gridy = 0;
		JLabel label = new JLabel("Callmessage-Port: "); //$NON-NLS-1$
		panel.add(label, c);
		callmessagePort = new JTextField("", 5); //$NON-NLS-1$
		panel.add(callmessagePort, c);

		JPanel buttonPanel = new JPanel();
		okButton = new JButton(JFritz.getMessage("okay")); //$NON-NLS-1$
		okButton.setActionCommand("ok_pressed"); //$NON-NLS-1$
		okButton.addActionListener(actionListener);
		okButton.addKeyListener(keyListener);

		cancelButton = new JButton(JFritz.getMessage("cancel")); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel_pressed"); //$NON-NLS-1$
		cancelButton.addActionListener(actionListener);
		cancelButton.addKeyListener(keyListener);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
}
