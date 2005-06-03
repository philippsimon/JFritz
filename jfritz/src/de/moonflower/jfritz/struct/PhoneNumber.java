/*
 * Created on 01.06.2005
 *
 */
package de.moonflower.jfritz.struct;


/**
 * @author Arno Willig
 *  
 */
public class PhoneNumber {

	public static String intPrefix = "+";

	public static String areaPrefix = "0";

	private String number = "";

	private String callbycall = "";

	/**
	 * Constructs a PhoneNumber
	 * 
	 * @param fullNumber
	 */
	public PhoneNumber(String fullNumber) {
		if (fullNumber.startsWith("010")) { // cut 01013 and others
			callbycall = fullNumber.substring(0, 5);
			number = fullNumber.substring(5);
		} else
			number = fullNumber;
		if (number.startsWith(intPrefix)) {
			// TODO
		}
	}

	public String toString() {
		return getNumber();
	}

	/**
	 * 
	 * @return the full number
	 */
	public String getNumber() {
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
		return number.startsWith("0800");
	}

	/**
	 * @return True if number is a SIP number
	 */
	public boolean isSIPNumber() {
		return (number.indexOf('@') > 0);
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
		return "49";
	}

	/**
	 * @return Area code
	 */
	public String getAreaCode() {
		return "441";
	}

	/**
	 * 
	 * @return Local part of number
	 */
	public String getLocalPart() {
		return "592904";
	}

	/**
	 * 
	 * @return Mobile provider
	 */
	public String getMobileProvider() {
		return "O2";
	}

	/**
	 * @return True if number is a mobile one
	 */
	public boolean isMobile() {
		return false;
	}

}
