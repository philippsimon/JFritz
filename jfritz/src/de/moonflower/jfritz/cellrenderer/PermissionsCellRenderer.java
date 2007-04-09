package de.moonflower.jfritz.cellrenderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.moonflower.jfritz.Main;

public class PermissionsCellRenderer extends DefaultTableCellRenderer implements
	 ActionListener{

	public static final long serialVersionUID = 100;
	
	public PermissionsCellRenderer(){
		super();
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JButton popupButton = new JButton(Main.getMessage("set_permissions"));
		popupButton.setActionCommand("permissions");
		popupButton.addActionListener(this);
		panel.add(popupButton, BorderLayout.CENTER);
		return panel;
		
	}

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("set_permissions")){
			System.out.print("i got clicked yo!");
		}
	}

}
