package de.moonflower.jfritz.utils.reverselookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.struct.ReverseLookupSite;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.HTMLUtil;
import de.moonflower.jfritz.utils.JFritzUtils;

	/**
	 * 
	 * @author marc
	 * 
	 */
public class LookupThread extends Thread {
		
	LookupRequest currentRequest;

	private boolean threadSuspended, quit;

	Person result;
	
	/**
	 * sets the default thread state to active
	 *
	 */
	public LookupThread(boolean quitOnDone) {
		threadSuspended = false;
		quit = quitOnDone;
	}

	/**
	 * This method iterates over all lookup requests and stops once
	 * no more are present, in which case it notifies ReverseLookup
	 * that it is done. If threadSuspended is true the thread while wait
	 * it is reactivated to continue doing lookups.
	 * 
	 */
	public void run() {
		
		while (true) {
			try {
		
			if(ReverseLookup.getRequestsCount() == 0 && quit)
				break;
				
			while(threadSuspended || ReverseLookup.getRequestsCount() == 0){
					ReverseLookup.lookupDone();
					synchronized(this){
						wait();
					}
				}
			} catch (InterruptedException e) {
					break;// we were interrupted
			}
				
			currentRequest = ReverseLookup.getNextRequest();
			
			result = lookup(currentRequest.number);
			ReverseLookup.personFound(result);
			
			try{
				sleep(2000);
			}catch(Exception e){
				break;
			}			
		}
	
		Debug.msg("Lookup thread exiting");
	
	}
	
	public synchronized void suspendLookup(){
		Debug.msg("suspending lookup thread");
		threadSuspended = true;
	}
	
	public synchronized void resumeLookup(){
		threadSuspended = false;
		notify();
		Debug.msg("resuming lookup thread again");
	}
	
	public synchronized boolean isSuspended(){
		return threadSuspended;
	}
	
	/**
	 * This function handles the actual "lookup" part. It uses the data loaded
	 * from number/international/revserlookup.xml to iterate over a series of
	 * websites and over series of patterns for processing each website
	 * 
	 * @see de.moonflower.jfritz.struct.ReverseLookupSite
	 * 
	 * @author brian
	 * 
	 * @param number number to be looked up
	 * @return a new person object containing the results of the search
	 */
	static synchronized Person lookup(PhoneNumber number) {
	
		Person newPerson = new Person();
		
		if (number.isFreeCall()) {
			newPerson = new Person("", "FreeCall"); //$NON-NLS-1$,  //$NON-NLS-2$
			newPerson.addNumber(number);
		} else if (number.isSIPNumber() || number.isQuickDial()) {
			newPerson = new Person();
			newPerson.addNumber(number);
		} else if (ReverseLookup.rlsMap.containsKey(number.getCountryCode())) {
			
			String nummer = number.getAreaNumber();
			LinkedList<ReverseLookupSite> rls_list = ReverseLookup.rlsMap.get(number.getCountryCode());
			ReverseLookupSite rls;
			String[] patterns;
			String urlstr = "";
			String data = ""; //$NON-NLS-1$
			URL url;
			URLConnection con;
			String header = ""; //$NON-NLS-1$
			String charSet = ""; //$NON-NLS-1$
			String str = ""; //$NON-NLS-1$
			Pattern pData;
			Matcher mData;
			String firstname = "", //$NON-NLS-1$
			lastname = "", //$NON-NLS-1$
			company = "", //$NON-NLS-1$
			street = "", //$NON-NLS-1$
			zipcode = "", //$NON-NLS-1$
			city = ""; 	  //$NON-NLS-1$		
			
			Debug.msg("Begin reverselookup for: "+nummer);
			
			//cut off the country code if were doing a non local lookup
			if(nummer.startsWith(number.getCountryCode()))
				nummer = nummer.substring(number.getCountryCode().length());
		
			//Iterate over all the web sites loaded for the given country
			for(int i=0; i < rls_list.size(); i++){
				yield();
				rls = rls_list.get(i);
				//if(rls_list[i] == null)
				//	break;
				
				//needed to make sure international calls are formatted correctly
				if(!nummer.startsWith(rls.getPrefix()))
					nummer = rls.getPrefix() + nummer;
				
				urlstr = rls.getURL().replaceAll("\\$NUMBER", nummer);
				Debug.msg("Reverse lookup using: "+urlstr);
				url = null;
				data = "";
				
				//urlstr = rls_list[i].getURL() + nummer.replaceAll("\\+","%2B");
				
				//open a connection to the site
				try {
					url = new URL(urlstr);
					if (url != null) {

						try {
							con = url.openConnection();
							// 5 Sekunden-Timeout f�r Verbindungsaufbau
							//15 seconds for the response
							con.setConnectTimeout(5000);
							con.setReadTimeout(15000);
							con.connect();						
							//process header
							for (int j = 0;; j++) {
								String headerName = con.getHeaderFieldKey(j);
								String headerValue = con.getHeaderField(j);

								if (headerName == null && headerValue == null) {
									// No more headers
									break;
								}
								if ("content-type".equalsIgnoreCase(headerName)) { //$NON-NLS-1$
									String[] split = headerValue.split(" ", 2); //$NON-NLS-1$
									for (int k = 0; j < split.length; k++) {
										split[k] = split[k].replaceAll(";", ""); //$NON-NLS-1$,  //$NON-NLS-2$
										if (split[k].toLowerCase().startsWith(
												"charset=")) { //$NON-NLS-1$
											String[] charsetSplit = split[k].split("="); //$NON-NLS-1$
											charSet = charsetSplit[1];
										}
									}
								}
								header += headerName + ": " + headerValue + " | "; //$NON-NLS-1$,  //$NON-NLS-2$
							}
							Debug.msg("Header of "+rls.getName()+":" + header); //$NON-NLS-1$
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
							//read in data, if the website has stalled the timer will kill the connection
							while (null != ((str = d.readLine()))) {
									data += str;
									yield();
									if ( data.length() > 100000 ) {
										System.err.println("Result > 100000 Bytes");
										break;
									}
							}
							d.close();
							Debug.msg("Begin processing response from "+rls.getName());

							//iterate over all patterns for this web site
							for(int j=0; j < rls.size(); j++){
								yield();
								//clear all the entries in case something got matched by mistake
								firstname = ""; //$NON-NLS-1$
								lastname = ""; //$NON-NLS-1$
								company = ""; //$NON-NLS-1$
								street = ""; //$NON-NLS-1$
								zipcode = ""; //$NON-NLS-1$
								city = "";	  //$NON-NLS-1$		
								
								patterns = rls.getEntry(j);
								
								//match name
								if(!patterns[0].equals("")){
									pData = Pattern.compile(patterns[0]);
									mData = pData.matcher(data);
									if(mData.find()){
										
										//read in and concate all groupings
										str = "";
										for(int k=1; k <= mData.groupCount(); k++)
											str = str + mData.group(k).trim() + " "; 
										
										String[] split = str.split(" ", 2); //$NON-NLS-1$
										
										lastname = JFritzUtils.removeLeadingSpaces(HTMLUtil.stripEntities(split[0]));
										if (split.length > 1) {
											firstname = " " + HTMLUtil.stripEntities(split[1]); //$NON-NLS-1$
											if ((firstname.indexOf("  ") > -1) //$NON-NLS-1$
													&& (firstname.indexOf("  u.") == -1)) { //$NON-NLS-1$
												company = JFritzUtils.removeLeadingSpaces(firstname.substring(
														firstname.indexOf("  ")).trim()); //$NON-NLS-1$
												firstname = JFritzUtils.removeLeadingSpaces(firstname.substring(0,
														firstname.indexOf("  ")).trim()); //$NON-NLS-1$
											} else {
												firstname = JFritzUtils.removeLeadingSpaces(firstname.replaceAll("  u. ", //$NON-NLS-1$
														" und ")); //$NON-NLS-1$
											}
										}
										
										firstname = JFritzUtils.removeLeadingSpaces(firstname.trim());
									}
								}
								yield();
								//match street
								if(!patterns[1].equals("")){
									
									pData = Pattern.compile(patterns[1]);
									mData = pData.matcher(data);
									if(mData.find()){
										
										//read in and concate all groupings
										str = "";
										for(int k=1; k <= mData.groupCount(); k++)
											str = str + mData.group(k).trim() + " "; 
										
										street = JFritzUtils.removeLeadingSpaces(HTMLUtil.stripEntities(str));;
									}
								}
								yield();
								//match city
								if(!patterns[2].equals("")){
									
									pData = Pattern.compile(patterns[2]);
									mData = pData.matcher(data);
									if(mData.find()){
										
										//read in and concate all groupings
										str = "";
										for(int k=1; k <= mData.groupCount(); k++)
											str = str + mData.group(k).trim() + " "; 
										
										city = JFritzUtils.removeLeadingSpaces(HTMLUtil.stripEntities(str));
									}
								}
								
								yield();
								//match zip code
								if(!patterns[3].equals("")){
									
									pData = Pattern.compile(patterns[3]);
									mData = pData.matcher(data);
									if(mData.find()){
										
										//read in and concate all groupings
										str = "";
										for(int k=1; k <= mData.groupCount(); k++)
											str = str + mData.group(k).trim() + " "; 
										
										zipcode = JFritzUtils.removeLeadingSpaces(HTMLUtil.stripEntities(str));
									}
								}
								
								//we found a name so stop looping
								if(!firstname.equals("") || !lastname.equals(""))
									break;
								
							} //Done iterating for the given web site

							yield();
							Debug.msg("Firstname: " + firstname); //$NON-NLS-1$
							Debug.msg("Lastname: " + lastname); //$NON-NLS-1$
							Debug.msg("Company: " + company); //$NON-NLS-1$
							Debug.msg("Street: " + street); //$NON-NLS-1$
							Debug.msg("ZipCode: " + zipcode); //$NON-NLS-1$
							Debug.msg("City: " + city); //$NON-NLS-1$

							//if we got a name then quite looking
							if(!firstname.equals("") || !lastname.equals("")){
								
								//if the city wasnt listed or matched use the number maps
								if(city.equals("") && number.getCountryCode().equals(ReverseLookup.GERMANY_CODE))
									city = ReverseLookupGermany.getCity(nummer);
								else if(city.equals("") && number.getCountryCode().equals(ReverseLookup.AUSTRIA_CODE))
									city = ReverseLookupGermany.getCity(nummer);
								else if(number.getCountryCode().startsWith(ReverseLookup.USA_CODE))
									city = ReverseLookupUnitedStates.getCity(nummer);
								
								newPerson = new Person(firstname, company, lastname,
										street, zipcode, city, ""); //$NON-NLS-1$
								if (company.length() > 0) {
									newPerson.addNumber(number.getIntNumber(), "business"); //$NON-NLS-1$
								} else {
									newPerson.addNumber(number.getIntNumber(), "home"); //$NON-NLS-1$
								}
								
								return newPerson;
							}
							
					} catch (IOException e1) {
						Debug.err("Error while retrieving " + urlstr); //$NON-NLS-1$
						}
					}
				} catch (MalformedURLException e) {
					Debug.err("URL invalid: " + urlstr); //$NON-NLS-1$
				}
				
			} // done iterating over all the loaded web sites
			yield();
				
			//if we made it here, no match was found
			Debug.msg("No match for "+nummer+" found");
			
			//use the number maps to lookup the city
			if(number.getCountryCode().equals(ReverseLookup.GERMANY_CODE))
				city = ReverseLookupGermany.getCity(nummer);
			else if(number.getCountryCode().equals(ReverseLookup.AUSTRIA_CODE))
				city = ReverseLookupGermany.getCity(nummer);
			else if(number.getCountryCode().startsWith(ReverseLookup.USA_CODE))
				city = ReverseLookupUnitedStates.getCity(nummer);
			
			newPerson = new Person("", "", "", "", "", city, "");
			newPerson.addNumber(number.getAreaNumber(), "home"); //$NON-NLS-1$
		
		//no reverse lookup sites available for country	
		} else {
			Debug.msg("No reverse lookup sites for: "+number.getCountryCode());
			newPerson = new Person();
			newPerson.addNumber(number.getAreaNumber(), "home");
		}
		
		return newPerson;
	}	
	
}
	

