package de.moonflower.jfritz;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.moonflower.jfritz.utils.JFritzProperties;

public class TestProperties {
	private static JFritzProperties windowProperties;	
	
	public final static String WINDOW_PROPERTIES_FILE = "jfritz.window.properties.xml"; //$NON-NLS-1$
	public final static String PROPERTIES_FILE = "jfritz.properties.xml"; //$NON-NLS-1$

	public static void main(String[] args) {
		Main.loadSaveDir();
		windowProperties = new JFritzProperties();
		try {
			System.out.println(Main.SAVE_DIR + PROPERTIES_FILE); 
			windowProperties.loadFromXML(Main.SAVE_DIR + PROPERTIES_FILE);
			System.err.println(windowProperties.size()); 
			System.err.println(windowProperties); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		windowProperties.setProperty("position.left", "100");
		
		try {
			windowProperties.storeToXML(Main.SAVE_DIR + WINDOW_PROPERTIES_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
