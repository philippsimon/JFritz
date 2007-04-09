package de.moonflower.jfritz.cellrenderer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JOptionPane;

public class ButtonEditor extends DefaultCellEditor {

	public static final long serialVersionUID = 100;
	
	protected JButton button;
	private String label;
	private boolean isPushed;
	
	public ButtonEditor(JCheckBox checkBox){
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fireEditingStopped();
			}
		});
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column){
		
		if(isSelected){
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		}else{
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value == null) ? "" : value.toString();
		button.setText(label);
		isPushed = true;
		return button;
	}
	
	public Object getCellEditorValue(){
		if(isPushed){
			JOptionPane.showMessageDialog(button, label + ": Ouch!");
		}
		isPushed = false;
		return new String(label);
	}
	
	public boolean stopCellEditing(){
		isPushed = false;
		return super.stopCellEditing();
	}
	
	public void fireEditingStopped(){
		super.fireEditingStopped();
	}
}
