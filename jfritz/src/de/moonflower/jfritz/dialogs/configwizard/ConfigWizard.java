package de.moonflower.jfritz.dialogs.configwizard;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.*;
import java.util.Locale;

import com.nexes.wizard.*;

import de.moonflower.jfritz.dialogs.config.ConfigPanelCallMonitor;
import de.moonflower.jfritz.dialogs.config.ConfigPanelLang;
import de.moonflower.jfritz.dialogs.config.ConfigPanelFritzBox;
import de.moonflower.jfritz.dialogs.config.ConfigPanelMessage;
import de.moonflower.jfritz.dialogs.config.ConfigPanelPhone;
import de.moonflower.jfritz.exceptions.InvalidFirmwareException;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;

/**
 * 
 * @author Brian Jensen
 * 
 * This class uses the java wizard framework for creating a configurations wizard
 * current config panels include: phone settings, box settings, message settings,
 * call monitor settings. This wizard is be used to configure jfritz for first time users
 * 
 * @see http://java.sun.com/developer/technicalArticles/GUI/swing/wizard/index.html
 *
 *
 */
public class ConfigWizard {
	
	private Wizard wizard;
	
	private ConfigPanelFritzBoxDescriptor descriptor3;
	
	private WizardPanelDescriptor descriptor2, descriptor4, descriptor5;
	
	private boolean canceled = false;
	
	public ConfigWizard(Frame parent){
		
		Debug.msg("asking the user for the language");
		
		//if user clicked cancel on the language dialog, return back to jfritz
		askLanguage(parent);
			
		Debug.msg("Create JFritz config wizard");
		wizard = new Wizard(JFritz.getJframe());
        wizard.getDialog().setTitle(Main.getMessage("config_wizard"));
       	wizard.getDialog().setLocationRelativeTo(JFritz.getJframe());
        
       	
        //initialize the wizard with the correct order of the panels
        WizardPanelDescriptor descriptor1 = new ConfigPanel1Descriptor();
        wizard.registerWizardPanel(ConfigPanel1Descriptor.IDENTIFIER, descriptor1);
    
        descriptor2 = new ConfigPanelPhoneDescriptor();
        wizard.registerWizardPanel(ConfigPanelPhoneDescriptor.IDENTIFIER, descriptor2);
        
        descriptor3 = new ConfigPanelFritzBoxDescriptor();
        wizard.registerWizardPanel(ConfigPanelFritzBoxDescriptor.IDENTIFIER, descriptor3);
        
        descriptor4 = new ConfigPanelMessageDescriptor();
        wizard.registerWizardPanel(ConfigPanelMessageDescriptor.IDENTIFIER, descriptor4);
        
        descriptor5 = new ConfigPanelCallMonitorDescriptor(descriptor3.getFritzBoxPanel());
        wizard.registerWizardPanel(ConfigPanelCallMonitorDescriptor.IDENTIFIER, descriptor5);

        WizardPanelDescriptor finishDescriptor= new ConfigPanelFinishDescriptor();
        wizard.registerWizardPanel(ConfigPanelFinishDescriptor.IDENTIFIER, finishDescriptor);
        
        //set the first panel to be displayed
        wizard.setCurrentPanel(ConfigPanel1Descriptor.IDENTIFIER);
    
	}
	/**
	 * This function shows the wizard then stores the values if finish is clicked
	 * 
	 * @author Brian Jensen
	 * @throws IOException 
	 * @throws InvalidFirmwareException 
	 * @throws WrongPasswordException 
	 *
	 */
	public void showWizard() throws WrongPasswordException, InvalidFirmwareException, IOException{
		
		if(canceled)
			return;		
		
		//possible return values: 0 finish clicked, 1 cancel clicked, 2 error...
       int ret = wizard.showModalDialog();
       
       switch (ret){
       
       		case 0:
       			Debug.msg("Finished clicked, saving settings");       		
       			
       			((ConfigPanelPhone)descriptor2.getPanelComponent()).saveSettings();
       			((ConfigPanelFritzBox)descriptor3.getPanelComponent()).saveSettings();
       			((ConfigPanelMessage)descriptor4.getPanelComponent()).saveSettings();
       			((ConfigPanelCallMonitor)descriptor5.getPanelComponent()).saveSettings();
       			
      			JFritz.getFritzBox().detectFirmware();
      			Main.saveConfigProperties();
       			
      			// start call monitor if selected
				if (Integer.parseInt(Main.getProperty("option.callMonitorType", "1")) != 0)
				{
					JFritz.getJframe().switchMonitorButton();
				}
       			break;
       		case 1:
       			Debug.msg("Cancel clicked, not saving values");
       			break;
       		case 2:
       			Debug.msg("Error in the wizard, bailing out..");
       			break;
       			
       
       }
	
	}
	
	/**
	 * This dialog changes the language used in jfritz
	 * It is called before the initial creation of the real wizard,
	 * so that the wizard will be displayed in the proper language
	 * 
	 * @author Brian Jensen
	 * 
	 * @param parent
	 */
	public void askLanguage(Frame parent){
		
		wizard = new Wizard();
	    wizard.getDialog().setTitle(Main.getMessage("config_wizard"));
		
	    if(parent != null)
	       	wizard.getDialog().setLocationRelativeTo(parent);
	    
	    WizardPanelDescriptor descriptorLang = new ConfigPanelLangDescriptor();
        wizard.registerWizardPanel(ConfigPanelLangDescriptor.IDENTIFIER, descriptorLang);
	    
        wizard.setCurrentPanel(ConfigPanelLangDescriptor.IDENTIFIER);
        
		int ret = wizard.showModalDialog();
		//only change the language if finish was selected
		if(ret == 0){
			
			//get the components from the wizardpanel
			JComboBox languageCombo = ((ConfigPanelLang)descriptorLang.getPanelComponent()).languageCombo;
			String[] localeList = ((ConfigPanelLang)descriptorLang.getPanelComponent()).localeList;
			
			//This code is real ugly, i should get around to cleaning it up!
			if (!Main.getProperty("locale").equals(localeList[languageCombo.getSelectedIndex()])) { //$NON-NLS-1$ //$NON-NLS-2$
				Main.setProperty(
						"locale", localeList[languageCombo.getSelectedIndex()]); //$NON-NLS-1$
				String loc = localeList[languageCombo.getSelectedIndex()];
				JFritz.getJframe().setLanguage(	
						new Locale(loc.substring(0, loc.indexOf("_")), loc.substring(loc.indexOf("_")+1, loc.length())));
			}
		
		}else
			canceled =  true;
		
	}
	
}
