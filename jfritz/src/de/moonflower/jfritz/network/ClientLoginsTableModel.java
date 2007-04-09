package de.moonflower.jfritz.network;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.callerlist.filter.CallFilter;
import de.moonflower.jfritz.utils.Debug;

public class ClientLoginsTableModel extends AbstractTableModel{

	public static final long serialVersionUID = 100;
	
	private static Vector<Login> clientLogins = new Vector<Login>();
	
	public ClientLoginsTableModel(){
		super();
	}
	
    private final String columnNames[] = { Main.getMessage("username"), Main.getMessage("password"), //$NON-NLS-1$,  //$NON-NLS-2$
    		Main.getMessage("client_permissions"), Main.getMessage("client_calllist_filters"), 
    		Main.getMessage("client_telephonebook_filters")}; //$NON-NLS-1$,  //$NON-NLS-2$
	
	public int getColumnCount(){
		return columnNames.length;
	}
	
	public int getRowCount(){
		return clientLogins.size();
	}

	public Object getValueAt(int row, int column){
		
		Login login = clientLogins.elementAt(row);
		switch(column){
			case 0:
				return login.user;
			case 1:
				return login.password;

			default:
				return "";
		}
		
		//return "test: "+row+ " "+ column;
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
