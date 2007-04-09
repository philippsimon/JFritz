package de.moonflower.jfritz.cellrenderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RightsCellRenderer extends DefaultTableCellRenderer {

	public static final long serialVersionUID = 100;
	
	public RightsCellRenderer(){
		super();
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JLabel label = (JLabel) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);
		
		if(value != null){
			boolean allow = Boolean.parseBoolean(value.toString());
			if(allow)
				label.setText("X");
			else
				label.setText("");
		}
		
		return label;
		
	}
}
