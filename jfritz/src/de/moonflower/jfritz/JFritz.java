/**
 * $Id$
 * 
 * JFritz!
 * http://jfritz.sourceforge.net
 * 
 *
 * (c) Arno Willig <akw@thinkwiki.org>
 * 
 * Created on 08.04.2005
 *
 * Authors working on the project:
 * 		akw			Arno Willig <akw@thinkwiki.org>
 * 		robotniko	Robert Palmer <robotniko@gmx.de>
 * 
 * 
 * This tool is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This piece of software is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this driver; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *
 * TODO:
 * Optionen-Dialog: box.clear_after_fetch=true/false
 * Optionen-Dialog: Bei Programmstart automatisch abrufen
 * SIP-Provider: SIP-Provider Handling
 * QuickDial: Kurzwahlverwaltung
 * Phonebook: Telefonbuch f�r Participants
 * 
 * ColumnWidth speichern.
 * 
 * JTable: 
 * - Filtern von CallTypes
 * - Suchfeld f�r Name/Nummernsuche
 * 
 * Statistik: Top-Caller (Name/Nummer, Wie oft, Wie lange)
 * 
 * JAR: Signing, Deploying, Website jfritz.moonflower.de oder Sourceforge
 * 
 * CHANGELOG:
 *  
 * JFritz! 0.3.0: Major release
 * - Compatibility for JRE 1.4.2
 * - Severel bugfixes
 * 
 * JFritz! 0.2.8: 
 * - Bugfix: Firmware detection had nasty bug
 * - Bugfix: Firmware detection detects modded firmware properly
 * - Bugfix: RegExps adapted for modded firmware
 * - Support for SIP-Provider for fritzbox fon wlan
 * - Notify users whenn calls have been retrieved
 * - CSV Export
 * 
 * JFritz! 0.2.6:
 * - Several bugfixes
 * - Support for Fritz!Boxes with modified firmware
 * - Improved config dialog
 * - Improved firmware detection
 * - Initial support f�r SIP-Provider
 * - Firmware/SIP-Provider are saved in config file
 * 
 * JFritz! 0.2.4:
 * - Several bugfixes
 * - Improventsment on number resolution
 * - Optimized Reverse Lookup
 * 
 * JFritz! 0.2.2:
 * - FRITZ!Box FON WLAN works again and is detected automatically.
 * - Target MSN is displayed
 * - Bugfixes for Reverse Lookup (Mobile phone numbers are filtered now)
 * - Nice icons for calltypes (Regular call, Area call, Mobile call)
 * - Several small bugfixes
 * 
 * JFritz! 0.2.0: Major release
 * - Improved GUI, arranged colours for win32 platform
 * - New ToolBar with nice icons
 * - Bugfix: Not all calls had been retrieved from box
 * - Improved reverse lookup
 * - Automatic box detection (does not yet work perfectly)
 * - Internal class restructuring
 *  
 * JFritz! 0.1.6:
 * - Calls are now saved in XML format
 * - Support for Fritz!Box 7050
 * 
 * JFritz! 0.1.0:
 * - Initial version
 */
package de.moonflower.jfritz;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

/**
 * This is main class of JFritz, which creates the GUI.
 * 
 * @author akw
 */
public class JFritz extends JFrame implements Runnable, ActionListener,
		ItemListener {

	public final static String PROGRAM_NAME = "JFritz!";

	public final static String PROGRAM_VERSION = "0.3.0";

	public final static String CVS_TAG = "$Id$";

	public final static String PROGRAM_AUTHOR = "Arno Willig <akw@thinkwiki.org>";

	public final static String PROPERTIES_FILE = "jfritz.properties.xml";

	public final static String PARTICIPANTS_FILE = "jfritz.participants.xml";

	public final static String CALLS_FILE = "jfritz.calls.xml";

	public final static String CALLS_CSV_FILE = "calls.csv";

	public final static boolean DEVEL_VERSION = Integer
			.parseInt(PROGRAM_VERSION.substring(PROGRAM_VERSION
					.lastIndexOf(".") + 1)) % 2 == 1;

	public ResourceBundle messages;

	JFritzProperties defaultProperties, properties, participants;

	CallerList callerlist;

	CallerTable callertable;

	Timer timer;

	JMenuBar menu;

	JToolBar toolbar;

	JButton fetchButton, lookupButton, vcardButton;

	JToggleButton taskButton;

	JProgressBar progressbar;

	boolean isretrieving = false;

	Locale currentLocale;

	public JFritz() {
		loadProperties();
		saveProperties();

		//currentLocale = new Locale("en", "US");
		currentLocale = new Locale("de", "DE");
		try {
			// messages = new JFritzTextResource_de();
			messages = ResourceBundle.getBundle(
					"de.moonflower.jfritz.resources.jfritz", currentLocale);

		} catch (MissingResourceException e) {
			Debug.err("Can't find i18n resource!");
			JOptionPane.showMessageDialog(this, PROGRAM_NAME + " v"
					+ PROGRAM_VERSION
					+ "\n\nCannot start if there is an '!' in path!");
			System.exit(0);
		}
		new ReverseLookup();

		callerlist = new CallerList(properties, participants);
		javax.swing.SwingUtilities.invokeLater(this);
	}

	public void run() {
		createAndShowGUI();
		callerlist.loadFromXMLFile();
		callerlist.sortAllRowsBy(1, false);
		setStatus(callerlist.getRowCount() + " "
				+ messages.getString("entries"));
	}

	private void createAndShowGUI() {
		setDefaultLookAndFeel();
		setTitle(PROGRAM_NAME);
		setSize(new Dimension(640, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		createMenu();
		createToolbar();
		createTable();
		createStatusbar();

		// pack();
		setVisible(true);
	}

	public void setDefaultLookAndFeel() {
		setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(properties.getProperty("lookandfeel",
					UIManager.getCrossPlatformLookAndFeelClassName()));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create the status bar
	 */
	public void createStatusbar() {
		// TODO: This can be done nice (more info fields, etc.)
		progressbar = new JProgressBar();
		progressbar.setValue(0);
		progressbar.setStringPainted(true);
		setStatus("");
		getContentPane().add(progressbar, BorderLayout.SOUTH);
	}

	/**
	 * Create a tool bar
	 */
	public void createToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		fetchButton = new JButton();
		fetchButton.setToolTipText(messages.getString("fetchlist"));
		fetchButton.setActionCommand("fetchList");
		fetchButton.addActionListener(this);
		fetchButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/fetch.png"))));
		toolbar.add(fetchButton);

		taskButton = new JToggleButton();
		taskButton.setToolTipText(messages.getString("fetchtask"));
		taskButton.setActionCommand("fetchTask");
		taskButton.addActionListener(this);
		taskButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/clock.png"))));
		toolbar.add(taskButton);

		lookupButton = new JButton();
		lookupButton.setToolTipText(messages.getString("reverse_lookup"));
		lookupButton.setActionCommand("reverselookup");
		lookupButton.addActionListener(this);
		lookupButton
				.setIcon(new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/reverselookup.png"))));
		toolbar.add(lookupButton);

		JButton button = new JButton();
		button.setActionCommand("phonebook");
		button.addActionListener(this);
		button
				.setIcon(new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/phonebook.png"))));
		button.setToolTipText(messages.getString("phonebook"));
		button.setEnabled(DEVEL_VERSION);
		toolbar.add(button);

		button = new JButton();
		button.setActionCommand("quickdial");
		button.addActionListener(this);
		button
				.setIcon(new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/quickdial.png"))));
		button.setToolTipText(messages.getString("quickdial"));
		button.setEnabled(DEVEL_VERSION);
		toolbar.add(button);

		toolbar.addSeparator();

		button = new JButton();
		button.setActionCommand("export_csv");
		button.addActionListener(this);
		button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/csv.png"))));
		button.setToolTipText(messages.getString("export_csv"));
		toolbar.add(button);

		vcardButton = new JButton();
		vcardButton.setActionCommand("export_vcard");
		vcardButton.addActionListener(this);
		vcardButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/vcard.png"))));
		vcardButton.setToolTipText(messages.getString("export_vcard"));
		toolbar.add(vcardButton);

		/*
		 * button = new JButton(); button.setActionCommand("export_excel");
		 * button.addActionListener(this); button.setIcon(new
		 * ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 * getClass().getResource(
		 * "/de/moonflower/jfritz/resources/images/excel.png"))));
		 * button.setToolTipText(messages.getString("export_excel"));
		 * button.setEnabled(IS_RELEASE); toolbar.add(button);
		 * 
		 * button = new JButton(); button.setActionCommand("export_openoffice");
		 * button.addActionListener(this); button.setIcon(new
		 * ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 * getClass().getResource(
		 * "/de/moonflower/jfritz/resources/images/excel.png"))));
		 * button.setToolTipText(messages.getString("export_excel"));
		 * button.setEnabled(true); toolbar.add(button);
		 * 
		 * button.setIcon(new
		 * ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
		 * "/de/moonflower/jfritz/resources/images/openoffice.png"))));
		 * button.setToolTipText(messages.getString("export_openoffice"));
		 * button.setEnabled(IS_RELEASE); toolbar.add(button);
		 */
		button = new JButton();
		button.setActionCommand("help");
		button.addActionListener(this);
		button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/help.png"))));
		button.setToolTipText(messages.getString("help_menu"));
		button.setEnabled(DEVEL_VERSION);
		toolbar.add(button);

		toolbar.addSeparator();

		button = new JButton();
		button.setActionCommand("config");
		button.addActionListener(this);
		button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/config.png"))));
		button.setToolTipText(messages.getString("config"));
		toolbar.add(button);

		toolbar.addSeparator();
		JToggleButton tbutton = new JToggleButton(
				new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/callin_grey.png"))),
				true);
		tbutton
				.setSelectedIcon(new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/callin.png"))));
		tbutton.setActionCommand("filter_callin");
		tbutton.addActionListener(this);
		tbutton.setToolTipText(messages.getString("filter_callin"));
		tbutton.setEnabled(DEVEL_VERSION);
		toolbar.add(tbutton);

		tbutton = new JToggleButton(
				new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/callinfailed_grey.png"))),
				true);
		tbutton
				.setSelectedIcon(new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/callinfailed.png"))));
		tbutton.setActionCommand("filter_callinfailed");
		tbutton.addActionListener(this);
		tbutton.setToolTipText(messages.getString("filter_callinfailed"));
		tbutton.setEnabled(DEVEL_VERSION);
		toolbar.add(tbutton);

		tbutton = new JToggleButton(
				new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/callout_grey.png"))),
				true);
		tbutton
				.setSelectedIcon(new ImageIcon(
						Toolkit
								.getDefaultToolkit()
								.getImage(
										getClass()
												.getResource(
														"/de/moonflower/jfritz/resources/images/callout.png"))));
		tbutton.setActionCommand("filter_callout");
		tbutton.addActionListener(this);
		tbutton.setToolTipText(messages.getString("filter_callout"));
		tbutton.setEnabled(DEVEL_VERSION);
		toolbar.add(tbutton);

		getContentPane().add(toolbar, BorderLayout.NORTH);
	}

	/**
	 * Create the main table
	 *  
	 */
	public void createTable() {
		//		FilteredCallerList fcl = new FilteredCallerList();
		//		fcl.setRealTableModel(callerlist);
		//		fcl.setHiddenColumn(3);
		//		callertable = new CallerTable(fcl,messages);
		callertable = new CallerTable(callerlist, messages, properties);

		//		callerlist.addTableModelListener(new ModelListener(callertable,
		// participants));

		getContentPane().add(new JScrollPane(callertable), BorderLayout.CENTER);
	}

	/**
	 * Create the menu bar
	 *  
	 */
	public void createMenu() {
		JMenu fritzMenu = new JMenu(PROGRAM_NAME);
		JMenu editMenu = new JMenu(messages.getString("edit_menu"));
		JMenu optionsMenu = new JMenu(messages.getString("options_menu"));
		JMenu helpMenu = new JMenu(messages.getString("help_menu"));
		JMenu lnfMenu = new JMenu(messages.getString("lnf_menu"));
		JMenu exportMenu = new JMenu(messages.getString("export_menu"));

		JMenuItem item = new JMenuItem(messages.getString("fetchlist"), 'a');
		item.setActionCommand("fetchList");
		item.addActionListener(this);
		fritzMenu.add(item);
		item = new JMenuItem(messages.getString("reverse_lookup"), 'l');
		item.setActionCommand("reverselookup");
		item.addActionListener(this);
		fritzMenu.add(item);
		item = new JMenuItem(messages.getString("export_csv"), 'c');
		item.setActionCommand("export_csv");
		item.addActionListener(this);
		exportMenu.add(item);
		item = new JMenuItem(messages.getString("export_excel"), 'c');
		item.setActionCommand("export_excel");
		item.addActionListener(this);
		item.setEnabled(DEVEL_VERSION);
		exportMenu.add(item);

		item = new JMenuItem(messages.getString("export_openoffice"), 'c');
		item.setActionCommand("export_openoffice");
		item.addActionListener(this);
		item.setEnabled(DEVEL_VERSION);
		exportMenu.add(item);
		fritzMenu.add(exportMenu);

		fritzMenu.add(new JSeparator());
		item = new JMenuItem(messages.getString("phonebook"), 'b');
		item.setActionCommand("phonebook");
		item.addActionListener(this);
		item.setEnabled(DEVEL_VERSION);
		fritzMenu.add(item);
		item = new JMenuItem(messages.getString("quickdials"));
		item.setActionCommand("quickdial");
		item.addActionListener(this);
		item.setEnabled(DEVEL_VERSION);
		fritzMenu.add(item);
		fritzMenu.add(new JSeparator());
		item = new JMenuItem(messages.getString("prog_exit"), 'x');
		item.setActionCommand("exit");
		item.addActionListener(this);
		fritzMenu.add(item);

		item = new JMenuItem(messages.getString("help_content"), 'h');
		item.setActionCommand("help");
		item.addActionListener(this);
		item.setEnabled(DEVEL_VERSION);
		helpMenu.add(item);
		item = new JMenuItem(messages.getString("jfritz_website"), 'w');
		item.setActionCommand("website");
		item.addActionListener(this);
		helpMenu.add(item);
		helpMenu.add(new JSeparator());
		item = new JMenuItem(messages.getString("prog_info"), 'i');
		item.setActionCommand("about");
		item.addActionListener(this);
		helpMenu.add(item);

		LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
		ButtonGroup lnfgroup = new ButtonGroup();
		for (int i = 0; i < lnfs.length; i++) {
			JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(lnfs[i]
					.getName());
			lnfMenu.add(rbmi);
			rbmi.setSelected(UIManager.getLookAndFeel().getClass().getName()
					.equals(lnfs[i].getClassName()));
			rbmi.putClientProperty("lnf name", lnfs[i]);
			rbmi.addItemListener(this);
			lnfgroup.add(rbmi);
		}
		optionsMenu.add(lnfMenu);
		item = new JMenuItem(messages.getString("config"), 'e');
		item.setActionCommand("config");
		item.addActionListener(this);
		optionsMenu.add(item);

		menu = new JMenuBar();
		menu.add(fritzMenu);
		menu.add(optionsMenu);
		menu.add(helpMenu);
		setJMenuBar(menu);
	}

	/**
	 * start/stop timer for cyclic caller list fetching
	 * 
	 * @param enabled
	 */
	private void fetchTask(boolean enabled) {
		if (enabled) {
			timer = new Timer();
			timer.schedule(new FetchListTask(this), 5000, Integer
					.parseInt(properties.getProperty("fetch.timer")) * 60000);
			Debug.msg("Timer enabled");
		} else {
			timer.cancel();
			Debug.msg("Timer disabled");
		}
	}

	/**
	 * Fetch list from box
	 */
	public void fetchList() {
		if (!isretrieving) { // Prevent multiple clicking
			isretrieving = true;
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					boolean isdone = false;
					while (!isdone) {
						try {
							setBusy(true);
							setStatus(messages.getString("fetchdata"));
							callerlist.getNewData();
							isdone = true;
						} catch (WrongPasswordException e) {
							setBusy(false);
							setStatus(messages.getString("password_wrong"));
							String password = showPasswordDialog(properties
									.getProperty("box.password"));
							if (!password.equals("")) {
								properties
										.setProperty("box.password", password);
							} else { // Cancel
								isdone = true;
							}
						} catch (IOException e) {
							setBusy(false);
							setStatus(messages.getString("box_not_found"));
							String box_address = showAddressDialog(properties
									.getProperty("box.address"));
							if (!box_address.equals("")) {
								properties.setProperty("box.address",
										box_address);
							} else { // Cancel
								isdone = true;
							}
						}
					}
					return null;
				}

				public void finished() {
					setBusy(false);
					int rows = callerlist.getRowCount();
					setStatus(rows + " " + messages.getString("entries"));
					callerlist.sortAllRowsBy(1, false);
					isretrieving = false;
				}
			};
			worker.start();
		}
	}

	/**
	 * Do a reverse lookup for the whole list
	 */
	public void reverseLookup() {
		if (!isretrieving) { // Prevent multiple clicking
			isretrieving = true;
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					boolean isdone = false;
					while (!isdone) {
						setBusy(true);
						setStatus(messages.getString("reverse_lookup"));
						for (int i = 0; i < callerlist.getRowCount(); i++) {
							Vector data = callerlist.getCallVector();
							Call call = (Call) data.get(i);
							String number = call.getNumber();
							String participant = callerlist
									.getParticipantFromNumber(call.getNumber());
							if (!number.equals("") && (participant.equals(""))) {
								setStatus(messages
										.getString("reverse_lookup_for")
										+ " " + number + " ...");
								if (participant.equals("")) {
									participant = ReverseLookup.lookup(number);
								}
								if (!participant.equals("")) {
									participants.setProperty(number,
											participant);
									callerlist.setParticipant(participant, i);
								}
							}
						}

						isdone = true;
					}
					return null;
				}

				public void finished() {
					setBusy(false);
					isretrieving = false;
					int rows = callerlist.getRowCount();
					setStatus(rows + " " + messages.getString("entries"));
				}
			};
			worker.start();
		} else {
			Debug.err("Multiple clicking is disabled..");
		}
	}

	/**
	 * Show the quick dial dialog
	 * 
	 * TODO: A lot..
	 */
	private void showQuickDialDialog() {
		QuickDialDialog p = new QuickDialDialog(this);

		//p.setValues(properties);
		if (p.showDialog()) {
			//			p.storeValues(properties);
			//			saveProperties();
		}
		p.dispose();
		p = null;
	}

	/**
	 * Show the configuration dialog
	 */
	private void showConfigDialog() {
		ConfigDialog p = new ConfigDialog(this);
		p.setValues(properties);
		if (p.showDialog()) {
			p.storeValues(properties);
			saveProperties();
		}
		p.dispose();
		p = null;
	}

	/**
	 * Show the password dialog
	 * 
	 * @param old_password
	 * @return new_password
	 */
	public String showPasswordDialog(String old_password) {
		String password = "";
		AddressPasswordDialog p = new AddressPasswordDialog(this, true);
		p.setPass(old_password);

		if (p.showDialog()) {
			password = p.getPass();
		}
		p.dispose();
		p = null;
		return password;
	}

	/**
	 * Shows the phone book
	 */
	public void showPhoneBook() {
		// TODO: A phonebook (jtable) in which the participants can be edited.

		// PhoneBook pb = new PhoneBook(this);
	}

	/**
	 * Shows the address dialog
	 * 
	 * @param old_address
	 * @return address
	 */
	public String showAddressDialog(String old_address) {
		String address = "";
		AddressPasswordDialog p = new AddressPasswordDialog(this, false);
		p.setAddress(old_address);

		if (p.showDialog()) {
			address = p.getAddress();
		}
		p.dispose();
		p = null;
		return address;

	}

	/**
	 * Shows the about dialog
	 */
	public void showAboutDialog() {
		JOptionPane.showMessageDialog(this, PROGRAM_NAME + " v"
				+ PROGRAM_VERSION + "\n"
				+ JFritzUtils.getVersionFromCVSTag(CVS_TAG) + "\n\n"
				+ "(c) 2005 by " + PROGRAM_AUTHOR + "\n\n"
				+ "This tool is developed and released under\n"
				+ "the terms of the GNU General Public License\n\n"
				+ "Long live Free Software!");
	}

	/**
	 * Shows the exit dialog
	 */
	public void showExitDialog() {
		int exit = JOptionPane.showConfirmDialog(this, messages
				.getString("really_quit"), PROGRAM_NAME,
				JOptionPane.YES_NO_OPTION);
		if (exit == JOptionPane.YES_OPTION) {
			saveProperties();
			System.exit(0);
		}
	}

	/**
	 * Listener for window events
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			showExitDialog();
		} else {
			super.processWindowEvent(e);
		}
	}

	/**
	 * ItemListener for LookAndFeel Menu
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent ie) {
		JRadioButtonMenuItem rbmi = (JRadioButtonMenuItem) ie.getSource();
		if (rbmi.isSelected()) {
			UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) rbmi
					.getClientProperty("lnf name");
			try {
				UIManager.setLookAndFeel(info.getClassName());
				SwingUtilities.updateComponentTreeUI(this);
				properties.setProperty("lookandfeel", info.getClassName());
			} catch (Exception e) {
				Debug.err("Unable to set UI " + e.getMessage());
			}
		}
	}

	/**
	 * Loads properties from xml files
	 */
	public void loadProperties() {
		participants = new JFritzProperties();
		defaultProperties = new JFritzProperties();
		properties = new JFritzProperties(defaultProperties);

		// Default properties
		defaultProperties.setProperty("box.address", "fritz.box");
		defaultProperties.setProperty("box.password", "fritzbox");
		defaultProperties.setProperty("country.prefix", "00");
		defaultProperties.setProperty("area.prefix", "0");
		defaultProperties.setProperty("country.code", "49");
		defaultProperties.setProperty("area.code", "441");
		defaultProperties.setProperty("fetch.timer", "5");

		try {
			FileInputStream fis = new FileInputStream(PROPERTIES_FILE);
			properties.loadFromXML(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			Debug.err("File " + PROPERTIES_FILE
					+ " not found, using default values");
		} catch (Exception e) {
		}

		try {
			FileInputStream fis = new FileInputStream(PARTICIPANTS_FILE);
			participants.loadFromXML(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			Debug.err("File " + PARTICIPANTS_FILE
					+ " not found, using default values");
		} catch (Exception e) {
		}
		saveProperties();
	}

	/**
	 * Saves properties to xml files
	 */
	public void saveProperties() {
		try {
			FileOutputStream fos = new FileOutputStream(PROPERTIES_FILE);
			properties.storeToXML(fos, "Properties for " + PROGRAM_NAME + " v"
					+ PROGRAM_VERSION);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		try {
			FileOutputStream fos = new FileOutputStream(PARTICIPANTS_FILE);
			participants.storeToXML(fos, "Participants for " + PROGRAM_NAME
					+ " v" + PROGRAM_VERSION);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Sets text in the status bar
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		if (status.equals(""))
			progressbar.setString(PROGRAM_NAME + " v" + PROGRAM_VERSION);
		else
			progressbar.setString(status);
	}

	/**
	 * Setting mode of progress bar
	 * 
	 * @param busy
	 */
	public void setBusy(boolean busy) {
		/*
		 * TODO: Activate this code when all buttons are implemented Component[]
		 * c = toolbar.getComponents(); for (int i=0;i <c.length;i++) { if
		 * (c[i].getClass().equals(JButton.class)) c[i].setEnabled(!busy); }
		 */
		fetchButton.setEnabled(!busy);
		lookupButton.setEnabled(!busy);

		menu.setEnabled(!busy); // TODO: This does not work
		// menu.setVisible(!busy);
		progressbar.setIndeterminate(busy);
		if (busy)
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		else
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * @return Returns the messages.
	 */
	public ResourceBundle getMessages() {
		return messages;
	}

	/**
	 * @return Returns the properties.
	 */
	public final JFritzProperties getProperties() {
		return properties;
	}

	/**
	 * Action Listener for menu and toolbar
	 */
	public void actionPerformed(ActionEvent e) {
		Debug.msg("Action " + e.getActionCommand());
		if (e.getActionCommand() == "exit") {
			showExitDialog();
		} else if (e.getActionCommand() == "about") {
			showAboutDialog();
		} else if (e.getActionCommand() == "help") {
			Debug.err("No help available yet");
		} else if (e.getActionCommand() == "website") {
			final String url = "http://jfritz.sourceforge.net/";
			try {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e1) {
				Debug.err("Website opening works only on win32 platforms.");
			}
		} else if (e.getActionCommand() == "export_csv") {

			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(messages.getString("export_csv"));
			fc.setDialogType(JFileChooser.SAVE_DIALOG);
			fc.setSelectedFile(new File(CALLS_CSV_FILE));
			fc.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(".csv");
				}

				public String getDescription() {
					return "CSV-Dateien";
				}
			});

			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				callerlist.saveToCSVFile(file.getAbsolutePath());
			}
		} else if (e.getActionCommand() == "export_vcard") {
			if (callertable.getSelectedRow() >= 0
					&& !((String) callertable.getModel().getValueAt(
							callertable.getSelectedRow(), 3)).startsWith("?")
					&& !((String) callertable.getModel().getValueAt(
							callertable.getSelectedRow(), 3)).equals("")) {
				String name = (String) callertable.getModel().getValueAt(
						callertable.getSelectedRow(), 3);
				String number = (String) callertable.getModel().getValueAt(
						callertable.getSelectedRow(), 2);
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(messages.getString("export_vcard"));
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				fc.setSelectedFile(new File(number + ".vcf"));
				fc.setFileFilter(new FileFilter() {
					public boolean accept(File f) {
						return f.isDirectory()
								|| f.getName().toLowerCase().endsWith(".vcf");
					}

					public String getDescription() {
						return "VCard (.vcf)";
					}
				});

				if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					VCard vcard = new VCard(name, number);
					vcard.saveToFile(file);
				}
			} else {
				Debug.err("No valid row selected");
			}
		} else if (e.getActionCommand() == "config") {
			showConfigDialog();
		} else if (e.getActionCommand() == "phonebook") {
			showPhoneBook();
		} else if (e.getActionCommand() == "quickdial") {
			showQuickDialDialog();
		} else if (e.getActionCommand() == "fetchList") {
			fetchList();
		} else if (e.getActionCommand() == "fetchTask") {
			fetchTask(((JToggleButton) e.getSource()).isSelected());
		} else if (e.getActionCommand() == "reverselookup") {
			reverseLookup();
		} else if (e.getActionCommand() == "filter_callin") {
			callerlist.setFilterCallIn(!((JToggleButton) e.getSource())
					.isSelected());
		} else if (e.getActionCommand() == "filter_callinfailed") {
			callerlist.setFilterCallInFailed(!((JToggleButton) e.getSource())
					.isSelected());
		} else if (e.getActionCommand() == "filter_callout") {
			callerlist.setFilterCallOut(!((JToggleButton) e.getSource())
					.isSelected());
		} else {
			Debug.err("Unimplemented action: " + e.getActionCommand());
		}
	}

	/**
	 * Main method for starting JFritz!
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(PROGRAM_NAME + " v" + PROGRAM_VERSION
				+ " (c) 2005 by " + PROGRAM_AUTHOR);

		if (DEVEL_VERSION)
			Debug.on();

		for (int n = 0; n < args.length; n++) {
			String opt = args[n];
			if (opt.equals("-h") || opt.equals("--help")) {
				System.out.println("Arguments:");
				System.out.println(" -h or --help		This short description");
				System.out.println(" -v or --verbose	Turn on debug information");
				System.exit(0);
			} else if (opt.equals("-v") || opt.equals("--verbose")
					|| opt.equals("--debug")) {
				Debug.on();
			}
		}
		new JFritz();
	}


}