/*
 * $Id$
 * 
 * Created on 17.05.2005
 *
 */
package de.moonflower.jfritz.firmware;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.exceptions.InvalidFirmwareException;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * Class for detected and managing different firmware versions
 * 
 * @author Arno Willig
 *  
 */
public class FritzBoxFirmware {

	public final static byte BOXTYPE_FRITZBOX_FON = 6;

	public final static byte BOXTYPE_FRITZBOX_FON_WLAN = 8;

	public final static byte BOXTYPE_FRITZBOX_ATA = 11;

    public final static byte BOXTYPE_FRITZBOX_5010 = 23;
    
    public final static byte BOXTYPE_FRITZBOX_5012 = 25;

	public final static byte BOXTYPE_FRITZBOX_5050 = 12;

    public final static byte BOXTYPE_FRITZBOX_7050 = 14;

    public final static byte BOXTYPE_FRITZBOX_7170 = 29;

    public final static byte ACCESS_METHOD_POST_0342 = 0;

	public final static byte ACCESS_METHOD_ENGLISH = 1;

	public final static byte ACCESS_METHOD_PRIOR_0342 = 2;

	private byte boxtype;

	private byte majorFirmwareVersion;

	private byte minorFirmwareVersion;

	private String modFirmwareVersion;
	
	private String language;

	private final static String[] POSTDATA_ACCESS_METHOD = {
			"getpage=../html/de/menus/menu2.html", //$NON-NLS-1$
			"getpage=../html/en/menus/menu2.html", //$NON-NLS-1$
			"getpage=../html/menus/menu2.html" }; //$NON-NLS-1$

	private final static String[] POSTDATA_DETECT_FIRMWARE = {
			"&var%3Alang=de&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2Fpassword=", //$NON-NLS-1$
			"&var%3Alang=en&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2Fpassword="}; //$NON-NLS-1$

	private final static String PATTERN_DETECT_FIRMWARE = "Firmware[-| ]Version[^\\d]*(\\d\\d).(\\d\\d).(\\d\\d\\d*)([^<]*)"; //$NON-NLS-1$
	
	private final static String PATTERN_DETECT_LANGUAGE_DE = "Telefonie";

	private final static String PATTERN_DETECT_LANGUAGE_EN = "Telephony";

	/**
	 * Firmware Constructor using Bytes
	 * 
	 * @param boxtype
	 * @param majorFirmwareVersion
	 * @param minorFirmwareVersion
	 */
	public FritzBoxFirmware(byte boxtype, byte majorFirmwareVersion,
			byte minorFirmwareVersion) {
		this.boxtype = boxtype;
		this.majorFirmwareVersion = majorFirmwareVersion;
		this.minorFirmwareVersion = minorFirmwareVersion;
		this.modFirmwareVersion = ""; //$NON-NLS-1$
	}

	/**
	 * Firmware Constructor using Strings
	 * 
	 * @param boxtype
	 * @param majorFirmwareVersion
	 * @param minorFirmwareVersion
	 */
	public FritzBoxFirmware(String boxtype, String majorFirmwareVersion,
			String minorFirmwareVersion) {
		this.boxtype = Byte.parseByte(boxtype);
		this.majorFirmwareVersion = Byte.parseByte(majorFirmwareVersion);
		this.minorFirmwareVersion = Byte.parseByte(minorFirmwareVersion);
		this.modFirmwareVersion = ""; //$NON-NLS-1$
	}

	/**
	 * Firmware Constructor using Strings
	 * 
	 * @param boxtype
	 * @param majorFirmwareVersion
	 * @param minorFirmwareVersion
	 * @param modFirmwareVersion
	 */
	public FritzBoxFirmware(String boxtype, String majorFirmwareVersion,
			String minorFirmwareVersion, String modFirmwareVersion) {
		this.boxtype = Byte.parseByte(boxtype);
		this.majorFirmwareVersion = Byte.parseByte(majorFirmwareVersion);
		this.minorFirmwareVersion = Byte.parseByte(minorFirmwareVersion);
		this.modFirmwareVersion = modFirmwareVersion;
	}
	
	/**
	 * Firmware Constructor using Strings
	 * 
	 * @param boxtype
	 * @param majorFirmwareVersion
	 * @param minorFirmwareVersion
	 * @param modFirmwareVersion
	 * @param language
	 */
	public FritzBoxFirmware(String boxtype, String majorFirmwareVersion,
			String minorFirmwareVersion, String modFirmwareVersion, String language) {
		this.boxtype = Byte.parseByte(boxtype);
		this.majorFirmwareVersion = Byte.parseByte(majorFirmwareVersion);
		this.minorFirmwareVersion = Byte.parseByte(minorFirmwareVersion);
		this.modFirmwareVersion = modFirmwareVersion;
		this.language = language;
	}

	/**
	 * Firmware Constructor using a single String
	 * 
	 * @param firmware
	 *            Firmware string like '14.06.37'
	 */
/**	public FritzBoxFirmware(String firmware) throws InvalidFirmwareException {
		String mod = ""; //$NON-NLS-1$
		if (firmware == null)
			throw new InvalidFirmwareException("No firmware found"); //$NON-NLS-1$
		if (firmware.indexOf("mod")>0) { //$NON-NLS-1$
			mod = firmware.substring(firmware.indexOf("mod")); //$NON-NLS-1$
			firmware = firmware.substring(0, firmware.indexOf("mod")); //$NON-NLS-1$
		} else if (firmware.indexOf("ds-")>0) {  //$NON-NLS-1$
			// danisahne MOD
            mod = firmware.substring(firmware.indexOf("ds-")); //$NON-NLS-1$
            firmware = firmware.substring(0, firmware.indexOf("ds-")); //$NON-NLS-1$
        } else if (firmware.indexOf("m")>0) { //$NON-NLS-1$
            mod = firmware.substring(firmware.indexOf("m")); //$NON-NLS-1$
            firmware = firmware.substring(0, firmware.indexOf("m")); //$NON-NLS-1$
        } else if (firmware.indexOf("-")>0) { //$NON-NLS-1$ 
        	// BETA Firmware von AVM
			mod = firmware.substring(firmware.indexOf("-")); //$NON-NLS-1$
			firmware = firmware.substring(0, firmware.indexOf("-")); //$NON-NLS-1$
		}
		String[] parts = firmware.split("\\."); //$NON-NLS-1$
		if (parts.length != 3)
			throw new InvalidFirmwareException("Firmware number crippled"); //$NON-NLS-1$

		this.boxtype = Byte.parseByte(parts[0]);
		this.majorFirmwareVersion = Byte.parseByte(parts[1]);
		this.minorFirmwareVersion = Byte.parseByte(parts[2]);
		this.modFirmwareVersion = mod;
	}
**/

	/**
	 * Static method for firmware detection
	 * 
	 * @param box_address
	 * @param box_password
	 * @return New instance of FritzBoxFirmware
	 * @throws WrongPasswordException
	 * @throws IOException
	 */
	public static FritzBoxFirmware detectFirmwareVersion(String box_address,
			String box_password, String port) throws WrongPasswordException, IOException, InvalidFirmwareException {
		final String urlstr = "http://" + box_address +":" + port + "/cgi-bin/webcm"; //$NON-NLS-1$, //$NON-NLS-2$

		String data = ""; //$NON-NLS-1$
		String language = "de";
		
		boolean detected = false;
		for (int i=0; i<(POSTDATA_ACCESS_METHOD).length; i++)
		{
			for (int j=0; j<(POSTDATA_DETECT_FIRMWARE).length; j++)
			{
				data = JFritzUtils.fetchDataFromURL(
						urlstr,
						POSTDATA_ACCESS_METHOD[i] + POSTDATA_DETECT_FIRMWARE[j]
							+ URLEncoder.encode(box_password, "ISO-8859-1"), true).trim(); //$NON-NLS-1$
			
				Pattern p = Pattern.compile(PATTERN_DETECT_LANGUAGE_DE);
				Matcher m = p.matcher(data);
				if (m.find()) {
					language = "de";
					detected = true;
					break;
				}
				
				if (!detected)
				{
					p = Pattern.compile(PATTERN_DETECT_LANGUAGE_EN);
					m = p.matcher(data);
					if (m.find()) {
						language = "en";
						detected = true;
						break;
					}
				}
			}
			if ( detected ) break;
		}
		
		if (!detected ) throw new InvalidFirmwareException();
				
		// Modded firmware: data = "> FRITZ!Box Fon WLAN, <span
		// class=\"Dialoglabel\">Modified-Firmware </span>08.03.37mod-0.55
		// \n</div>";
		Pattern p = Pattern.compile(PATTERN_DETECT_FIRMWARE);
		Matcher m = p.matcher(data);
		if (m.find()) {
			String boxtypeString = m.group(1);
			String majorFirmwareVersion = m.group(2);
			String minorFirmwareVersion = m.group(3);
			String modFirmwareVersion = m.group(4).trim();
			Debug.msg("Detected Firmware: " + 
					boxtypeString + "." + 
					majorFirmwareVersion + "." +
					minorFirmwareVersion + 
					modFirmwareVersion + " " +
					language);
			return new FritzBoxFirmware(boxtypeString, majorFirmwareVersion,
					minorFirmwareVersion, modFirmwareVersion, language);
		} else {
			System.err.println("detectFirmwareVersion: Password wrong?"); //$NON-NLS-1$
			throw new WrongPasswordException(
					"Could not detect FRITZ!Box firmware version."); //$NON-NLS-1$
		}
	}

	/**
	 * @return Returns the boxtype.
	 */
	public final byte getBoxType() {
		return boxtype;
	}

	/**
	 * @return Returns the access method string.
	 * 
	 * TODO: Sp�ter noch die Major-Version mit einbeziehen, falls es mit neueren
	 * Versionen nicht klappen sollte
	 *  
	 */
	public final String getAccessMethod() {
		int accessMethod;
		if ( language.equals("en"))
			accessMethod = ACCESS_METHOD_ENGLISH;
		else if (majorFirmwareVersion == 3 && minorFirmwareVersion < 42)
			accessMethod = ACCESS_METHOD_PRIOR_0342;
		else
			accessMethod = ACCESS_METHOD_POST_0342;

		return POSTDATA_ACCESS_METHOD[accessMethod];
	}

	/**
	 * @return Returns the majorFirmwareVersion.
	 */
	public final byte getMajorFirmwareVersion() {
		return majorFirmwareVersion;
	}

	/**
	 * @return Returns the minorFirmwareVersion.
	 */
	public final byte getMinorFirmwareVersion() {
		return minorFirmwareVersion;
	}

	/**
	 * @return Returns the majorFirmwareVersion.
	 */
	public final String getFirmwareVersion() {
        String boxtypeStr = Byte.toString(boxtype);
        String majorStr = Byte.toString(majorFirmwareVersion);
        String minorStr = Byte.toString(minorFirmwareVersion);
        if (boxtypeStr.length() == 1) { boxtypeStr = "0" + boxtypeStr; } //$NON-NLS-1$
        if (majorStr.length() == 1) { majorStr = "0" + majorStr; } //$NON-NLS-1$
        if (minorStr.length() == 1) { minorStr = "0" + minorStr; } //$NON-NLS-1$
       	return boxtypeStr + "." + majorStr + "." + minorStr + modFirmwareVersion; //$NON-NLS-1$,  //$NON-NLS-2$
	}

	public String getBoxName() {
		switch (boxtype) {
		case 6:
			return "FRITZ!Box Fon"; //$NON-NLS-1$
		case 8:
			return "FRITZ!Box Fon WLAN"; //$NON-NLS-1$
		case 14:
			return "FRITZ!Box 7050"; //$NON-NLS-1$
		case 12:
			return "FRITZ!Box 5050"; //$NON-NLS-1$
		case 11:
			return "FRITZ!Box ata"; //$NON-NLS-1$
        case 23:
            return "FRITZ!Box 5010"; //$NON-NLS-1$
        case 25:
            return "FRITZ!Box 5012"; //$NON-NLS-1$
        case 29:
            return "FRITZ!Box 7170"; //$NON-NLS-1$
		default:
			return JFritz.getMessage("unknown"); //$NON-NLS-1$
		}
	}

	public final String toString() {
		return getFirmwareVersion();
	}
	
	public final String getLanguage() {
		return language;
	}
}
