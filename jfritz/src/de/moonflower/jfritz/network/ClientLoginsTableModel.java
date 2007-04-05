package de.moonflower.jfritz.network;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.utils.Debug;

public class ClientLoginsTableModel extends AbstractTableModel{

	private static Vector<Login> clientLogins = new Vector<Login>();
	
	public ClientLoginsTableModel(){
		super();
	}
	
    private final String columnNames[] = { Main.getMessage("username"), Main.getMessage("password"), //$NON-NLS-1$,  //$NON-NLS-2$
    		Main.getMessage("allow_add"), Main.getMessage("allow_remove"), Main.getMessage("allow_update") }; //$NON-NLS-1$,  //$NON-NLS-2$
	
	public int getColumnCount(){
		return 5;
	}
	
	public int getRowCount(){
		return 4;
	}

	public Object getValueAt(int row, int column){
		return "test: "+row+ " "+ column;
	}

    public String getColumnName(int column) {
        return columnNames[column];
    }
	
    public static Vector<Login> getClientLogins(){
    	return clientLogins;
    }
    
    public static void loadClientLogins(){
    	Debug.msg("Loading client logins");
    	Login login = new Login("Brian", "password", true, true, true);
    	clientLogins.add(login);
    }
    
}
