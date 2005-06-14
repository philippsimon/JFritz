/*
 * Created on 01.06.2005
 *
 */
package de.moonflower.jfritz.struct;

import java.util.HashMap;

import de.moonflower.jfritz.JFritz;

/**
 * @author Arno Willig
 *  
 */
public class PhoneNumber implements Comparable {

	private String number = "";

	private String callbycall = "";

	private String type = "";

	static HashMap mobileMap;

	/**
	 * Constructs a PhoneNumber with a special type
	 * 
	 * @param fullNumber
	 * @param
	 */
	public PhoneNumber(String number, String type) {
		this.type = type;
		this.number = number;
		createMobileMap();
		refactorNumber();
	}

	/**
	 * Constructs a PhoneNumber withput a type
	 * 
	 * @param fullNumber
	 */
	public PhoneNumber(String fullNumber) {
		this(fullNumber, "");
	}

	public void setNumber(String number) {
		this.number = number;
		refactorNumber();
	}

	/**
	 * creates a map of german cellphone providers
	 */
	private void createMobileMap() {
		if (mobileMap == null) {
			mobileMap = new HashMap();
			mobileMap.put("+49151", "D1");
			mobileMap.put("+49160", "D1");
			mobileMap.put("+49170", "D1");
			mobileMap.put("+49171", "D1");
			mobileMap.put("+49175", "D1");
			mobileMap.put("+49152", "D2");
			mobileMap.put("+49162", "D2");
			mobileMap.put("+49172", "D2");
			mobileMap.put("+49173", "D2");
			mobileMap.put("+49174", "D2");
			mobileMap.put("+49163", "E+");
			mobileMap.put("+49177", "E+");
			mobileMap.put("+49178", "E+");
			mobileMap.put("+49159", "O2");
			mobileMap.put("+49176", "O2");
			mobileMap.put("+49179", "O2");
		}
	}

	public void refactorNumber() {
		cutCallByCall();
		number = convertToIntNumber();
	}

	public String cutCallByCall() {
		String callbycall = "";
		if (number.startsWith("010")) { // cut 01013 and others
			callbycall = number.substring(0, 5);
			number = number.substring(5);
		}
		return number;
	}

	public String convertToIntNumber() {
		String countryCode = JFritz.getProperty("country.code");
		String countryPrefix = JFritz.getProperty("country.prefix");
		String areaCode = JFritz.getProperty("area.code");
		String areaPrefix = JFritz.getProperty("area.prefix");

		if ((number.length() < 4) // A valid number??
				|| (number.startsWith("+")) // International number
				|| isSIPNumber() // SIP Number
				|| isEmergencyCall() // Emergency
		) {
			return number;
		}

		if (number.startsWith(countryCode) && number.length() > 10) {
			// International numbers without countryPrefix
			// (some VOIP numbers) }
			return "+" + number;
		}
		if (number.startsWith(countryPrefix)) { // International call
			return "+" + number.substring(countryPrefix.length());
		}
		if (number.startsWith(areaPrefix)) {
			return "+" + countryCode + number.substring(areaPrefix.length());
		}
		return "+" + countryCode + areaCode + number;
	}

	public String toString() {
		return getFullNumber();
	}

	/**
	 * 
	 * @return the full number
	 */
	public String getFullNumber() {
		return number;
	}

	public String getShortNumber() {
		String countryCode = JFritz.getProperty("country.code");
		String areaCode = JFritz.getProperty("area.code");
		String areaPrefix = JFritz.getProperty("area.prefix");
		if (number.startsWith("+" + countryCode + areaCode))
			return number.substring(countryCode.length() + areaCode.length()
					+ 1);
		if (number.startsWith("+" + countryCode))
			return areaPrefix + number.substring(countryCode.length() + 1);
		return number;
	}

	public String getAreaNumber() {
		String countryCode = JFritz.getProperty("country.code");
		String countryPrefix = JFritz.getProperty("country.prefix");
		String areaCode = JFritz.getProperty("area.code");
		String areaPrefix = JFritz.getProperty("area.prefix");
		if (number.startsWith("+" + countryCode))
			return areaPrefix + number.substring(countryCode.length() + 1);
		return number;
	}

	/**
	 * @return CallByCall predial number
	 */
	public String getCallByCall() {
		return callbycall;
	}

	/**
	 * @return True if number has a Vorvorwahl (like 01013)
	 */
	public boolean hasCallByCall() {
		return (callbycall.length() > 0);
	}

	/**
	 * @return True if number is a FreeCall number
	 */
	public boolean isFreeCall() {
		boolean ret = number.startsWith("0800");
		if (ret && getType() == "")
			type = "business";
		return ret;
	}

	/**
	 * @return True if number is a local number
	 */
	public boolean isLocalCall() {
		String countryCode = JFritz.getProperty("country.code");
		String areaCode = JFritz.getProperty("area.code");
		return number.startsWith("+" + countryCode + areaCode);
	}

	/**
	 * @return True if number is a SIP number
	 */
	public boolean isSIPNumber() {
		String countryCode = JFritz.getProperty("country.code");
		return ((number.indexOf('@') > 0) || number.startsWith("00038") // PurTel
				|| number.startsWith("555") // SIPGate
		|| number.startsWith("777") // SIPGate
		);
	}

	/**
	 * @return True if number is a short quickdial number
	 */
	public boolean isQuickDial() {
		return (number.length() < 3);
	}

	/**
	 * @return True if number is an emergency number
	 */
	public boolean isEmergencyCall() {
		if (number.equals("110"))
			return true; // Germany Police
		else if (number.equals("112"))
			return true; // Germany Medical
		else if (number.equals("116116"))
			return true; // Germany Credit Card
		else if (number.equals("144"))
			return true; // Switzerland Medical
		return false;
	}

	// FIXME: This does not work yet ***************

	/**
	 * 
	 * @return Country code (49 for Germany, 41 for Switzerland)
	 */
	public String getCountryCode() {
		//	return "49";
		return "";
	}

	/**
	 * @return Area code
	 */
	public String getAreaCode() {
		//		return "441";
		return "";
	}

	/**
	 * 
	 * @return Local part of number
	 */
	public String getLocalPart() {
		//		return "592904";
		return "";
	}

	/**
	 * 
	 * @return Returns mobile provider
	 */
	public String getMobileProvider() {
		if (number.length() < 5)
			return "";
		Object provider = mobileMap.get(number.substring(0, 6));
		if (provider == null)
			return "";
		return mobileMap.get(number.substring(0, 6)).toString();
	}

	/**
	 * @return True if number is a mobile one
	 */
	public boolean isMobile() {
		//		String provider = ReverseLookup.getMobileProvider(getFullNumber());
		//		return (!provider.equals(""));
		boolean ret = number.length() > 6
				&& mobileMap.containsKey(number.substring(0, 6));
		if (ret && getType() == "")
			type = "mobile";
		return ret;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		if (arg0.getClass().equals(this.getClass())) {

		}
		return 0;
	}

	/**
	 * @return Returns the type.
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public final void setType(String type) {
		this.type = type;
	}

	/**
	 * Auto-Set type
	 */
	public final void setType() {
		if (isMobile())
			type = "mobile";
		else if (isFreeCall())
			type = "business";
		else if (isSIPNumber())
			type = "sip";
		else
			type = "home";
	}
	/**
	 * @return Returns the callbycall.
	 */
	public String getCallbycall() {
		return callbycall;
	}
	/**
	 * @param callbycall The callbycall to set.
	 */
	public void setCallbycall(String callbycall) {
		this.callbycall = callbycall;
	}
}
