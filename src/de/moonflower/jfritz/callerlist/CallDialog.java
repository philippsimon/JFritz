/*
 * Created on 03.06.2005
 *
 */
package de.moonflower.jfritz.callerlist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.firmware.FritzBoxFirmware;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.NoticeDialog;

/**
 * @author Robert Palmer
 * 
 */
public class CallDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1;

	private Vector<PhoneNumber> numbers;

	private JComboBox port;

	private FritzBoxFirmware firmware = null;

	JButton okButton, cancelButton;

	private Object cboNumber;
	private PhoneNumber defaultNumber;

	/**
	 * This Constructor initializes the CallDialog with a set of numbers and a default number.
	 * If Vector contains more than one number CallDialog will present the numbers of the vector in an editable JComboBox with the defaultNumber selected as default.    
	 * @param numbers
	 *            Vector of PhoneNumbers (e.g. from Person.getNumbers())
	 * @param defaultNumber
	 *            String containig the default number to select in JComboBox
	 * @throws HeadlessException
	 */
	public CallDialog(Vector<PhoneNumber> numbers, PhoneNumber defaultNumber)
			throws HeadlessException {
		super(JFritz.getJframe()); //sets icon to JFritz' one
		//this.setLocation(JFritz.getJframe().getX() + 80, JFritz.getJframe().getY() + 100);
		this.numbers = numbers;
		this.defaultNumber = defaultNumber;
		drawDialog();
		this.setLocationRelativeTo(JFritz.getJframe());
	}

	/**
	 * This Constructor initializes the CallDialog with a one number and sets this to the defaultNumber.
	 * Due to having only one number in Vector CallDialog will present this number in an editable JTextField. 
	 * @param number
	 *            PhoneNumber object
	 * @throws HeadlessException
	 */
	public CallDialog(PhoneNumber number)
			throws HeadlessException {
		super(JFritz.getJframe()); //sets icon to JFritz' one
		// this.setLocationRelativeTo(JFritz.getJframe());
		//this.setLocation(JFritz.getJframe().getX() + 80, JFritz.getJframe().getY() + 100);
		Vector<PhoneNumber> v = new Vector<PhoneNumber>();
		v.addElement(number);
		this.numbers = v;
        this.defaultNumber = number; //does not really need, but to be complete
		drawDialog();
		this.setLocationRelativeTo(JFritz.getJframe());
	}

	private void drawDialog() {
		NoticeDialog info = new NoticeDialog(
                "legalInfo.telephoneCharges", //$NON-NLS-1$
				Main.getMessage("telefonCharges_Warning")); //$NON-NLS-1$
		
		info.setVisible(true);
		info.dispose();
		if (info.isAccepted()) {
			super.dialogInit();
			setTitle(Main.getMessage("call")); //$NON-NLS-1$
			// this.setAlwaysOnTop(true); //erst ab Java V.5.0 m�glich
			setModal(true);
			getContentPane().setLayout(new BorderLayout());

			JPanel topPane = new JPanel();
			JPanel bottomPane = new JPanel();

			// Top Pane
			topPane.setLayout(new GridBagLayout());
			topPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
			GridBagConstraints c = new GridBagConstraints();
			c.insets.top = 5;
			c.insets.bottom = 5;
			c.insets.left = 5;
			c.anchor = GridBagConstraints.WEST;

			c.gridy = 1;
			
			JLabel label = new JLabel(Main.getMessage("number")+": "); //$NON-NLS-1$,  //$NON-NLS-2$
			topPane.add(label, c);
			
			//make the number editable
			if (this.numbers.size() == 1) { // if only one number -> use editable JTextField
				cboNumber = new JTextField((numbers.elementAt(0)).getAreaNumber());
				((JTextField)cboNumber).setPreferredSize(new Dimension(100, 20));
			} else {// if more then one number -> use editable JComboBox
				cboNumber = new JComboBox();
				for (int i = 0; i < this.numbers.size(); i++) {
					((JComboBox) cboNumber).addItem((numbers
							.elementAt(i)).getAreaNumber());
				}

				//choose defaultNumber as initial value of JComboBox 
				((JComboBox) cboNumber).setSelectedItem(this.defaultNumber
						.getAreaNumber());
				((JComboBox) cboNumber).setEditable(true);
			}
			topPane.add((Component) cboNumber, c);
			c.gridy = 2;
			label = new JLabel(Main.getMessage("extension")+": "); //$NON-NLS-1$,  //$NON-NLS-2$
			topPane.add(label, c);

			port = new JComboBox();
			port.addItem("Fon 1"); //$NON-NLS-1$
            firmware = JFritz.getFritzBox().getFirmware();
			if (firmware != null) {
				switch (firmware.getBoxType()) {
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_FON :
						port.addItem("Fon 2"); //$NON-NLS-1$
						break;
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_FON_WLAN :
						// ggf. kann dies auch f�r die anderen Boxen gelten?
						port.addItem("Fon 2"); //$NON-NLS-1$
						port.addItem(Main.getMessage("analog_telephones_all"));  //$NON-NLS-1$
						break;
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_ATA :
						port.addItem("Fon 2"); //$NON-NLS-1$
						break;
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_5010:
						// die 5010 hat nur einen analogen Anschluss
						break;
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_5050:
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_7050:
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_7170:
						 {
							 port.addItem("Fon 2"); //$NON-NLS-1$
							 port.addItem("Fon 3"); //$NON-NLS-1$
						 }
					case FritzBoxFirmware.BOXTYPE_FRITZBOX_5012:
						 {
							 port.addItem("ISDN Alle"); //$NON-NLS-1$
							 port.addItem("ISDN 1"); //$NON-NLS-1$
							 port.addItem("ISDN 2"); //$NON-NLS-1$
							 port.addItem("ISDN 3"); //$NON-NLS-1$
							 port.addItem("ISDN 4"); //$NON-NLS-1$
							 port.addItem("ISDN 5"); //$NON-NLS-1$
							 port.addItem("ISDN 6"); //$NON-NLS-1$
							 port.addItem("ISDN 7"); //$NON-NLS-1$
							 port.addItem("ISDN 8"); //$NON-NLS-1$
							 port.addItem("ISDN 9"); //$NON-NLS-1$
							 break;
						 }
					}
			}
            port.setPreferredSize(new Dimension(100, 20));            
			topPane.add(port, c);

			// Bottom Pane
			okButton = new JButton(Main.getMessage("call")); //$NON-NLS-1$
			okButton.setActionCommand("call"); //$NON-NLS-1$
			okButton.addActionListener(this);

			cancelButton = new JButton(Main.getMessage("cancel")); //$NON-NLS-1$
			cancelButton.setActionCommand("close"); //$NON-NLS-1$
			cancelButton.addActionListener(this);

			bottomPane.add(okButton);
			bottomPane.add(cancelButton);

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

            port.setSelectedIndex(Integer.parseInt(Main.getProperty("calldialog.lastport", "0")));

			getContentPane().add(topPane, BorderLayout.NORTH);
			getContentPane().add(bottomPane, BorderLayout.SOUTH);
			setSize(new Dimension(300, 150));

		}

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("call")) { //$NON-NLS-1$
			
            Main.setProperty("calldialog.lastport", Integer.toString(port.getSelectedIndex()));
            
//			if(!number.getText().equals(""))
//				JFritz.getFritzBox().doCall(number.getText(), port.getSelectedItem().toString());

			if (cboNumber.getClass().toString().equals(
					"class javax.swing.JTextField")) //$NON-NLS-1$
			{
				JFritz.getFritzBox().doCall(((JTextField) cboNumber).getText(), port.getSelectedItem().toString());
				//JOptionPane.showMessageDialog(null,"JTextField: "+((JTextField) cboNumber).getText());
			}
			if (cboNumber.getClass().toString().equals(
					"class javax.swing.JComboBox")) //$NON-NLS-1$
			{
				JFritz.getFritzBox().doCall(((JComboBox) cboNumber).getSelectedItem().toString(), port.getSelectedItem().toString());
				//JOptionPane.showMessageDialog(null,"JComboBox: "+((JComboBox) cboNumber).getSelectedItem().toString());
			}
			
			setVisible(false);
		} else if (e.getActionCommand().equals("close")) { //$NON-NLS-1$
			Main.setProperty("calldialog.lastport", Integer.toString(port.getSelectedIndex()));
			setVisible(false);
		}
	}
}
