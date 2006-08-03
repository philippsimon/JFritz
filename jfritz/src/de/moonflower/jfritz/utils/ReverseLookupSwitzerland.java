package de.moonflower.jfritz.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.Debug;
/**
 * This class is responsible for doing reverse lookups for swiss numbers
 * 
 * The search engine used is: http://tel.search.ch
 * A big thanks to them for creating an easy to parse web response
 * 
 * @author Brian Jensen
 *
 */
public final class ReverseLookupSwitzerland {

	public final static String SEARCH_URL="http://tel.search.ch/result.html?tel=";
	
	public static Person lookup(String number){
		boolean intNumber = false;
		
		if(number.startsWith("+")){
			number = number.substring(1);
			intNumber = true;
		}
		Debug.msg("Switzerland reverselookup number: "+number);
		
		String urlstr = SEARCH_URL + number;
		Person newPerson;
		
		String firstname = "",
				lastname = "",
				street = "", //$NON-NLS-1$ 
				zipCode = "", //$NON-NLS-1$
				city = ""; 	  //$NON-NLS-1$;
	 
		
		try {
			URL url = new URL(urlstr);
			if (url != null) {

				URLConnection con;
				try {
					con = url.openConnection();

					String header = ""; //$NON-NLS-1$
					String charSet = ""; //$NON-NLS-1$
					for (int i = 0;; i++) {
						String headerName = con.getHeaderFieldKey(i);
						String headerValue = con.getHeaderField(i);

						if (headerName == null && headerValue == null) {
							// No more headers
							break;
						}
						if ("content-type".equalsIgnoreCase(headerName)) { //$NON-NLS-1$
							String[] split = headerValue.split(" ", 2); //$NON-NLS-1$
							for (int j = 0; j < split.length; j++) {
								split[j] = split[j].replaceAll(";", ""); //$NON-NLS-1$,  //$NON-NLS-2$
								if (split[j].toLowerCase().startsWith(
										"charset=")) { //$NON-NLS-1$
									String[] charsetSplit = split[j].split("="); //$NON-NLS-1$
									charSet = charsetSplit[1];
								}
							}
						}
						header += headerName + ": " + headerValue + " | "; //$NON-NLS-1$,  //$NON-NLS-2$
					}
					Debug.msg("Header of tel.search.ch: " + header); //$NON-NLS-1$
					Debug.msg("CHARSET : " + charSet); //$NON-NLS-1$

					// Get used Charset
					BufferedReader d;
					if (charSet.equals("")) { //$NON-NLS-1$
						d = new BufferedReader(new InputStreamReader(con
								.getInputStream(), "ISO-8859-1")); //$NON-NLS-1$
					} else {
						d = new BufferedReader(new InputStreamReader(con
								.getInputStream(), charSet));
					}
					int i = 0;
					String str = ""; //$NON-NLS-1$
					String data = "";
					
					// Get response data
					while ((i < 700) && (null != ((str = d.readLine())))) {
						data += str;
						i++;
					}
					d.close();
					Debug.msg("Begin processing responce from tel.search.ch");

					Pattern pName = Pattern
							.compile("<div class=\"rname\"><a[^>]*>([^<]*)</a></div>"); //$NON-NLS-1$
					Pattern pAddress = Pattern
						.compile("<div class=\"raddr\">([^<,]*),\\s*([0-9]*)([^<]*)</div>"); //$NON-NLS-1$
					
					//parse Name
					Matcher mName = pName.matcher(data);
					if (mName.find()) {
						String[] results = mName.group(1).trim().split(",");
						
						lastname = results[0];
						Debug.msg("Last name: " + lastname);
						if(results.length > 1)
							firstname = results[1];
						
						Debug.msg("First name: " + firstname);
					}
					
					//parse Street, zip code and city
					Matcher mAddress = pAddress.matcher(data);
					if(mAddress.find()){
						street = mAddress.group(1).trim();
						Debug.msg("Street: "+street);
						zipCode  = mAddress.group(2).trim();
						Debug.msg("Zip Code: "+ zipCode);
						city = mAddress.group(3).trim();
						Debug.msg("City: "+city);
					}

				} catch (IOException e1) {
					Debug.err("Error while retrieving " + urlstr); //$NON-NLS-1$
				}
			}
		} catch (MalformedURLException e) {
			Debug.err("URL invalid: " + urlstr); //$NON-NLS-1$
		}
		
		newPerson = new Person(firstname, "", lastname, street, zipCode, city, "");
		if(intNumber)
			number = "+" + number;
		
		newPerson.addNumber(number, "home"); //$NON-NLS-1$
		
		
		return newPerson;
		
	}
	
}
