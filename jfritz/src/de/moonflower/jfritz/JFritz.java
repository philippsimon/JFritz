/**
 * $Id$
 * 
 * JFritz
 * http://jfritz.sourceforge.net/
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
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *
 * 
 * BUGS: bitte bei Sourceforge nachschauen und dort auch den Status �ndern
 * BUG: die Autoerkennung, ob telefond f�r Syslog richtig l�uft hat ein Sicherheitsloch. Nun kann jede IP auf Port 1011 zugreifen.
 * 
 * FeatureRequests: bitte bei Sourceforge nachschauen und dort auch den Status �ndern

 * CHANGELOG:
 * - Anrufen aus der Anrufliste heraus (noch nicht getestet)
 * TODO: Checken, ob alle Bibliotheken vorhanden sind
 * 
 * JFritz 0.4.6
 * - Reset-Button bei den Filtern deaktiviert alle Filter
 * - Neuer Filter: Kontextmen� bei "Verpasste Anrufe"-Filter
 * - Neuer Filter: Kommentarfilter
 * - Neuer Befehl f�r "Anrufmonitor - Externes Programm starten": %URLENCODE();
 * - Kompatibel zu FritzBox 5010 und 5012
 * - Automatische Erkennung der Firmware
 * - Bugfix: Danisahne-Mod wird richtig erkannt
 * - Bugfix: Outlook-Import (entfernen von Klammern)
 * - Bugfix: Anzeigefehler beim Start behoben
 * - Bugfix: Sortierfunktion beim Telefonbuch korrigiert
 * 
 * JFritz 0.4.5
 * - Unterst�tzung f�r FRITZ!Box Firmware .85
 * - Unterst�tzung f�r FRITZ!Box Firmware .87
 * - Unterst�tzung f�r FRITZ!Box Firmware .88
 * - Spalten sind jetzt frei verschiebbar
 * - Kommentarspalte hinzugef�gt
 * - Kommentar- und Anschlu�-Spalte k�nnen ausgeblendet werden
 * - Suche der FritzBox �ber UPNP/SSDP abschaltbar
 * - Telefonbuch nun nach allen Spalten sortierbar
 * - Beim Export merkt sich JFritz die Verzeichnisse
 * - Drucken der Anrufliste (und Export nach Excel, RTF, PDF, CSV, ...)
 * - Neue Kommandozeilenoption -n: Schaltet die Tray-Unterst�tzung aus
 * - Direkter Import von Outlook-Kontakten
 * - Datumsfilter unterst�tzt nun "Gestern"
 * - Unterst�tzung f�r die neue Version des Callmessage-Anrufomitors (http://www.evil-dead.org/traymessage/index.php4)
 * - Bugfix: Firmware konnte beim ersten Start nicht erkannt werden
 * - Bugfix: Spaltenbreite wurde nicht korrekt gespeichert
 * - Bugfix: Falsche SIP-ID bei gel�schten Eintr�gen
 * - Bugfix: Wenn Kurzwahl unbekannt war, wurde eine falsche Rufnummer angezeigt
 * - Bugfix: Anrufliste wird nur gel�scht, wenn mind. 1 Eintrag abgeholt wurde
 * 
 * Internal: 
 * - SipProvider-Informationen werden nicht mehr in den 
 * 	 jfritz.properties.xml sondern in jfritz.sipprovider.xml
 *   gespeichert.
 * - Zugriff auf SipProvider �ber jfritz.getSIPProviderTableModel() 
 * 
 * JFritz 0.4.4
 * - CallByCall information is saved (only 010xy and 0100yy)
 * - Added support for MacOSX Application Menu
 * - Telnet: Timeout handling
 * - Telnet-Callmonitor: support for username, password
 * - Syslog-Callmonitor: syslogd and telefond check configurable
 * - Added Callmessage-Callmonitor. See Thread-Nr. 178199 in IPPF
 * - Wait, when no network reachable (On startup, return of standby, ...)
 * - Added context menu to phonebook and callerlist
 * - New Callfilter: Route, Fixed call, CallByCall
 * - New Datefilter: Right click on date filter button
 * - Display more information in status bar
 * - Export to XML
 * - Export CallByCall to CSV
 * - Phonenumber with wildcard support (PhoneNumber-Type "main")
 * - Start external Program on incoming call (%Number, %Name, %Called)
 * - Bugfix: Syslog-Monitor get Callerlist on Restart
 * - Bugfix: Check for double entries in Callerlist
 * - Bugfix: Reverselookup on call
 * 
 * Internal:
 * - VCard Export moved from CallerTable to PhoneBook
 * 
 * 
 * JFritz 0.4.2
 * - CallByCall information is saved
 * - Added Phonebookfilter (Private Phonebook)
 * - Callerlist deleteable
 * - Advanced CSV-File
 * - Callmonitor with Telnet, Syslog, YAC
 * - Syslog passthrough
 * - CMD Option -e : Export CSV
 * - CMD Option -c : Clear Callerlist
 * - CMD Option -l : Debug to Logfile
 * - Bugfix: Statistic-Dialog uses box.ip not 192.168.178.1
 * - Bugfix: Compatibility to Java 1.4.2
 * - Bugfix: Passwords with special chars
 * - Bugfix: Some charset bugfixing
 * - Bugfix: Phonebook XML-Saving fixed (UTF-8 coding)
 * 
 * 
 * JFritz 0.4.0
 * - Systray minimizes JFrame
 * - Mobile filter inverted
 * - Removed participant support in favour of person
 * - Phonebook support
 * - Added commandline option --fetch
 * - Rewrote xml handler for phonebook
 * - Data statistics
 * - Call monitor with sound notification
 * - Crypted password
 * - Option for password check on program start
 * - Option for disabling sounds
 * 
 * Internal:
 * - Added PhoneNumber class
 * - Added PhoneType class
 * - Restructured packages
 * 
 * 
 * JFritz 0.3.6
 * - New mobile phone filter feature
 * - Systray support for Linux/Solaris/Windows
 * - Systray ballon messages for Linux/Solaris/Windows
 * - Browser opening on Unix platforms
 * - Bugfix: Call with same timestamp are collected
 * 
 * JFritz 0.3.4
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
 * JFritz 0.3.2:
 * - Saves and restores window position/size
 * - Saves and restores width of table columns
 * - CallTypeFilter works now (thanks to robotniko)
 * - Filter option is saved
 * - Added filter for calls without displayed number
 * - Total duration of calls now displayed in status bar
 * 
 * JFritz 0.3.0: Major release
 * - Compatibility for JRE 1.4.2
 * - Severel bugfixes
 * 
 * JFritz 0.2.8: 
 * - Bugfix: Firmware detection had nasty bug
 * - Bugfix: Firmware detection detects modded firmware properly
 * - Bugfix: RegExps adapted for modded firmware
 * - Support for SIP-Provider for fritzbox fon wlan
 * - Notify users whenn calls have been retrieved
 * - CSV Export
 * 
 * JFritz 0.2.6:
 * - Several bugfixes
 * - Support for Fritz!Boxes with modified firmware
 * - Improved config dialog
 * - Improved firmware detection
 * - Initial support f�r SIP-Provider
 * - Firmware/SIP-Provider are saved in config file
 * 
 * JFritz 0.2.4:
 * - Several bugfixes
 * - Improventsment on number resolution
 * - Optimized Reverse Lookup
 * 
 * JFritz 0.2.2:
 * - FRITZ!Box FON WLAN works again and is detected automatically.
 * - Target MSN is displayed
 * - Bugfixes for Reverse Lookup (Mobile phone numbers are filtered now)
 * - Nice icons for calltypes (Regular call, Area call, Mobile call)
 * - Several small bugfixes
 * 
 * JFritz 0.2.0: Major release
 * - Improved GUI, arranged colours for win32 platform
 * - New ToolBar with nice icons
 * - Bugfix: Not all calls had been retrieved from box
 * - Improved reverse lookup
 * - Automatic box detection (does not yet work perfectly)
 * - Internal class restructuring
 *  
 * JFritz 0.1.6:
 * - Calls are now saved in XML format
 * - Support for Fritz!Box 7050
 * 
 * JFritz 0.1.0:
 * - Initial version
 */

package de.moonflower.jfritz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
import de.moonflower.jfritz.dialogs.simple.MessageDlg;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.firmware.FritzBoxFirmware;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.CLIOptions;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.utils.JFritzProperties;
import de.moonflower.jfritz.utils.JFritzUtils;
import de.moonflower.jfritz.utils.CLIOption;
import de.moonflower.jfritz.utils.ReverseLookup;
import de.moonflower.jfritz.utils.network.SSDPdiscoverThread;
import de.moonflower.jfritz.utils.network.CallMonitor;
import de.moonflower.jfritz.dialogs.simple.CallMessageDlg;
import de.moonflower.jfritz.dialogs.sip.SipProviderTableModel;

/**
 * @author Arno Willig
 * 
 */
public final class JFritz {

    public final static String PROGRAM_NAME = "JFritz";

    public final static String PROGRAM_VERSION = "0.4.6";

    public final static String PROGRAM_URL = "http://www.jfritz.org/";

    public final static String PROGRAM_SECRET = "jFrItZsEcReT";

    public final static String DOCUMENTATION_URL = "http://jfritz.sourceforge.net/doc/";

    public final static String CVS_TAG = "$Id$";

    public final static String PROGRAM_AUTHOR = "Arno Willig <akw@thinkwiki.org>";

    public final static String PROPERTIES_FILE = "jfritz.properties.xml";

    public final static String CALLS_FILE = "jfritz.calls.xml";

    public final static String QUICKDIALS_FILE = "jfritz.quickdials.xml";

    public final static String PHONEBOOK_FILE = "jfritz.phonebook.xml";

    public final static String SIPPROVIDER_FILE = "jfritz.sipprovider.xml";

    public final static String CALLS_CSV_FILE = "calls.csv";

    public final static int SSDP_TIMEOUT = 1000;

    public final static int SSDP_MAX_BOXES = 3;

    public static boolean SYSTRAY_SUPPORT = false;

    public static boolean checkSystray = true;

    private JFritzProperties defaultProperties;

    private static JFritzProperties properties;

    private static ResourceBundle messages;

    private SystemTray systray;

    private JFritzWindow jframe;

    private SSDPdiscoverThread ssdpthread;

    private CallerList callerlist;

    private static TrayIcon trayIcon;

    private static PhoneBook phonebook;

    private static SipProviderTableModel sipprovider;

    private static URL ringSound, callSound;

    private CallMonitor callMonitor = null;

    private static String HostOS = "other";

    public static final int CALLMONITOR_START = 0;

    public static final int CALLMONITOR_STOP = 1;

    /**
     * Constructs JFritz object
     */
    public JFritz(boolean fetchCalls, boolean csvExport, String csvFileName,
            boolean clearList) {
        loadMessages(new Locale("de", "DE"));
        loadProperties();
        loadSounds();

        String osName = System.getProperty("os.name");
        Debug.msg("Betriebssystem: " + osName);
        if (osName.startsWith("Mac OS"))
            HostOS = "mac";
        else if (osName.startsWith("Windows"))
            HostOS = "windows";
        else if (osName.equals("Linux")) {
            HostOS = "linux";
        }
        Debug.msg("JFritz runs on " + HostOS);

        if (HostOS.equalsIgnoreCase("mac")) {
            new MacHandler(this);
        }
        autodetectFirmware();

        phonebook = new PhoneBook(this);
        phonebook.loadFromXMLFile(PHONEBOOK_FILE);

        sipprovider = new SipProviderTableModel();
        sipprovider.loadFromXMLFile(SIPPROVIDER_FILE);

        callerlist = new CallerList(this);
        callerlist.loadFromXMLFile(CALLS_FILE);

        Debug.msg("Start des commandline parsing");
        if (fetchCalls) {
            Debug.msg("Anrufliste wird von Fritz!Box geholt..");
            try {
                callerlist.getNewCalls();
            } catch (WrongPasswordException e) {
                Debug.err(e.toString());
            } catch (IOException e) {
                Debug.err(e.toString());
            } finally {
                if (csvExport) {
                    Debug.msg("CSV-Export to " + csvFileName);
                    callerlist.saveToCSVFile(csvFileName, true);
                }
                if (clearList) {
                    Debug.msg("Clearing Caller List");
                    callerlist.clearList();
                }
                Debug.msg("JFritz beendet sich nun.");
                System.exit(0);
            }
        }
        if (csvExport) {
            Debug.msg("CSV-Export to " + csvFileName);
            callerlist.saveToCSVFile(csvFileName, true);
            if (clearList) {
                Debug.msg("Clearing Caller List");
                callerlist.clearList();
            }
            System.exit(0);
        }
        if (clearList) {
            Debug.msg("Clearing Caller List");
            callerlist.clearList();
            System.exit(0);
        }

        Debug.msg("Neue Instanz von JFrame");
        jframe = new JFritzWindow(this);

        Debug.msg("Checke Systray-Support");
        if (checkForSystraySupport()) {
            try {
                systray = SystemTray.getDefaultSystemTray();
                createTrayMenu();
            } catch (Exception e) {
                Debug.err(e.toString());
                SYSTRAY_SUPPORT = false;
            }
        }

        if (JFritzUtils.parseBoolean(JFritz.getProperty("option.useSSDP",
                "true"))) {
            Debug.msg("Suche FritzBox �ber UPnP / SSDP");

            ssdpthread = new SSDPdiscoverThread(this, SSDP_TIMEOUT);
            ssdpthread.start();
            try {
                ssdpthread.join();
            } catch (InterruptedException ie) {

            }
        }

        jframe.checkStartOptions();

        javax.swing.SwingUtilities.invokeLater(jframe);

    }

    /**
     * Loads sounds from resources
     */
    private void loadSounds() {
        ringSound = getClass().getResource(
                "/de/moonflower/jfritz/resources/sounds/call_in.wav");
        callSound = getClass().getResource(
                "/de/moonflower/jfritz/resources/sounds/call_out.wav");
    }

    /**
     * Checks for systray availability
     */
    private boolean checkForSystraySupport() {
        if (!checkSystray)
            return false;
        String os = System.getProperty("os.name");
        if (os.equals("Linux") || os.equals("Solaris")
                || os.startsWith("Windows")) {
            SYSTRAY_SUPPORT = true;
        }
        return SYSTRAY_SUPPORT;
    }

    /**
     * Main method for starting JFritz
     * 
     * @param args
     *            Program arguments (-h -v ...)
     */
    public static void main(String[] args) {
        System.out.println(PROGRAM_NAME + " v" + PROGRAM_VERSION
                + " (c) 2005 by " + PROGRAM_AUTHOR);

        boolean fetchCalls = false;
        boolean clearList = false;
        boolean csvExport = false;
        String csvFileName = "";

        CLIOptions options = new CLIOptions();

        options.addOption('h', "help", null, "This short description");
        options.addOption('v', "verbose", null, "Turn on debug information");
        options.addOption('v', "debug", null, "Turn on debug information");
        options.addOption('s', "systray", null, "Turn on systray support");
        options.addOption('n', "nosystray", null, "Turn off systray support");
        options.addOption('f', "fetch", null, "Fetch new calls and exit");
        options.addOption('c', "clear_list", null,
                "Clears Caller List and exit");
        options.addOption('e', "export", "filename",
                "Fetch calls and export to CSV file.");
        options.addOption('l', "logfile", "filename",
                "Writes debug messages to logfile");

        Vector foundOptions = options.parseOptions(args);
        Enumeration en = foundOptions.elements();
        while (en.hasMoreElements()) {
            CLIOption option = (CLIOption) en.nextElement();

            switch (option.getShortOption()) {
            case 'h':
                System.out.println("Call: java -jar jfritz.jar [Options]");
                options.printOptions();
                System.exit(0);
                break;
            case 'v':
                Debug.on();
                break;
            case 's':
                JFritz.SYSTRAY_SUPPORT = true;
                break;
            case 'f':
                fetchCalls = true;
                break;
            case 'e':
                csvExport = true;
                csvFileName = option.getParameter();
                if (csvFileName == null) {
                    System.err.println("Parameter not found!");
                    System.exit(0);
                }
                break;
            case 'c':
                clearList = true;
                break;
            case 'l':
                String logFilename = option.getParameter();
                if (logFilename == null) {
                    System.err.println("Parameter not found!");
                    System.exit(0);
                } else {
                    Debug.logToFile(logFilename);
                    break;
                }
            case 'n':
                checkSystray = false;
            default:
                break;
            }
        }
        new JFritz(fetchCalls, csvExport, csvFileName, clearList);
    }

    /**
     * Creates the tray icon menu
     */
    private void createTrayMenu() {
        System.setProperty("javax.swing.adjustPopupLocationToFit", "false");

        JPopupMenu menu = new JPopupMenu("JFritz Menu");
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
                        .getResource("/de/moonflower/jfritz/resources/images/trayicon.png"));

        trayIcon = new TrayIcon(icon, "JFritz", menu);
        trayIcon.setIconAutoSize(false);
        trayIcon
                .setCaption(JFritz.PROGRAM_NAME + " v" + JFritz.PROGRAM_VERSION);
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideShowJFritz();
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
     * Replace old property values with new one p.e. column0.width =>
     * column.type.width
     * 
     */
    private void replaceOldProperties() {
        for (int i = 0; i < 10; i++) {
            JFritz.removeProperty("SIP" + i);
        }

        if (properties.contains("column0.width")) {
            properties.setProperty("column." + getMessage("type") + ".width",
                    properties.getProperty("column0.width"));
            JFritz.removeProperty("column0.width");
        } else if (properties.contains("column1.width")) {
            properties.setProperty("column." + getMessage("date") + ".width",
                    properties.getProperty("column1.width"));
            JFritz.removeProperty("column1.width");
        } else if (properties.contains("column2.width")) {
            properties.setProperty("column." + "Call-By-Call" + ".width",
                    properties.getProperty("column2.width"));
            JFritz.removeProperty("column2.width");
        } else if (properties.contains("column3.width")) {
            properties.setProperty("column." + getMessage("number") + ".width",
                    properties.getProperty("column3.width"));
            JFritz.removeProperty("column3.width");
        } else if (properties.contains("column4.width")) {
            properties.setProperty("column." + getMessage("participant")
                    + ".width", properties.getProperty("column4.width"));
            JFritz.removeProperty("column4.width");
        } else if (properties.contains("column5.width")) {
            properties.setProperty("column." + getMessage("port") + ".width",
                    properties.getProperty("column5.width"));
            JFritz.removeProperty("column5.width");
        } else if (properties.contains("column6.width")) {
            properties.setProperty("column." + getMessage("route") + ".width",
                    properties.getProperty("column6.width"));
            JFritz.removeProperty("column6.width");
        } else if (properties.contains("column7.width")) {
            properties.setProperty("column." + getMessage("duration")
                    + ".width", properties.getProperty("column7.width"));
            JFritz.removeProperty("column7.width");
        } else if (properties.contains("column8.width")) {
            properties.setProperty("column." + "Kommentar" + ".width",
                    properties.getProperty("column8.width"));
            JFritz.removeProperty("column8.width");
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
            replaceOldProperties();
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

        removeProperty("state.warningFreeminutesShown"); // don't save
        // warningState

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
            TableColumn col = (TableColumn) en.nextElement();

            properties.setProperty("column." + col.getHeaderValue().toString()
                    + ".width", Integer.toString(col.getWidth()));
            properties.setProperty("column" + i + ".name", col.getHeaderValue()
                    .toString());
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
    }

    /**
     * Displays balloon info message
     * 
     * @param msg
     *            Message to be displayed
     */
    public static void infoMsg(String msg) {
        switch (Integer.parseInt(JFritz.getProperty("option.popuptype", "1"))) {
        case 0: { // No Popup
            break;
        }
        case 1: {
            MessageDlg msgDialog = new MessageDlg();
            msgDialog.showMessage(msg);
            break;
        }
        case 2: {
            trayIcon.displayMessage(JFritz.PROGRAM_NAME, msg,
                    TrayIcon.INFO_MESSAGE_TYPE);
            break;
        }
        }
    }

    /**
     * Display call monitor message
     * 
     * @param caller
     *            Caller number
     * @param called
     *            Called number
     */
    public void callInMsg(String caller, String called) {
        callInMsg(caller, called, "");
    }

    /**
     * Display call monitor message
     * 
     * @param caller
     *            Caller number
     * @param called
     *            Called number
     * @param name
     *            Known name (only YAC)
     */
    public void callInMsg(String caller, String called, String name) {

        Debug.msg("Caller: " + caller);
        Debug.msg("Called: " + called);
        Debug.msg("Name: " + name);

        String callerstr = "", calledstr = "";
        if (name.equals("")) {
            name = "Unbekannt";
        }
        if (caller.equals("")) {
            caller = "Unbekannt";
        }
        if (called.equals("")) {
            calledstr = "Unbekannt";
        } else
            calledstr = getSIPProviderTableModel().getSipProvider(called,
                    called);

        PhoneNumber callerPhoneNumber = new PhoneNumber(caller);
        if (name.equals("Unbekannt") && !caller.equals("Unbekannt")) {
            Debug.msg("Searchin in local database ...");
            Person callerperson = phonebook.findPerson(callerPhoneNumber);
            if (callerperson != null) {
                name = callerperson.getFullname();
                Debug.msg("Found in local database: " + name);
                Vector numbers = new Vector();
                numbers = callerperson.getNumbers();
                Enumeration en = numbers.elements();
                while (en.hasMoreElements()) {
                    PhoneNumber checkNumber = (PhoneNumber) en.nextElement();
                    if (checkNumber.getType().startsWith("main")) {
                        String number = checkNumber.getIntNumber();
                        if (callerPhoneNumber.getIntNumber().startsWith(number)) {
                            String prefix = callerPhoneNumber.getIntNumber()
                                    .substring(0, number.length());
                            String extension = callerPhoneNumber.getIntNumber()
                                    .substring(number.length());
                            if (extension.length() > 0) {
                                caller = prefix + " - " + extension;
                            } else {
                                caller = prefix;
                            }
                        }
                    }
                }
            } else {
                Debug.msg("Searchin on dasoertliche.de ...");
                Person person = ReverseLookup.lookup(callerPhoneNumber);
                if (!person.getFullname().equals("")) {
                    name = person.getFullname();
                    Debug.msg("Found on dasoertliche.de: " + name);
                    Debug.msg("Add person to database");
                    phonebook.addEntry(person);
                    phonebook.fireTableDataChanged();
                    caller = callerPhoneNumber.getIntNumber();
                }
            }
        }

        if (name.equals("Unbekannt")) {
            callerstr = caller;
        } else {
            callerstr = caller + " (" + name + ")";
        }

        Debug.msg("Caller: " + callerstr);
        Debug.msg("Called: " + calledstr);
        Debug.msg("Name: " + name);

        switch (Integer.parseInt(JFritz.getProperty("option.popuptype", "1"))) {
        case 0: { // No Popup
            break;
        }
        case 1: {
            CallMessageDlg msgDialog = new CallMessageDlg();
            msgDialog.showMessage(callerstr, calledstr);
            break;
        }
        case 2: {
            String outstring = JFritz.getMessage("incoming_call") + "\nvon "
                    + callerstr;
            if (!calledstr.equals("Unbekannt")) {
                outstring = outstring + "\nan " + calledstr;
            }
            JFritz.infoMsg(outstring);
            break;
        }
        }

        if (JFritzUtils.parseBoolean(JFritz.getProperty("option.playSounds",
                "true"))) {
            playSound(ringSound);
        }

        if (JFritzUtils.parseBoolean(JFritz.getProperty(
                "option.startExternProgram", "false"))) {
            String programString = JFritz.getProperty("option.externProgram",
                    "");

            programString = programString.replaceAll("%Number", caller);
            programString = programString.replaceAll("%Name", name);
            programString = programString.replaceAll("%Called", called);

            if (programString.indexOf("%URLENCODE") > -1) {
                try {
                    Pattern p;
                    p = Pattern.compile("%URLENCODE\\(([^;]*)\\);");
                    Matcher m = p.matcher(programString);
                    while (m.find()) {
                        String toReplace = m.group();                        
                        toReplace = toReplace.replaceAll("\\\\", "\\\\\\\\");                       
                        toReplace = toReplace.replaceAll("\\(", "\\\\(");
                        toReplace = toReplace.replaceAll("\\)", "\\\\)");                        
                        String toEncode = m.group(1);
                        programString = programString.replaceAll(toReplace, URLEncoder.encode(toEncode, "UTF-8"));
                    }
                } catch (UnsupportedEncodingException uee) {
                    Debug.err("JFritz.class: UnsupportedEncodingException: "
                            + uee.toString());
                }
            }

            if (programString.equals("")) {
                Debug
                        .errDlg("Kein externes Programm angegeben"
                                + programString);
                return;
            }
            Debug.msg("Starte externes Programm: " + programString);
            try {
                Runtime.getRuntime().exec(programString);
            } catch (IOException e) {
                Debug.errDlg("Konnte externes Programm nicht ausf�hren: "
                        + programString);
                e.printStackTrace();
            }
        }

    }

    /**
     * Display call monitor message
     * 
     * @param called
     *            Called number
     */
    public static void callOutMsg(String called) {
        String calledstr = "";
        Debug.msg("Called: " + called);

        infoMsg("Ausgehender Telefonanruf\n " + "\nan " + calledstr + "!");
        if (JFritzUtils.parseBoolean(JFritz.getProperty("option.playSounds",
                "true"))) {
            playSound(callSound);
        }
    }

    /**
     * Plays a sound by a given resource URL
     * 
     * @param sound
     *            URL of sound to be played
     */
    public static void playSound(URL sound) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
            DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat(),
                    ((int) ais.getFrameLength() * ais.getFormat()
                            .getFrameSize()));
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(ais);
            clip.start();
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
                if (!clip.isRunning()) {
                    break;
                }
            }
            clip.stop();
            clip.close();
        } catch (UnsupportedAudioFileException e) {
        } catch (IOException e) {
        } catch (LineUnavailableException e) {
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
                    TrayIcon.ERROR_MESSAGE_TYPE);
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
        if (JFritzUtils.parseBoolean(JFritz.getProperty("option.useSSDP",
                "true"))) {
            try {
                ssdpthread.join();
            } catch (InterruptedException e) {
            }
            return ssdpthread.getDevices();
        } else
            return null;
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

    /**
     * 
     * @param property
     *            Property to get the value from
     * @param defaultValue
     *            Default value to be returned if property does not exist
     * @return Returns value of a specific property
     */
    public static String getProperty(String property, String defaultValue) {
        return properties.getProperty(property, defaultValue);
    }

    /**
     * 
     * @param property
     *            Property to get the value from
     * @return Returns value of a specific property
     */
    public static String getProperty(String property) {
        return getProperty(property, "");
    }

    /**
     * Sets a property to a specific value
     * 
     * @param property
     *            Property to be set
     * @param value
     *            Value of property
     */
    public static void setProperty(String property, String value) {
        properties.setProperty(property, value);
    }

    /**
     * Removes a property
     * 
     * @param property
     *            Property to be removed
     */
    public static void removeProperty(String property) {
        properties.remove(property);
    }

    public void stopCallMonitor() {
        if (callMonitor != null) {
            callMonitor.stopCallMonitor();
            // Let buttons enable start of callMonitor
            getJframe().setCallMonitorButtons(CALLMONITOR_START);
            callMonitor = null;
        }
    }

    public CallMonitor getCallMonitor() {
        return callMonitor;
    }

    public void setCallMonitor(CallMonitor cm) {
        callMonitor = cm;
    }

    public static String runsOn() {
        return HostOS;
    }

    public void hideShowJFritz() {
        if (jframe.isVisible()) {
            Debug.msg("Hide JFritz-Window");
            jframe.setState(JFrame.ICONIFIED);
            jframe.setVisible(false);
        } else {
            Debug.msg("Show JFritz-Window");
            jframe.setState(JFrame.NORMAL);
            jframe.setVisible(true);
            jframe.toFront();
        }
    }

    public SipProviderTableModel getSIPProviderTableModel() {
        return sipprovider;
    }
    
    private void autodetectFirmware() {
        FritzBoxFirmware firmware;
        try {
            firmware = FritzBoxFirmware.detectFirmwareVersion(
                    JFritz
                    .getProperty("box.address"),
            Encryption
                    .decrypt(JFritz
                            .getProperty("box.password")));

        } catch (WrongPasswordException e1) {
            Debug.err("Password wrong!");
            firmware = null;
        } catch (IOException e1) {
            Debug.err("Address wrong!");
            firmware = null;
        }
        if (firmware != null) {
            Debug.msg("Found FritzBox-Firmware: "+firmware.getFirmwareVersion());
            JFritz.setProperty("box.firmware", firmware.getFirmwareVersion());
        } else {
            Debug.msg("Found no FritzBox-Firmware");
            JFritz.removeProperty("box.firmware");
        }
    }
}