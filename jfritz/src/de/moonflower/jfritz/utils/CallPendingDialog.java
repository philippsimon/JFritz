package de.moonflower.jfritz.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.network.NetworkStateMonitor;

/**
 * @author Bastian Schaefer
 * 
 */

public class CallPendingDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1;

	private String infoText;

	JButton okButton, cancelButton;

	public CallPendingDialog(String infoText) throws HeadlessException {
		super(JFritz.getJframe());
		this.infoText = infoText;		
		
		drawDialog();
		this.setLocationRelativeTo(JFritz.getJframe());
	}

	public void drawDialog() {
		super.dialogInit();
		setTitle(Main.getMessage("information")); //$NON-NLS-1$

		Container c = getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);

		// this.setAlwaysOnTop(true); //erst ab Java V.5.0 m�glich
		setModal(true);

		// Top
		JLabel label = new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setText("<html>"+infoText+"</html>");
		addComponent(c, gbl, label, 0, 0, 2, 3, 1.0, 1.0, 15, 15, 15, 15);

		// Center
		okButton = new JButton(Main.getMessage("okay")); //$NON-NLS-1$
		okButton.setActionCommand("ok"); //$NON-NLS-1$
		okButton.addActionListener(this);
		addComponent(c, gbl, okButton, 0, 3, 1, 1, 1.0, 0, 30, 5, 0, 10);

		cancelButton = new JButton(Main.getMessage("cancel")); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
		cancelButton.addActionListener(this);
		addComponent(c, gbl, cancelButton, 1, 3, 1, 1, 1.0, 0, 5, 30, 0, 10);

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
		
		setSize(new Dimension(300, 150));
		setResizable(false);
	}

	private void addComponent(Container cont, GridBagLayout gbl, Component c,
			int x, int y, int width, int height, double weightx,
			double weighty, int insetsLeft, int insetsRight, int insetsTop,
			int insetsBottom) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets.left = insetsLeft;
		gbc.insets.right = insetsRight;
		gbc.insets.top = insetsTop;
		gbc.insets.bottom = insetsBottom;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok")) { //$NON-NLS-1$
			setVisible(false);
		} else if (e.getActionCommand().equals("cancel")) { //$NON-NLS-1$
			try 
			{
				NetworkStateMonitor.hangup();
				setVisible(false);
			} catch (WrongPasswordException e1) {
				JFritz.errorMsg(Main.getMessage("box.wrong_password")); //$NON-NLS-1$
				Debug.errDlg(Main.getMessage("box.wrong_password")); //$NON-NLS-1$
			} catch (IOException e1) {
				JFritz.errorMsg(Main.getMessage("box.not_found")); //$NON-NLS-1$
				Debug.errDlg(Main.getMessage("box.not_found")); //$NON-NLS-1$
			}
		}
	}

}
