package de.moonflower.jfritz.struct;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.dialogs.quickdial.QuickDials;
import de.moonflower.jfritz.dialogs.sip.SipProvider;
import de.moonflower.jfritz.exceptions.InvalidFirmwareException;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.firmware.FritzBoxFirmware;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.utils.HTMLUtil;
import de.moonflower.jfritz.utils.JFritzUtils;
import de.moonflower.jfritz.utils.network.AddonInfosListener;
import de.moonflower.jfritz.utils.network.AddonInfosXMLHandler;
import de.moonflower.jfritz.utils.network.UPNPUtils;

public class FritzBox {
	
	private static String POSTDATA_CLEAR = "&var%3Alang=$LANG&var%3Apagename=foncalls&var%3Amenu=fon&telcfg%3Asettings/ClearJournal=1"; //$NON-NLS-1$
	
	private static String POSTDATA_SIPPROVIDER = "&var%3Alang=$LANG&var%3Amenu=fon&var%3Apagename=siplist&login%3Acommand%2Fpassword="; //$NON-NLS-1$

	private static String POSTDATA_LIST = "&var%3Alang=$LANG&var%3Amenu=fon&var%3Apagename=foncalls&login%3Acommand%2Fpassword="; //$NON-NLS-1$

	private static String POSTDATA_FETCH_CALLERLIST = "getpage=../html/$LANG/$CSV_FILE&errorpage=..%2Fhtml%2F$LANG%2Fmenus%2Fmenu2.html&var%3Alang=$LANG&var%3Apagename=foncalls&var%3Aerrorpagename=foncalls&var%3Amenu=fon&var%3Apagemaster=&time%3Asettings%2Ftime=1136559837%2C-60";

	private static String POSTDATA_QUICKDIAL = "&var%3Alang=$LANG&var%3Amenu=fon&var%3Apagename=kurzwahlen&login%3Acommand%2Fpassword="; //$NON-NLS-1$

	private static String POSTDATA_QUICKDIAL_NEW = "&var%3Alang=$LANG&var%3Amenu=fon&var%3Apagename=fonbuch&login%3Acommand%2Fpassword="; //$NON-NLS-1$

	private static String POSTDATA_LOGIN = "&login:command/password=$PASSWORT"; //$NON-NLS-1$

	private static String POSTDATA_CALL = "&login:command/password=$PASSWORT&telcfg:settings/UseClickToDial=1&telcfg:settings/DialPort=$NEBENSTELLE&telcfg:command/Dial=$NUMMER"; //$NON-NLS-1$
	
	
	//the following are strings used by the web services on the box
	//from XX.04.33 onwards
	private static String URL_SERVICE_ADDONINFOS = ":49000/upnp/control/WANCommonIFC1";  //$NON-NLS-1$
	
	private static String URN_SERVICE_ADDONINFOS = "urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1#GetAddonInfos"; //$NON-NLS-1$
	
	private static String URL_SERVICE_DSLLINK = ":49000/upnp/control/WANDSLLinkC1";
	
	private static String URN_SERVICE_DSLLINK = "urn:schemas-upnp-org:service: WANDSLLinkConfig:1#GetDSLLinkInfo";
	
	private static String URL_SERVICE_EXTERNALIP = ":49000/upnp/control/WANIPConn1";
	
	private static String URN_SERVICE_EXTERNALIP = "urn:schemas-upnp-org:service:WANIPConnection:1#GetExternalIPAddress";
	
	private static String URL_SERVICE_STATUSINFO = ":49000/upnp/control/WANIPConn1";
	                
	private static String URN_SERVICE_STATUSINFO = "urn:schemas-upnp-org:service:WANIPConnection:1#GetStatusInfo";

	private static String URL_SERVICE_COMMONLINK= ":49000/upnp/control/WANCommonIFC1";
	
	private static String URN_SERVICE_COMMONLINK = "urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1#GetCommonLinkProperties";
	
	
	private final static String CSV_FILE_EN = "FRITZ!Box_Calllist.csv";

	private final static String CSV_FILE_DE = "FRITZ!Box_Anrufliste.csv";

	private final static String PATTERN_SIPPROVIDER_OLD = "<!-- \"(\\d)\" / \"(\\w*)\" -->" //$NON-NLS-1$ 
		// FW <= 37
		+ "\\s*<td class=\"c1\">\\s*<input type=checkbox id=\"uiViewActivsip\\d\"" //$NON-NLS-1$
		+ "\\s*onclick=\"uiOnChangeActivated\\('uiViewActivsip\\d','uiPostActivsip\\d'\\); return true;\">" //$NON-NLS-1$
		+ "\\s*</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c2\">([^<]*)</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c3\"><script type=\"text/javascript\">document.write\\(ProviderDisplay\\(\"([^\"]*)\"\\)\\);</script></td>"; //$NON-NLS-1$

	private final static String PATTERN_SIPPROVIDER_37_91 = "<!-- \"(\\d)\" / \"(\\w*)\" -->" //$NON-NLS-1$ 
		// 37 < FW <= 91
		+ "\\s*<td class=\"c1\">\\s*<input type=checkbox id=\"uiViewActivsip\\d\"" //$NON-NLS-1$
		+ "\\s*onclick=\"uiOnChangeActivated\\('uiViewActivsip\\d','uiPostActivsip\\d'\\); return true;\">" //$NON-NLS-1$
		+ "\\s*</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c2\">([^<]*)</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c3\"><script type=\"text/javascript\">document.write\\(ProviderDisplay\\(\"([^\"]*)\"\\)\\);</script></td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c6\"><script type=\"text/javascript\">document.write\\(AuswahlDisplay\\(\"([^\"]*)\"\\)\\);</script></td>"; //$NON-NLS-1$

	private final static String PATTERN_SIPPROVIDER_96 = "<!--\\s*\"(\\d)\"\\s*/\\s*\"(\\w*)\"\\s*/\\s*\"\\w*\"\\s*-->" //$NON-NLS-1$ 
		// FW >= 96
		+ "\\s*<td class=\"c1\">\\s*<input type=checkbox id=\"uiViewActivsip\\d\"" //$NON-NLS-1$
		+ "\\s*onclick=\"uiOnChangeActivated\\('uiViewActivsip\\d','uiPostActivsip\\d'\\); return true;\">" //$NON-NLS-1$
		+ "\\s*</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c2\">([^<]*)</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c3\"><script type=\"text/javascript\">document.write\\(ProviderDisplay\\(\"([^\"]*)\"\\)\\);</script></td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c6\"><script type=\"text/javascript\">document.write\\(AuswahlDisplay\\(\"([^\"]*)\"\\)\\);</script></td>"; //$NON-NLS-1$

	private final static String PATTERN_SIPPROVIDER_ACTIVE = "<input type=\"hidden\" name=\"sip:settings/sip(\\d)/activated\" value=\"(\\d)\" id=\"uiPostActivsip"; //$NON-NLS-1$	
	
	private final static String PATTERN_QUICKDIAL = "<tr class=\"Dialoglist\">" //$NON-NLS-1$
		+ "\\s*<td style=\"text-align: center;\">(\\d*)</td>" //$NON-NLS-1$
		+ "\\s*<td>(\\w*)</td>" //$NON-NLS-1$
		+ "\\s*<td>([^<]*)</td>" //$NON-NLS-1$
		+ "\\s*<td style=\"text-align: right;\"><button [^>]*>\\s*<img [^>]*></button></td>" //$NON-NLS-1$
		+ "\\s*<td style=\"text-align: right;\"><button [^>]*>\\s*<img [^>]*></button></td>" //$NON-NLS-1$
		+ "\\s*</tr>"; //$NON-NLS-1$
	
	private final static String PATTERN_QUICKDIAL_NEW = "<td class=\"c5\"><span title=\"[^\"]*\">([^<]*)</span></td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c3\"><span title=\"[^\"]*\">([^<]*)</span></td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c1\">(\\d*)</td>" //$NON-NLS-1$
		+ "\\s*<td class=\"c2\"><span title=\"[^\"]*\">([^<]*)"; //$NON-NLS-1$

	private final static String PATTERN_QUICKDIAL_BETA = "<script type=\"text/javascript\">document.write\\(TrFon\\(\"[^\"]*\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"\\)\\);</script>";

	
	private FritzBoxFirmware firmware;
	
	private String box_address;
	
	private String box_password;
	
	private String box_port;
	
	private static int[] quickdial_indizes = { -1, -1, -1, -1 };
	
	private final static int VANITY = 0;

	private final static int NAME = 1;

	private final static int QUICKDIAL = 2;

	private final static int NUMBER = 3;
	
	public FritzBox ( String address, String password, String port) {
		box_address = address;
		box_password = password;
		box_port = port;
		detectFirmware();
	}

	public void updateSettings() {
		box_address = Main.getProperty("box.address","192.168.178.1");
		box_password = Encryption.decrypt(Main.getProperty("box.password",Encryption.encrypt("")));
		box_port = Main.getProperty("box.port","80");
		detectFirmware();
	}
	
	/**
	 * Detects firmware version
	 * @return
	 */
	public void detectFirmware() {
		try {
			
			//avoid trying to access the box if running as a client
			if(Main.getProperty("network.type", "0").equals("2")
					&& Boolean.parseBoolean(Main.getProperty("option.clientCallList", "false"))){
					
				Debug.netMsg("JFritz is running as a client and using call list from server, canceling firmware detection");
			
			}else{
				firmware = FritzBoxFirmware.detectFirmwareVersion(
						box_address,
						box_password,
						box_port);
			}
		} catch (WrongPasswordException e) {
			Debug.msg(Main.getMessage("box.wrong_password"));
		} catch (InvalidFirmwareException e) {
			Debug.msg(Main.getMessage("box.address_wrong"));
		} catch (IOException e) {				
			Debug.msg(Main.getMessage("box.address_wrong"));
		}
	}
		
	/**
	 * Returns reference to firmware
	 * @return
	 */
	public FritzBoxFirmware getFirmware() {
		return firmware;
	}
	
	/**
	 * clears the caller list on the fritz box
	 * 
	 * @throws IOException
	 * @throws WrongPasswordException
	 */
	public void clearListOnFritzBox() throws WrongPasswordException,
			IOException {
		Debug.msg("Clearing List"); //$NON-NLS-1$
		String urlstr = "http://" + box_address + ":" + box_port + "/cgi-bin/webcm"; //$NON-NLS-1$,  //$NON-NLS-2$
		String postdata = firmware.getAccessMethod()
				+ POSTDATA_CLEAR.replaceAll("\\$LANG", firmware.getLanguage());
		fetchDataFromURL(urlstr, postdata, true);
	}
	
	/**
	 * fetches html data from url using POST requests
	 * 
	 * @param urlstr
	 * @param postdata
	 * @return html data
	 * @throws WrongPasswordException
	 * @throws IOException
	 */
	public String fetchDataFromURL(String urlstr, String postdata,
			boolean retrieveData) throws WrongPasswordException, IOException {
		URL url = null;
		URLConnection urlConn;
		DataOutputStream printout;
		String data = ""; //$NON-NLS-1$
		boolean wrong_pass = false;
		Debug.msg("Urlstr: " + urlstr);
		Debug.msg("Postdata: " + postdata);

		try {
			url = new URL(urlstr);
		} catch (MalformedURLException e) {
			Debug.err("URL invalid: " + urlstr); //$NON-NLS-1$
			throw new WrongPasswordException("URL invalid: " + urlstr); //$NON-NLS-1$
		}

		if (url != null) {
			urlConn = url.openConnection();
			// 5 Sekunden-Timeout f�r Verbindungsaufbau
			urlConn.setConnectTimeout(5000);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			// Sending postdata
			if (postdata != null) {
				urlConn.setRequestProperty("Content-Type", //$NON-NLS-1$
						"application/x-www-form-urlencoded"); //$NON-NLS-1$
				printout = new DataOutputStream(urlConn.getOutputStream());
				printout.writeBytes(postdata);
				printout.flush();
				printout.close();
			}

			BufferedReader d;

			try {
				// Get response data
				d = new BufferedReader(new InputStreamReader(urlConn
						.getInputStream()));
				String str;
				while (null != ((str = HTMLUtil.stripEntities(d.readLine())))) {
					// Password seems to be wrong
					if (str.indexOf("FRITZ!Box Anmeldung") > 0) //$NON-NLS-1$
						wrong_pass = true;
					if (retrieveData)
						data += str;
				}
				d.close();
		
				
			} catch (IOException e1) {
				throw new IOException("Network unavailable"); //$NON-NLS-1$
			}

			if (wrong_pass)
				throw new WrongPasswordException("Password invalid"); //$NON-NLS-1$
		}
		
		return data;
	}	
	
	/**
	 * retrieves vector of SipProviders stored in the FritzBox
	 * 
	 * @return Vector of SipProvider
	 * @throws WrongPasswordException
	 * @throws IOException
	 *             author robotniko
	 * @throws InvalidFirmwareException
	 */
	public Vector<SipProvider> retrieveSipProvider()
			throws WrongPasswordException, IOException,
			InvalidFirmwareException {
		if (firmware == null)
			throw new InvalidFirmwareException("No valid firmware"); //$NON-NLS-1$
		String postdata = firmware.getAccessMethod()
				+ POSTDATA_SIPPROVIDER.replaceAll("\\$LANG", firmware
						.getLanguage())
				+ URLEncoder.encode(box_password, "ISO-8859-1"); //$NON-NLS-1$
		String urlstr = "http://" + box_address + ":" + box_port + "/cgi-bin/webcm"; //$NON-NLS-1$,  //$NON-NLS-2$
		Debug.msg("Postdata: " + postdata); //$NON-NLS-1$
		Debug.msg("Urlstr: " + urlstr); //$NON-NLS-1$
		String data = fetchDataFromURL(urlstr, postdata, true);

		// DEBUG: Test other versions
		if (false) {
			String filename = "./Firmware 96/sipProvider.html"; //$NON-NLS-1$
			Debug.msg("Debug mode: Loading " + filename); //$NON-NLS-1$
			try {
				data = ""; //$NON-NLS-1$
				String thisLine;
				BufferedReader in = new BufferedReader(new FileReader(filename));
				while ((thisLine = in.readLine()) != null) {
					data += thisLine;
				}
				in.close();
			} catch (IOException e) {
				Debug.err("File not found: " + filename); //$NON-NLS-1$
			}
		}

		Vector<SipProvider> list = parseSipProvider(data, firmware);
		return list;
	}
	
	/**
	 * parses html data from the fritz!box's web interface and retrieves SIP
	 * information.
	 * 
	 * @param data
	 *            html data
	 * @return list of SipProvider objects author robotniko, akw
	 */
	public Vector<SipProvider> parseSipProvider(String data, FritzBoxFirmware firmware) {
		Vector<SipProvider> list = new Vector<SipProvider>();
		data = JFritzUtils.removeDuplicateWhitespace(data);
		Pattern p;
		if (firmware.getMajorFirmwareVersion() == 3
				&& firmware.getMinorFirmwareVersion() < 42)
			p = Pattern.compile(PATTERN_SIPPROVIDER_OLD);
		else if (firmware.getMajorFirmwareVersion() == 3
				&& firmware.getMinorFirmwareVersion() < 96)
			p = Pattern.compile(PATTERN_SIPPROVIDER_37_91);
		else
			p = Pattern.compile(PATTERN_SIPPROVIDER_96);
		Matcher m = p.matcher(data);
		while (m.find()) {
			Debug.msg("FOUND SIP-PROVIDER"); //$NON-NLS-1$
			if (!(m.group(4).equals(""))) { //$NON-NLS-1$
				if (firmware.getMajorFirmwareVersion() == 3
						&& firmware.getMinorFirmwareVersion() < 42)
					list.add(new SipProvider(Integer.parseInt(m.group(1)), m
							.group(3), m.group(4)));
				else
					list.add(new SipProvider(Integer.parseInt(m.group(5)), m
							.group(3), m.group(4)));

				Debug.msg("SIP-Provider: " + list.lastElement()); //$NON-NLS-1$
			}
		}
		p = Pattern.compile(PATTERN_SIPPROVIDER_ACTIVE);
		m = p.matcher(data);
		while (m.find()) {
			Enumeration<SipProvider> en = list.elements();
			while (en.hasMoreElements()) {
				SipProvider sipProvider = en.nextElement();
				if (sipProvider.getProviderID() == Integer.parseInt(m.group(1))) {
					if (Integer.parseInt(m.group(2)) == 1) {
						sipProvider.setActive(true);
					} else {
						sipProvider.setActive(false);
					}
				}
			}
		}

		return list;
	}

	/**
	 * This function fetches the call list from the box it first gets a
	 * connection to the fritz!Box opens the call list html page, then it sends
	 * a request to the box for the csv file then passes a bufferedReader to
	 * CallerList.importFromCSV() it passes back to the caller if there were new
	 * callers or not NOTE: Function no longer checks for invalid passwords!
	 * 
	 * 
	 * LAST MODIFIED: 14.04.06 Brian Jensen
	 * 
	 * @author Brian Jensen
	 * 
	 * @return if there were new calls or not
	 * @throws WrongPasswordException
	 * @throws IOException
	 */

	public boolean retrieveCSVList()
			throws WrongPasswordException, IOException {

		URL url;
		URLConnection urlConn;
		DataOutputStream printout;
		boolean wrong_pass = false;
		boolean newEntries = false;
		Debug.msg("Opening HTML Callerlist page");
		// retrieveHTMLCallerList(box_address, password, countryPrefix,
		// countryCode,
		// areaPrefix, areaCode, firmware, jfritz);

		// Attempting to fetch the html version of the call list
		String postdata = firmware.getAccessMethod()
				+ POSTDATA_LIST.replaceAll("\\$LANG", firmware.getLanguage())
				+ URLEncoder.encode(box_password, "ISO-8859-1");
		String urlstr = "http://" + box_address +":" + box_port + "/cgi-bin/webcm";

		Debug.msg("Urlstr: " + urlstr);
		Debug.msg("Postdata: " + postdata);

		try {
			url = new URL(urlstr);
		} catch (MalformedURLException e) {
			Debug.err("URL invalid: " + urlstr);
			throw new WrongPasswordException("URL invalid: " + urlstr);
		}

		if (url != null) {
			urlConn = url.openConnection();
			// 5 Sekunden-Timeout f�r Verbindungsaufbau
			urlConn.setConnectTimeout(5000);

			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			// Sending postdata
			if (postdata != null) {
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				printout = new DataOutputStream(urlConn.getOutputStream());
				printout.writeBytes(postdata);
				printout.flush();
				printout.close();
			}

			try {
				// Get response data from the box
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlConn.getInputStream()));

				// read out the response data!
				while (reader.skip(100000) > 0) {
					// kind of stupid, but it has to be
					// If you don't read the list, you may not get an
					// Updated list from the box
				}

				// close the streams
				reader.close();
				urlConn.getInputStream().close();

			} catch (IOException e1) {
				throw new IOException("Network unavailable");
			}

		}

		// The list should be updated now
		// Get the csv file for processing
		Debug.msg("Retrieving the CSV list from the box");
		urlstr = "http://" + box_address + ":" + box_port + "/cgi-bin/webcm";

		try {
			url = new URL(urlstr);
		} catch (MalformedURLException e) {
			Debug.err("URL invalid: " + urlstr);
			throw new WrongPasswordException("URL invalid: " + urlstr);
		}

		// If the url is valid load the data
		if (url != null) {

			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			// Sending postdata to the fritz box
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			printout = new DataOutputStream(urlConn.getOutputStream());
			if (firmware.getLanguage().equals("de")) {
				printout.writeBytes(POSTDATA_FETCH_CALLERLIST.replaceAll(
						"\\$LANG", firmware.getLanguage()).replaceAll(
						"\\$CSV_FILE", CSV_FILE_DE));
			} else if (firmware.getLanguage().equals("en")) {
				printout.writeBytes(POSTDATA_FETCH_CALLERLIST.replaceAll(
						"\\$LANG", firmware.getLanguage()).replaceAll(
						"\\$CSV_FILE", CSV_FILE_EN));
			}
			printout.flush();
			printout.close();

			BufferedReader reader;

			try {
				// Get response data from the box
				reader = new BufferedReader(new InputStreamReader(urlConn
						.getInputStream()));

				// pass it on to the import function

				Debug.msg("Received response, begin processing call list");
				newEntries = JFritz.getCallerList().importFromCSVFile(reader);
				Debug.msg("Finished processing response");

				// close the reader and the cocket connection
				reader.close();
				urlConn.getInputStream().close();

			} catch (IOException e1) {
				throw new IOException("Network unavailable");
			}

			if (wrong_pass)
				throw new WrongPasswordException("Password invalid");
		}

		// return if there were new entries or not
		return newEntries;
	}
	
	/**
	 * 
	 * @return Vector of QuickDial objects
	 * @throws WrongPasswordException
	 * @throws IOException
	 * @throws InvalidFirmwareException
	 */
	public Vector<QuickDial> retrieveQuickDialsFromFritzBox(QuickDials model) throws WrongPasswordException,
			IOException, InvalidFirmwareException {

		String postdata;
		if (firmware.getMajorFirmwareVersion() == 4
				&& firmware.getMinorFirmwareVersion() >= 3) {
			postdata = firmware.getAccessMethod()
					+ POSTDATA_QUICKDIAL_NEW.replaceAll("\\$LANG", firmware
							.getLanguage())
					+ URLEncoder.encode(box_password, "ISO-8859-1"); //$NON-NLS-1$,  //$NON-NLS-2$
		} else {
			postdata = firmware.getAccessMethod()
					+ POSTDATA_QUICKDIAL.replaceAll("\\$LANG", firmware
							.getLanguage())
					+ URLEncoder.encode(box_password, "ISO-8859-1"); //$NON-NLS-1$,  //$NON-NLS-2$
		}
		String urlstr = "http://" //$NON-NLS-1$
				+ box_address + ":"
				+ box_port
				+ "/cgi-bin/webcm"; //$NON-NLS-1$
		String data = fetchDataFromURL(urlstr, postdata, true);
		return parseQuickDialData(model, data, firmware);
	}
	
	/**
	 * creates a list of QuickDial objects
	 * 
	 * @param data
	 * @param firmware
	 * @return list of QuickDial objects
	 */
	public Vector<QuickDial> parseQuickDialData(QuickDials model, String data,
			FritzBoxFirmware firmware) {
		Vector<QuickDial> list = new Vector<QuickDial>();
		data = JFritzUtils.removeDuplicateWhitespace(data);
		Pattern p;
		if (firmware.getMajorFirmwareVersion() == 4
				&& firmware.getMinorFirmwareVersion() >= 3
				&& firmware.getMinorFirmwareVersion() < 5) {
			p = Pattern.compile(PATTERN_QUICKDIAL_NEW);
			quickdial_indizes[NAME] = 1;
			quickdial_indizes[QUICKDIAL] = 3;
			quickdial_indizes[VANITY] = 4;
			quickdial_indizes[NUMBER] = 2;
		} else if (firmware.getMajorFirmwareVersion() == 4
				&& firmware.getMinorFirmwareVersion() >= 5) {
			p = Pattern.compile(PATTERN_QUICKDIAL_BETA);
			quickdial_indizes[NAME] = 1;
			quickdial_indizes[QUICKDIAL] = 3;
			quickdial_indizes[VANITY] = 4;
			quickdial_indizes[NUMBER] = 2;
		} else {
			p = Pattern.compile(PATTERN_QUICKDIAL);
			quickdial_indizes[QUICKDIAL] = 1;
			quickdial_indizes[VANITY] = 2;
			quickdial_indizes[NUMBER] = 3;

		}
		Matcher m = p.matcher(data);

		// TODO: Name einf�gen
		while (m.find()) {
			String description = model.getDescriptionFromNumber(m
					.group(quickdial_indizes[NUMBER]));
			list.add(new QuickDial(m.group(quickdial_indizes[QUICKDIAL]), m
					.group(quickdial_indizes[VANITY]), m
					.group(quickdial_indizes[NUMBER]), description));
		}
		return list;
	}
	
	public boolean checkValidFirmware() {
		while (firmware == null) {
			try {
				firmware = FritzBoxFirmware.detectFirmwareVersion(
								box_address,
								box_password,
								box_port);
				return true;
			} catch (WrongPasswordException e) {
				String new_box_password = JFritz.getJframe().showPasswordDialog("");
				if (new_box_password == null) // Canceled dialog
					return false;
				else {
					box_password = new_box_password;
				}
			} catch (InvalidFirmwareException e) {
				String new_box_address = JFritz.getJframe().showAddressDialog(box_address);
				if (new_box_address == null) { // Dialog canceled
					return false;
				} else {
					box_address = new_box_address; 
				}
			} catch (IOException e) {
				String new_box_address = JFritz.getJframe().showAddressDialog(box_address);
				if (new_box_address == null) { // Dialog canceled
					return false;
				} else {
					box_address = new_box_address;
				}
			}
		}
		return true;
	}
	
	/**
	 * Sends login data to FritzBox
	 */
	public void login() {
		try {
			String postdata = POSTDATA_LOGIN.replaceAll("\\$PASSWORT", //$NON-NLS-1$
					URLEncoder.encode(box_password, "ISO-8859-1"));

			postdata = firmware.getAccessMethod() + postdata;

			String urlstr = "http://" //$NON-NLS-1$
					+ box_address + ":" + box_port
					+ "/cgi-bin/webcm"; //$NON-NLS-1$
			fetchDataFromURL(urlstr, postdata, true);
		} catch (UnsupportedEncodingException uee) {
			//TODO handle Exceptions
		} catch (WrongPasswordException wpe) {
			//TODO handle Exceptions
		} catch (IOException ioe) {
			//TODO handle Exceptions
		}
	}
	
	public void doCall(String number, String port) {
		try {
			login();
			number = number.replaceAll("\\+", "00"); //$NON-NLS-1$,  //$NON-NLS-2$

			String portStr = ""; //$NON-NLS-1$
			if (port.equals("Fon 1")) { //$NON-NLS-1$
				portStr = "1"; //$NON-NLS-1$
			} else if (port.equals("Fon 2")) { //$NON-NLS-1$
				portStr = "2"; //$NON-NLS-1$
			} else if (port.equals("Fon 3")) { //$NON-NLS-1$
				portStr = "3"; //$NON-NLS-1$
			} else if (port.equals(Main.getMessage("analog_telephones_all"))) { //$NON-NLS-1$
				portStr = "9"; //$NON-NLS-1$                
			} else if (port.equals("ISDN Alle")) { //$NON-NLS-1$
				portStr = "50"; //$NON-NLS-1$
			} else if (port.equals("ISDN 1")) { //$NON-NLS-1$
				portStr = "51"; //$NON-NLS-1$
			} else if (port.equals("ISDN 2")) { //$NON-NLS-1$
				portStr = "52"; //$NON-NLS-1$
			} else if (port.equals("ISDN 3")) { //$NON-NLS-1$
				portStr = "53"; //$NON-NLS-1$
			} else if (port.equals("ISDN 4")) { //$NON-NLS-1$
				portStr = "54"; //$NON-NLS-1$
			} else if (port.equals("ISDN 5")) { //$NON-NLS-1$
				portStr = "55"; //$NON-NLS-1$
			} else if (port.equals("ISDN 6")) { //$NON-NLS-1$
				portStr = "56"; //$NON-NLS-1$
			} else if (port.equals("ISDN 7")) { //$NON-NLS-1$
				portStr = "57"; //$NON-NLS-1$
			} else if (port.equals("ISDN 8")) { //$NON-NLS-1$
				portStr = "58"; //$NON-NLS-1$
			} else if (port.equals("ISDN 9")) { //$NON-NLS-1$
				portStr = "59"; //$NON-NLS-1$
			}
            String postdata = POSTDATA_CALL.replaceAll("\\$PASSWORT", //$NON-NLS-1$
                    URLEncoder.encode(box_password, "ISO-8859-1"));
			postdata = postdata.replaceAll("\\$NUMMER", number); //$NON-NLS-1$
			postdata = postdata.replaceAll("\\$NEBENSTELLE", portStr); //$NON-NLS-1$

			postdata = firmware.getAccessMethod() + postdata;

			String urlstr = "http://" //$NON-NLS-1$
					+ box_address + ":" + box_port
					+ "/cgi-bin/webcm"; //$NON-NLS-1$
			fetchDataFromURL(urlstr, postdata, true);
		} catch (UnsupportedEncodingException uee) {
		} catch (WrongPasswordException wpe) {
		} catch (IOException ioe) {
		}

	}
	
	/**
	 * Returns current box address
	 * @return
	 */
	public String getAddress() {
		return box_address;
	}
	
	/**
	 * Returns current box password
	 * @return
	 */
	public String getPassword() {
		return box_password;
	}
	
	/**
	 * Returns current box port
	 * @return
	 */
	public String getPort() {
		return box_port;
	}

	/**
	 * This function calls one of the upnp web services of the box and returns the raw data
	 * the data returned has the following format
	 * 
	 * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body>
	 * <u:GetAddonInfosResponse xmlns:u="urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1">
	 * <NewByteSendRate>0</NewByteSendRate>
	 * <NewByteReceiveRate>0</NewByteReceiveRate>
	 * <NewPacketSendRate>0</NewPacketSendRate>
	 * <NewPacketReceiveRate>0</NewPacketReceiveRate>
	 * <NewTotalBytesSent>0</NewTotalBytesSent>
	 * <NewTotalBytesReceived>0</NewTotalBytesReceived>
	 * <NewAutoDisconnectTime>300</NewAutoDisconnectTime>
	 * <NewIdleDisconnectTime>7</NewIdleDisconnectTime>
	 * <NewDNSServer1>X.X.X.X</NewDNSServer1>
 	 * <NewDNSServer2>Y.Y.Y.Y</NewDNSServer2>
	 * <NewVoipDNSServer1>Z.Z.Z.Z</NewVoipDNSServer1>
	 * <NewVoipDNSServer2>0.0.0.0</NewVoipDNSServer2>
	 * <NewUpnpControlEnabled>0</NewUpnpControlEnabled>
	 * <NewRoutedBridgedModeBoth>0</NewRoutedBridgedModeBoth>
	 * </u:GetAddonInfosResponse>
	 * </s:Body> </s:Envelope>
	 * 
	 * @return the raw xml from the web service of the box
	 */
	public void getInternetStats(AddonInfosListener listener){
		
		String xml =
	        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	        "<s:Envelope " +
	        " xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
	        +"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding\">\n" +
	        "<s:Body>" +
	        "<u:GetAddonInfos xmlns:u=\"urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1\""+
	        "</u:GetAddonInfos>\n"	+
	        " </s:Body>\n" +
	        "</s:Envelope>";
		
		
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			reader.setContentHandler(new AddonInfosXMLHandler(listener));
			reader.parse(new InputSource(new StringReader(
					UPNPUtils.getSOAPData("http://" + getAddress() + 
					URL_SERVICE_ADDONINFOS, URN_SERVICE_ADDONINFOS, xml))));

		} catch (ParserConfigurationException e1) {
			System.err.println(e1);
		} catch (SAXException e1) {
			System.err.println(e1);
		} catch (IOException e1) {
			System.err.println(e1);
		}

	}

	/**
	 * This functions contains various web service calls that are not currently
	 * used but may be used in the future
	 *
	 */
	public void getWebservice(){
		
		String xml =
	        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	        "<s:Envelope " +
	        " xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" 
	        +"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding\">\n" +
	        "<s:Body>" +
	        "<u:GetDSLLinkInfo xmlns:u=\"urn:schemas-upnp-org:service: WANDSLLinkConfig:1\""+
	        "</u:GetDSLLinkInfo>\n"	+
	        " </s:Body>\n" +
	        "</s:Envelope>";
		
		String result = UPNPUtils.getSOAPData("http://" + getAddress() + 
			URL_SERVICE_DSLLINK, URN_SERVICE_DSLLINK, xml);
	
		/*	This is the result of the web service
		 	<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body>
			<u:GetDSLLinkInfoResponse xmlns:u="urn:schemas-upnp-org:service: WANDSLLinkConfig:1">
			<NewLinkType>PPPoE</NewLinkType>
			<NewLinkStatus>Up</NewLinkStatus>
			</u:GetDSLLinkInfoResponse>
			</s:Body> </s:Envelope>
		 	*/
		
		Debug.msg("Result of dsl service: "+ result);
		
		//XML for service, in case the box starts checking some time
       /* <?xml version="1.0"?>
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
        s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
        <s:Body><u:GetStatusInfo xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1"></u:GetStatusInfo>
        </s:Body>
        </s:Envelope>*/

		result = UPNPUtils.getSOAPData("http://" + getAddress() + 
				URL_SERVICE_STATUSINFO, URN_SERVICE_STATUSINFO, xml);
	
		/* 	This is the result fo the STATUSINFO web service
		<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body>
		<u:GetStatusInfoResponse xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1">
		<NewConnectionStatus>Connected</NewConnectionStatus>
		<NewLastConnectionError>ERROR_NONE</NewLastConnectionError>
		<NewUptime>29313</NewUptime>
		</u:GetStatusInfoResponse>
		</s:Body> </s:Envelope>
	*/
		
		
		Debug.msg("Result of status info: "+result);
	
		
	}
	
	/**
	 * function gets the external IP address from the box using the upnp web services
	 * 
	 * @return
	 */
	public String getExternalIPAddress(){
		
		String xml = 	        
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<s:Envelope " +
			" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" 
			+"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding\">\n" +
			"<s:Body>" +
			"<u:GetExternalIPAddress xmlns:u=\"urn:schemas-upnp-org:service:WANIPConnection:1\""+
			"</u:GetExternalIPAddress>\n"	+
			" </s:Body>\n" +
			"</s:Envelope>";
	
		String result = UPNPUtils.getSOAPData("http://" + getAddress() + 
				URL_SERVICE_EXTERNALIP, URN_SERVICE_EXTERNALIP, xml);
		
		/*  Results from the box
		    <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body>
			<u:GetExternalIPAddressResponse xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1">
			<NewExternalIPAddress>X.X.X.X</NewExternalIPAddress>
			</u:GetExternalIPAddressResponse>
			</s:Body> </s:Envelope> */
		
		Debug.msg("External IP response: "+result);
		
		Pattern p = Pattern.compile("<NewExternalIPAddress>([^<]*)</NewExternalIPAddress>");
		Matcher m = p.matcher(result);
		if(m.find())
			return m.group(1);
		else
			return "";
			
	}
	
	public void getCommonLinkInfo(){
		/*
		 *  This is the soap message expected by the box
			<?xml version="1.0"?>
			<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
			s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
			<s:Body><u:GetCommonLinkProperties xmlns:u="urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1"></u:GetCommonLinkProperties>
			</s:Body>
			</s:Envelope>
		 */
		
		String xml = 	        
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<s:Envelope " +
			" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" 
			+"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding\">\n" +
			"<s:Body>" +
			"<u:GetCommonLinkProperties xmlns:u=\"urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1\""+
			"</u:GetCommonLinkProperties>\n"	+
			" </s:Body>\n" +
			"</s:Envelope>";

		String result =  UPNPUtils.getSOAPData("http://" + getAddress() + 
				URL_SERVICE_COMMONLINK, URN_SERVICE_COMMONLINK, xml);
		
		/*  This is the response
		 	<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body>
			<u:GetCommonLinkPropertiesResponse xmlns:u="urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1">
			<NewWANAccessType>DSL</NewWANAccessType>
			<NewLayer1UpstreamMaxBitRate>1183000</NewLayer1UpstreamMaxBitRate>
			<NewLayer1DownstreamMaxBitRate>15656000</NewLayer1DownstreamMaxBitRate>
			<NewPhysicalLinkStatus>Up</NewPhysicalLinkStatus>
			</u:GetCommonLinkPropertiesResponse>
			</s:Body> </s:Envelope>
		*/
		
	}
	
	
	public void setAddress(String box_address) {
		this.box_address = box_address;
	}

	public void setPassword(String box_password) {
		this.box_password = box_password;
	}

	public void setPort(String box_port) {
		this.box_port = box_port;
	}


}
