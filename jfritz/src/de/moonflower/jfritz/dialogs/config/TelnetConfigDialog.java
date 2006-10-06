/*
 * Created on 05.08.2005
 *
 */
package de.moonflower.jfritz.dialogs.config;

import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.utils.Encryption;


/**
 * @author rob
 *  
 */
public class TelnetConfigDialog extends JDialog implements CallMonitorConfigDialog{

	/**
	 * This avoids compiler warnings
	 * I don't know what it's for yet
	 */
	private static final long serialVersionUID = 8494982860518260908L;

	private JPasswordField passwordTextfield;

	private JTextField userNameTextfield;

	private JButton okButton, cancelButton;
	
	private int exitCode = 0;
	
	public static final int APPROVE_OPTION = 1;
	public static final int CANCEL_OPTION = 2;
	
	public TelnetConfigDialog(JFrame parent) {
		super(parent, true);
		if (parent != null) {
			setLocationRelativeTo(parent);
		}
		initDialog();
	}
	
	public TelnetConfigDialog(JDialog parent) {
		super(parent, true);
		initDialog();
		if (parent != null) {
			setLocationRelativeTo(parent);
		}

	}
	public void initDialog() {
		setTitle(Main.getMessage("dialog_title_telnet_options")); //$NON-NLS-1$
		setSize(270, 140);
		drawDialog();
		setProperties();
	}
	
	private void setProperties() {		
		userNameTextfield.setText(Main.getProperty("telnet.user", "")); //$NON-NLS-1$,  //$NON-NLS-2$
		if (Main.getProperty("telnet.password").equals("")) { //$NON-NLS-1$,  //$NON-NLS-2$
			passwordTextfield.setText(""); //$NON-NLS-1$
		}
		else {
			passwordTextfield.setText(Encryption.decrypt(Main.getProperty("telnet.password",""))); //$NON-NLS-1$,  //$NON-NLS-2$
		}
	}

	private void storeProperties() {
		Main.setProperty("telnet.user",userNameTextfield.getText()); //$NON-NLS-1$
		Main.setProperty("telnet.password",Encryption.encrypt(new String(passwordTextfield.getPassword()))); //$NON-NLS-1$
	}
	
	public int showConfigDialog() {
//		super.show();	
		super.setVisible(true);
		return exitCode;
	}
	
	private void drawDialog() {
	    this.setModal(true);
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
				if (source == passwordTextfield || source == okButton) {
					//OK
					exitCode = APPROVE_OPTION;
					storeProperties();
				}
				else if (source == cancelButton) {
					exitCode = CANCEL_OPTION;
				}
				// Close Window
				if (source == passwordTextfield || source == okButton
						|| source == cancelButton) {					
					setVisible(false);
				}
			}
		};
		
		JPanel telnetPanel = new JPanel();
		telnetPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 5;
		c.insets.bottom = 5;
		c.anchor = GridBagConstraints.WEST;

		c.gridwidth = 1;
		c.gridy = 0;
		JLabel telnetUserLabel = new JLabel(Main.getMessage("telnet_user")+": "); //$NON-NLS-1$
		telnetPanel.add(telnetUserLabel, c);
		userNameTextfield = new JTextField("", 12); //$NON-NLS-1$
		userNameTextfield.addKeyListener(keyListener);
		telnetPanel.add(userNameTextfield, c);		

		c.gridy = 1;
		JLabel telnetPasswordLabel = new JLabel(Main.getMessage("telnet_password")+": "); //$NON-NLS-1$
		telnetPanel.add(telnetPasswordLabel, c);
		passwordTextfield = new JPasswordField("", 12); //$NON-NLS-1$
		passwordTextfield.addKeyListener(keyListener);
		telnetPanel.add(passwordTextfield, c);

		JPanel buttonPanel = new JPanel();
		okButton = new JButton(Main.getMessage("okay")); //$NON-NLS-1$
		okButton.setActionCommand("ok_pressed"); //$NON-NLS-1$
		okButton.addActionListener(actionListener);
		okButton.addKeyListener(keyListener);

		cancelButton = new JButton(Main.getMessage("cancel")); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel_pressed"); //$NON-NLS-1$
		cancelButton.addActionListener(actionListener);
		cancelButton.addKeyListener(keyListener);
			
        //set default confirm button (Enter)
        getRootPane().setDefaultButton(okButton);
               
        //set default close button (ESC)       
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction()
        {
            private static final long serialVersionUID = 3L;

            public void actionPerformed(ActionEvent e)
            {
                 cancelButton.doClick();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE"); //$NON-NLS-1$
        getRootPane().getActionMap().put("ESCAPE", escapeAction); //$NON-NLS-1$
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		getContentPane().add(telnetPanel, BorderLayout.CENTER);		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);		
	}
}
