package de.moonflower.jfritz.dialogs.configwizard;

import java.awt.*;

import javax.swing.*;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.utils.MultiLabel;

/**
 * 
 * @author Brian Jensen
 * 
 * This is the first panel, containing only an informational message
 * 
 * @see http://java.sun.com/developer/technicalArticles/GUI/swing/wizard/index.html
 *
 */
public class ConfigPanelFinish extends JPanel{

	    private static final long serialVersionUID = 1;
	    
	    private JPanel configPanel1;

	    public ConfigPanelFinish() {
	        
			configPanel1 = new JPanel();
			configPanel1.setLayout(new GridBagLayout());
			configPanel1.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
			GridBagConstraints c = new GridBagConstraints();
			c.insets.top = 2;
			c.insets.left = 5;
			c.anchor = GridBagConstraints.WEST;

			c.gridy = 0;
			c.gridx = 0;
			c.anchor = GridBagConstraints.LINE_START;
			MultiLabel label = new MultiLabel(Main.getMessage("config_wizard.finish"));
			configPanel1.add(label, c);
			
			add(configPanel1);
			
 	    }
	    
}