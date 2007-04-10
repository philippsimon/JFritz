package de.moonflower.jfritz.network;

import java.util.Vector;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;


import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.callerlist.filter.CallFilter;
import de.moonflower.jfritz.dialogs.config.PermissionsDialog;
import de.moonflower.jfritz.utils.Debug;

public class ClientLoginsTableModel extends AbstractTableModel{

	public static final long serialVersionUID = 100;
	
	private static Vector<Login> clientLogins = new Vector<Login>();
	
	public ClientLoginsTableModel(){
		super();
	}
	
    private final String columnNames[] = { Main.getMessage("username"), Main.getMessage("password"), //$NON-NLS-1$,  //$NON-NLS-2$
    		Main.getMessage("permissions"), Main.getMessage("callerlist_filters"), 
    		Main.getMessage("phonebook_filters")}; //$NON-NLS-1$,  //$NON-NLS-2$
	
	public int getColumnCount(){
		return columnNames.length;
	}
	
	public int getRowCount(){
		return clientLogins.size();
	}

	public boolean isCellEditable(int row, int col){

		return true;
	}
	
	public Object getValueAt(int row, int column){
		
		Login login = clientLogins.elementAt(row);
		switch(column){
			case 0:
				return login.user;
			case 1:
				return login.password;
			case 2:
				return Main.getMessage("set");
			case 3:
				return Main.getMessage("set");
			case 4:
				return Main.getMessage("set");
			default:
				return "";
		}
		
	}

	public void setValueAt(Object value, int row, int column){
		Login login = clientLogins.elementAt(row);
		switch(column){
		case 0:
			login.user = value.toString();
			break;
		case 1:
			login.password = value.toString();
			break;
		case 2:
			if(value instanceof JDialog){
				PermissionsDialog dialog = new PermissionsDialog((JDialog) value,login);
				dialog.showConfigDialog();
				dialog.dispose();
			}
			break;
		case 3:
		case 4:
			if(value instanceof JDialog){
				JOptionPane.showMessageDialog((Component) value, "Function not yet implemented!!");
			}
		}
	}
	
    public String getColumnName(int column) {
        return columnNames[column];
    }
	
    public static Vector<Login> getClientLogins(){
    	return clientLogins;
    }
    
    public static void loadClientLogins(){
    	Debug.msg("Loading client logins");
    	Login login = new Login("Brian", "password", true, true, true, true, true,
    			true, true, true, new Vector<CallFilter>(), "");
    	clientLogins.add(login);
    	login = new Login("dummy", "none", false, false, false, false, false, false, false,
    			false, new Vector<CallFilter>(), "");
    	clientLogins.add(login);
    }
    
}
