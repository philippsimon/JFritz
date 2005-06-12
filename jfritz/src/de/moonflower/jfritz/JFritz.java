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
 * of MERCHANTABILITY or F�ITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this driver; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *
 * GLOBAL TODO:
 * 
 * CallerList: Einzelne Eintr�ge l�schen
 * CallerList: Eintr�ge l�schen �lter als Datum
 * CallerList: Alle Eintr�ge l�schen
 * CallerList: ev. Popup-Menu?
 * Statistik: Top-Caller (Name/Nummer, Wie oft, Wie lange)
 * YAC-Messages: Config-Options: enabled/disabled
 * 
 * BUG: Password on start
 * 
 * CHANGELOG:
 * 
 * JFritz! 0.3.9
 * - Systray minimizes JFrame
 * - Mobile filter inverted
 * - Removed participant support in favour of person
 * - Phonebook support
 * - Added commandline option --fetch
 * - Rewrote xml handler for phonebook
 * - Option for password check on program start
 *
 * 
 * Internal:
 * - Added PhoneNumber class
 * - Added PhoneType class
 * - Restructured packages
 * 
 * TODO:
 * - Sort Phonebook (new entries must be sorted, too)
 * - Implement Phonebook ToolBar (add/delete
 * - Merging of person entries
 * - Implement reverselookup for Switzerland (www.telsearch.ch)
 * - CMD Option --export-csv
 * 
 * JFritz! 0.3.6
 * - New mobile phone filter feature
 * - Systray support for Linux/Solaris/Windows
 * - Systray ballon messages for Linux/Solaris/Windows
 * - Browser opening on Unix platforms
 * - Bugfix: Call with same timestamp are collected
 * 
 * JFritz! 0.3.4
 * - New search filter feature
 * - New date and date range filter feature
 * - Sorting of columns by clicking on column headers
 * - VOIP numbers starting with 49 are now rewritten correctly 
 * - SSDP Autodetection of Fritz!Boxes
 * - QuickDial Management
 * - Selection of multiple rows copies VCards to clipboard
 * - Bugfix: Config-Dialog now saves all values correctly
 * - Bugfix: No empty SIP provider after detection
 * - Bugfix: Save-Dialog on export functions
 * - Code rearrangement
 * 
 * 
 * JFritz! 0.3.2:
 * - Saves and restores window position/size
 * - Saves and restores width of table columns
 * - CallTypeFilter works now (thanks to robotniko)
 * - Filter option is saved
 * - Added filter for calls without displayed number
 * - Total duration of calls now displayed in status bar
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumn;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import de.moonflower.jfritz.callerlist.CallerList;
import de.moonflower.jfritz.dialogs.phonebook.PhoneBook;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.utils.JFritzProperties;
import de.moonflower.jfritz.utils.YAClistener;
import de.moonflower.jfritz.utils.upnp.SSDPdiscoverThread;

/**
 * @author Arno Willig
 *  
 */
public final class JFritz {

	public final static String PROGRAM_NAME = "JFritz!";

	public final static String PROGRAM_VERSION = "0.4.0";

	public final static String PROGRAM_URL = "http://jfritz.sourceforge.net/";

	public final static String PROGRAM_SECRET = "jFrItZsEcReT";

	public final static String DOCUMENTATION_URL = "http://jfritz.sourceforge.net/documentation.php";

	public final static String CVS_TAG = "$Id$";

	public final static String PROGRAM_AUTHOR = "Arno Willig <akw@thinkwiki.org>";

	public final static String PROPERTIES_FILE = "jfritz.properties.xml";

	public final static String CALLS_FILE = "jfritz.calls.xml";

	public final static String QUICKDIALS_FILE = "jfritz.quickdials.xml";

	public final static String PHONEBOOK_FILE = "jfritz.phonebook.xml";

	public final static String CALLS_CSV_FILE = "calls.csv";

	public final static int SSDP_TIMEOUT = 1000;

	public final static int SSDP_MAX_BOXES = 3;

	public final static boolean DEVEL_VERSION = Integer
			.parseInt(PROGRAM_VERSION.substring(PROGRAM_VERSION
					.lastIndexOf(".") + 1)) % 2 == 1;

	public static boolean SYSTRAY_SUPPORT = false;

	private JFritzProperties defaultProperties;

	private static JFritzProperties properties;

	private static ResourceBundle messages;

	private SystemTray systray;

	private static TrayIcon trayIcon;

	private JFritzWindow jframe;

	private Vector devices;

	private SSDPdiscoverThread ssdpthread;

	private CallerList callerlist;

	private PhoneBook phonebook;

	private YAClistener yacListener;

	/**
	 * Constructs JFritz object
	 */
	public JFritz(boolean onlyFetchCalls) {
		loadProperties();
		loadMessages(new Locale("de", "DE"));

		phonebook = new PhoneBook(this);
		phonebook.loadFromXMLFile(PHONEBOOK_FILE);

		callerlist = new CallerList(this);
		callerlist.loadFromXMLFile(CALLS_FILE);

		if (onlyFetchCalls) {
			infoMsg("Anrufliste wird von Fritz!Box geholt..");
			try {
				callerlist.getNewCalls();
			} catch (WrongPasswordException e) {
				Debug.err(e.toString());
			} catch (IOException e) {
				Debug.err(e.toString());
			} finally {
				infoMsg("JFritz! beendet sich nun.");
				System.exit(0);
			}
		}

		jframe = new JFritzWindow(this);

		if (checkForSystraySupport()) {
			try {
				systray = SystemTray.getDefaultSystemTray();
				createTrayMenu();
			} catch (Exception e) {
				Debug.err(e.toString());
				SYSTRAY_SUPPORT = false;
			}
		}

		ssdpthread = new SSDPdiscoverThread(this, SSDP_TIMEOUT);
		ssdpthread.start();

		javax.swing.SwingUtilities.invokeLater(jframe);

	}

	/**
	 * Checks for systray availability
	 */
	private boolean checkForSystraySupport() {
		String os = System.getProperty("os.name");
		if (os.equals("Linux") || os.equals("Solaris")
				|| os.startsWith("Windows")) {
			SYSTRAY_SUPPORT = true;
		}
		return SYSTRAY_SUPPORT;
	}

	/**
	 * Main method for starting JFritz!
	 * 
	 * @param args
	 *            Program arguments (-h -v)
	 */
	public static void main(String[] args) {
		System.out.println(PROGRAM_NAME + " v" + PROGRAM_VERSION
				+ " (c) 2005 by " + PROGRAM_AUTHOR);
		if (DEVEL_VERSION)
			Debug.on();
		boolean onlyFetchCalls = false;

		for (int n = 0; n < args.length; n++) {
			String opt = args[n];
			if (opt.equals("-h") || opt.equals("--help")) {
				System.out.println("Arguments:");
				System.out.println(" -h or --help	 This short description");
				System.out
						.println(" -v or --verbose Turn on debug information");
				System.out.println(" -s or --systray Turn on systray support");
				System.out.println(" -f or --fetch   Fetch new calls and exit");
				System.exit(0);
			} else if (opt.equals("-v") || opt.equals("--verbose")
					|| opt.equals("--debug")) {
				Debug.on();
			}
			if (opt.equals("-s") || opt.equals("--systray")) {
				JFritz.SYSTRAY_SUPPORT = true;
			}
			if (opt.equals("-f") || opt.equals("--fetch")) {
				onlyFetchCalls = true;
			}
		}

		new JFritz(onlyFetchCalls);
	}

	/**
	 * Creates the tray icon menu
	 */
	private void createTrayMenu() {
		System.setProperty("javax.swing.adjustPopupLocationToFit", "false");

		JPopupMenu menu = new JPopupMenu("JFritz! Menu");
		JMenuItem menuItem = new JMenuItem(PROGRAM_NAME + " v"
				+ PROGRAM_VERSION);
		menuItem.setEnabled(false);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(getMessage("fetchlist"));
		menuItem.setActionCommand("fetchList");
		menuItem.addActionListener(jframe);
		menu.add(menuItem);
		menuItem = new JMenuItem(getMessage("reverse_lookup"));
		menuItem.setActionCommand("reverselookup");
		menuItem.addActionListener(jframe);
		menu.add(menuItem);
		menuItem = new JMenuItem(getMessage("config"));
		menuItem.setActionCommand("config");
		menuItem.addActionListener(jframe);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(getMessage("prog_exit"));
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(jframe);
		menu.add(menuItem);

		ImageIcon icon = new ImageIcon(
				JFritz.class
						.getResource("/de/moonflower/jfritz/resources/images/phone.png"));

		trayIcon = new TrayIcon(icon, "JFritz!", menu);
		trayIcon.setIconAutoSize(false);
		trayIcon
				.setCaption(JFritz.PROGRAM_NAME + " v" + JFritz.PROGRAM_VERSION);
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (jframe.isVisible()) {
					jframe.setState(JFrame.ICONIFIED);
					jframe.setVisible(false);
				} else {
					jframe.setState(JFrame.NORMAL);
					jframe.setVisible(true);
				}
			}
		});
		systray.addTrayIcon(trayIcon);
	}

	/**
	 * Loads resource messages
	 * 
	 * @param locale
	 */
	private void loadMessages(Locale locale) {
		try {
			messages = ResourceBundle.getBundle(
					"de.moonflower.jfritz.resources.jfritz", locale);
		} catch (MissingResourceException e) {
			Debug.err("Can't find i18n resource!");
			JOptionPane.showMessageDialog(null, JFritz.PROGRAM_NAME + " v"
					+ JFritz.PROGRAM_VERSION
					+ "\n\nCannot start if there is an '!' in path!");
			System.exit(0);
		}
	}

	/**
	 * Loads properties from xml files
	 */
	public void loadProperties() {
		defaultProperties = new JFritzProperties();
		properties = new JFritzProperties(defaultProperties);

		// Default properties
		defaultProperties.setProperty("box.address", "192.168.178.1");
		defaultProperties.setProperty("box.password", Encryption.encrypt(""));
		defaultProperties.setProperty("country.prefix", "00");
		defaultProperties.setProperty("area.prefix", "0");
		defaultProperties.setProperty("country.code", "49");
		defaultProperties.setProperty("area.code", "441");
		defaultProperties.setProperty("fetch.timer", "5");

		try {
			FileInputStream fis = new FileInputStream(JFritz.PROPERTIES_FILE);
			properties.loadFromXML(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			Debug.err("File " + JFritz.PROPERTIES_FILE
					+ " not found, using default values");
		} catch (Exception e) {
		}
	}

	/**
	 * Saves properties to xml files
	 */
	public void saveProperties() {

		properties.setProperty("position.left", Integer.toString(jframe
				.getLocation().x));
		properties.setProperty("position.top", Integer.toString(jframe
				.getLocation().y));
		properties.setProperty("position.width", Integer.toString(jframe
				.getSize().width));
		properties.setProperty("position.height", Integer.toString(jframe
				.getSize().height));

		Enumeration en = jframe.getCallerTable().getColumnModel().getColumns();
		int i = 0;
		while (en.hasMoreElements()) {
			int width = ((TableColumn) en.nextElement()).getWidth();
			properties.setProperty("column" + i + ".width", Integer
					.toString(width));
			i++;
		}

		try {
			FileOutputStream fos = new FileOutputStream(JFritz.PROPERTIES_FILE);
			properties.storeToXML(fos, "Properties for " + JFritz.PROGRAM_NAME
					+ " v" + JFritz.PROGRAM_VERSION);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		phonebook.saveToXMLFile(PHONEBOOK_FILE);
	}

	/**
	 * Displays balloon info message
	 * 
	 * @param msg
	 */
	public static void infoMsg(String msg) {
		System.out.println(msg);
		if (SYSTRAY_SUPPORT) {
			trayIcon.displayMessage(JFritz.PROGRAM_NAME, msg,
					TrayIcon.INFO_MESSAGE_TYPE);
		}
	}

	/**
	 * Displays balloon error message
	 * 
	 * @param msg
	 */
	public void errorMsg(String msg) {
		Debug.err(msg);
		if (SYSTRAY_SUPPORT) {
			trayIcon.displayMessage(JFritz.PROGRAM_NAME, msg,
					TrayIcon.ERROE_MESSAGE_TYPE);
		}
	}

	/**
	 * @return Returns the callerlist.
	 */
	public final CallerList getCallerlist() {
		return callerlist;
	}

	/**
	 * @return Returns the phonebook.
	 */
	public final PhoneBook getPhonebook() {
		return phonebook;
	}

	/**
	 * @return Returns the jframe.
	 */
	public final JFritzWindow getJframe() {
		return jframe;
	}

	/**
	 * @return Returns the fritzbox devices.
	 */
	public final Vector getDevices() {
		try {
			ssdpthread.join();
		} catch (InterruptedException e) {
		}
		return ssdpthread.getDevices();
	}

	/**
	 * @return Returns an internationalized message.
	 */
	public static String getMessage(String msg) {
		String i18n = "";
		try {
			i18n = messages.getString(msg);
		} catch (MissingResourceException e) {
			Debug.err("Can't find resource string for " + msg);
			i18n = msg;
		}
		return i18n;
	}

	// Property methods
	public static String getProperty(String property, String defaultValue) {
		return properties.getProperty(property, defaultValue);
	}

	public static String getProperty(String property) {
		return getProperty(property, "");
	}

	public static void setProperty(String property, String value) {
		properties.setProperty(property, value);
	}

	public static void removeProperty(String property) {
		properties.remove(property);
	}

}
