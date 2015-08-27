/*
 * $Id$
 * 
 * Created on 14.04.2005
 *
 */
package de.moonflower.jfritz.callerlist;

import java.awt.Color;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;

/**
 * This deprecated class manages editing of the participant cell in the caller table.
 * 
 * @author Arno Willig
 * 
 */
public class CommentCellEditor extends AbstractCellEditor implements
		TableCellEditor {
	private static final long serialVersionUID = 1;
    private JTextField textField = new JTextField();
    private String oldText;
	private String newText;
	/**
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (isSelected) {
			// cell (and perhaps other cells) are selected
		}
		textField.setBackground(new Color(127, 255, 255));
		// Configure the component with the specified value
		String strval = ""; //$NON-NLS-1$
		if (value != null)
			strval = value.toString();
		oldText = strval;
		textField.setText(strval);
		// Return the configured component
		return textField;
	}

	/**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return textField.getText();
	}

	public boolean stopCellEditing() {
		String s = (String) getCellEditorValue();
		if (!isValid(s)) { // Should display an error message at this point
			return false;
		}
		newText = s;
		return super.stopCellEditing();
	}

	public boolean isValid(String s) {
		return true;
	}

	/**
	 * @see javax.swing.AbstractCellEditor#fireEditingCanceled()
	 */
	protected void fireEditingCanceled() {
		super.fireEditingCanceled();
	}

	/**
	 * @see javax.swing.AbstractCellEditor#fireEditingStopped()
	 */
	protected void fireEditingStopped() {
		super.fireEditingStopped();
		if(!oldText.equals(newText)){
			JFritz.getCallerList().saveToXMLFile(Main.SAVE_DIR + JFritz.CALLS_FILE, true);
		}
	}

}
