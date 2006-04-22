/*
 * $Id$
 * 
 * Created on 05.05.2005
 *
 */
package de.moonflower.jfritz.dialogs.phonebook;

import java.util.Date;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * XML Handler for reading the call file
 * 
 * @author Arno Willig
 *  
 */
public class PhonebookFileXMLHandler extends DefaultHandler {

	String firstName, company, lastName, type, standard, email, street,
			postCode, city, category;

	Vector numbers;

	String chars;

	PhoneBook phonebook;

	Person person;

	Date calldate;

	int duration;
	
	boolean privateEntry;

	public PhonebookFileXMLHandler(PhoneBook phonebook) {
		super();
		this.phonebook = phonebook;
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String namespaceURI, String lName, String qName,
			Attributes attrs) throws SAXException {
		String eName = lName;
		if ("".equals(eName)) //$NON-NLS-1$
			eName = qName;

		// Important to clear buffer :)
		chars = ""; //$NON-NLS-1$

		if (eName.equals("entry")) { //$NON-NLS-1$
			String privEntry = attrs.getValue("private"); //$NON-NLS-1$
			if (privEntry != null) {
				privateEntry = JFritzUtils.parseBoolean(attrs.getValue("private")); //$NON-NLS-1$
			}
			else privateEntry = false;
			firstName = ""; //$NON-NLS-1$
			company = ""; //$NON-NLS-1$
			lastName = ""; //$NON-NLS-1$
			numbers = new Vector();
			street = ""; //$NON-NLS-1$
			postCode = ""; //$NON-NLS-1$
			city = ""; //$NON-NLS-1$
			email = ""; //$NON-NLS-1$
			category = ""; //$NON-NLS-1$
			type = "home"; //$NON-NLS-1$
			standard = "home"; //$NON-NLS-1$
		} else if (eName.equals("phonenumbers")) { //$NON-NLS-1$
			standard = attrs.getValue("standard"); //$NON-NLS-1$
		} else if (eName.equals("number")) { //$NON-NLS-1$
			type = attrs.getValue("type"); //$NON-NLS-1$
			// Debug.msg("STD: "+standard+" TYPE: "+type);
		}
	}

	public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		if (qName.equals("firstname")) { //$NON-NLS-1$
			firstName = chars;
		} else if (qName.equals("company")) { //$NON-NLS-1$
			company = chars;
		} else if (qName.equals("lastname")) { //$NON-NLS-1$
			lastName = chars;
		} else if (qName.equals("number")) { //$NON-NLS-1$
			numbers.add(new PhoneNumber(chars, type));
		} else if (qName.equals("street")) { //$NON-NLS-1$
			street = chars;
		} else if (qName.equals("postcode")) { //$NON-NLS-1$
			postCode = chars;
		} else if (qName.equals("city")) { //$NON-NLS-1$
			city = chars;
		} else if (qName.equals("email")) { //$NON-NLS-1$
			email = chars;
		} else if (qName.equals("category")) { //$NON-NLS-1$
			category = chars;
		} else if (qName.equals("entry")) { //$NON-NLS-1$
			Person newPerson = new Person(firstName, company, lastName,
					street, postCode, city, email);

			newPerson.setNumbers(numbers, standard);
			newPerson.setPrivateEntry(privateEntry);
			phonebook.addEntry(newPerson);

		}
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		chars += new String(buf, offset, len);
	}

}