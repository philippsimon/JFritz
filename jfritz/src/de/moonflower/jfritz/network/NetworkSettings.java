package de.moonflower.jfritz.network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.utils.Debug;

public class NetworkSettings {
	
	private static Vector<Login> clientLogins;
	
	public static void loadFromXMLFile(){
		
	}

	public static void saveToXMLFile(String filename){
	
		Debug.msg("Saving to file " + filename); //$NON-NLS-1$
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filename);
			PrintWriter pw = new PrintWriter(fos);
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
			pw.println("<networksettings>"); //$NON-NLS-1$

			if(Main.getProperty("network.type", "none").equals("server")){
				pw.println("<server>");
				
				Login login;
				Enumeration<Login> en = clientLogins.elements();
				while(en.hasMoreElements()){
						login = en.nextElement();
						pw.println("\t<logon>");
						pw.println("\t\t<user>"+login.getUser()+"</user>");
						pw.println("\t\t<password>"+login.getPassword()+"</password>");
						pw.println("\t\t<allowadd>"+login.allowAdd()+"</allowadd>");
						pw.println("\t\t<allowupdate>"+login.allowUpdate()+"</allowupdate>");
						pw.println("\t\t<allowremove>"+login.allowRemove()+"</allowremove");
						pw.println("\t</logon>");
				}
				pw.println("</server>");
			}

			pw.println("</networksettings>"); //$NON-NLS-1$
			pw.close();
		} catch (FileNotFoundException e) {
			Debug.err("Could not write " + filename + "!"); //$NON-NLS-1$,  //$NON-NLS-2$
		}
		
	}

	
	

}
