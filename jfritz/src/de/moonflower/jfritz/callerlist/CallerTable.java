/*
 * $Id$
 * 
 * Created on 08.05.2005
 *
 */
package de.moonflower.jfritz.callerlist;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.cellrenderer.CallByCallCellRenderer;
import de.moonflower.jfritz.cellrenderer.CallTypeCellRenderer;
import de.moonflower.jfritz.cellrenderer.CommentCellRenderer;
import de.moonflower.jfritz.cellrenderer.DateCellRenderer;
import de.moonflower.jfritz.cellrenderer.DurationCellRenderer;
import de.moonflower.jfritz.cellrenderer.NumberCellRenderer;
import de.moonflower.jfritz.cellrenderer.PersonCellRenderer;
import de.moonflower.jfritz.cellrenderer.PortCellRenderer;
import de.moonflower.jfritz.cellrenderer.RouteCellRenderer;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * Creates table of callers
 * 
 */
public class CallerTable extends JTable {
	private static final long serialVersionUID = 1;

	private static final String TRUE = "true";

	private static final String FALSE = "false";

	private static final int MAXCOLUMNCOUNT = 9;

	private TableColumn callByCallColumn = null;

	private TableColumn commentColumn = null;

	private TableColumn portColumn = null;

	private ColumnHeaderToolTips headerTips;

	final CallerTable table = this;

	/**
	 * Constructs CallerTable
	 * 
	 */
	public CallerTable(CallerList list) {
		super(list);
		headerTips = new ColumnHeaderToolTips();
		setTableProperties();
		createColumns();
	}

	/**
	 * sets some properties of the CallerTable
	 */
	private void setTableProperties() {
		setRowHeight(24);
		setAutoCreateColumnsFromModel(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		setFocusable(true);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		getTableHeader().setReorderingAllowed(true);
		getTableHeader().setResizingAllowed(true);
		getTableHeader().addMouseListener(new ColumnHeaderListener(getModel()));

		KeyListener keyListener = (new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					// clear selection
					table.clearSelection();
					JFritz.getJframe().setStatus();
				} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					// Delete selected entries
					((CallerList) getModel()).removeEntries(getSelectedRows());
				}
			}
		});

		addKeyListener(keyListener);

		SelectionListener listener = new SelectionListener(this);
		getSelectionModel().addListSelectionListener(listener);
	}

	/**
	 * Erstellt eine Spalte mit dem Namen columnName an der Position i mit dem
	 * Standard-Renderer renderer und liefert eine Referenz auf das erstellte
	 * Spalten-Objekt
	 * 
	 * @param position
	 *            Spaltenposition
	 * @param columnName
	 *            Spaltenname
	 * @param renderer
	 *            Spalten-Renderer
	 * @return Referenz auf erstelltes Spalten-Objekt
	 */
	private TableColumn createColumn(int position, String columnName,
			DefaultTableCellRenderer renderer) {
		TableColumn col = getColumnModel().getColumn(position);
		col.setIdentifier(columnName); //$NON-NLS-1$
		col.setHeaderValue(Main.getMessage(columnName)); //$NON-NLS-1$
		col.setCellRenderer(renderer);
		headerTips.setToolTip(col, Main.getMessage(columnName + "_desc")); //$NON-NLS-1$
		col.setMinWidth(10);
		col.setMaxWidth(1600);
		col.setPreferredWidth(Integer.parseInt(Main.getProperty(
				"column." + columnName + ".width", "70"))); //$NON-NLS-1$,  //$NON-NLS-2$
		return col;
	}

	/**
	 * Creates the columns of the CallerTable
	 * 
	 * @param messages
	 */
	private void createColumns() {

		createColumn(0, "type", new CallTypeCellRenderer());

		createColumn(1, "date", new DateCellRenderer());

		callByCallColumn = createColumn(2, "callbycall",
				new CallByCallCellRenderer());

		TableColumn numberColumn = createColumn(3, "number",
				new NumberCellRenderer());
		numberColumn.setCellEditor(new CallCellEditor());

		TableColumn participantColumn = createColumn(4, "participant",
				new PersonCellRenderer());
		participantColumn.setCellEditor(new PersonCellEditor(
				(CallerList) getModel()));

		portColumn = createColumn(5, "port", new PortCellRenderer());

		createColumn(6, "route", new RouteCellRenderer());

		createColumn(7, "duration", new DurationCellRenderer());

		commentColumn = createColumn(8, "comment", new CommentCellRenderer());
		commentColumn.setCellEditor(new CommentCellEditor());

		showHideColumns();

		for (int i = 0; i < getColumnCount(); i++) {
			String columnName = Main.getProperty("column" + i + ".name", ""); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
			// Debug.msg("column"+i+".name:
			// "+Main.getProperty("column"+i+".name",""));
			if (!columnName.equals("")) { //$NON-NLS-1$
				if (getColumnIndex(columnName) != -1) {
					moveColumn(getColumnIndex(columnName), i);
				}
			}
		}

		getTableHeader().addMouseMotionListener(headerTips);
	}

	public Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
			int vColIndex) {
		Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
		if ((rowIndex % 2 == 0) && !isCellSelected(rowIndex, vColIndex)) {
			c.setBackground(new Color(255, 255, 200));
		} else if (!isCellSelected(rowIndex, vColIndex)) {
			// If not shaded, match the table's background
			c.setBackground(getBackground());
		} else {
			c.setBackground(new Color(204, 204, 255));
		}
		return c;
	}

	public void saveColumnStatus() {
		TableColumnModel colModel = getColumnModel();
		for (int i = 0; i < colModel.getColumnCount(); i++) {
			TableColumn col = getColumnModel().getColumn(i);
			Main.setProperty("column." + col.getIdentifier() + ".width", ""
					+ col.getPreferredWidth());
		}
		try {
			colModel.getColumnIndex("callbycall");
			Main.setProperty("option.showCallByCallColumn", "" + TRUE);
		} catch (IllegalArgumentException iae) {
			Main.setProperty("option.showCallByCallColumn", "" + FALSE);
		}
		try {
			colModel.getColumnIndex("comment");
			Main.setProperty("option.showCommentColumn", "" + TRUE);
		} catch (IllegalArgumentException iae) {
			Main.setProperty("option.showCommentColumn", "" + FALSE);
		}
		try {
			colModel.getColumnIndex("port");
			Main.setProperty("option.showPortColumn", "" + TRUE);
		} catch (IllegalArgumentException iae) {
			Main.setProperty("option.showPortColumn", "" + FALSE);
		}

		for (int i = 0; i < MAXCOLUMNCOUNT; i++) {
			try {
				Main.setProperty("" + "column" + i + ".name", ""
						+ colModel.getColumn(i).getIdentifier());
				// Debug.msg("" + "column" + i + ".name" + "="+
				// colModel.getColumn(i).getIdentifier());
			} catch (IllegalArgumentException iae) {
				Main.setProperty("column" + i + ".name", "");
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				Main.setProperty("column" + i + ".name", "");
			}
		}/*
			 * String columnName = Main.getProperty("column"+i+".name","");
			 * //$NON-NLS-1$, //$NON-NLS-2$, //$NON-NLS-3$ if
			 * (!columnName.equals("")) { //$NON-NLS-1$ if
			 * (getColumnIndex(columnName) != -1) {
			 * moveColumn(getColumnIndex(columnName), i); } }
			 * 
			 */
	}

	/**
	 * 
	 * Bestimmt die Spaltennummer zu einer bestimmten SpaltenID SpaltenID =
	 * type, duration, port, participant etc.
	 * 
	 */
	public int getColumnIndex(String columnIdentifier) {
		for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
			TableColumn currentColumn = getColumnModel().getColumn(i);
			if (currentColumn.getIdentifier().toString().equals(
					columnIdentifier)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Existiert die Spalte mit dem Namen columnName?
	 * 
	 * @param columnName
	 * @return true, wenn Spalte existiert
	 */
	public boolean columnExists(String columnName) {
		TableColumnModel colModel = getColumnModel();
		try {
			colModel.getColumnIndex(columnName);
			// column found
			return true;
		} catch (IllegalArgumentException iae) {
			// No column found
			return false;
		}
	}

	/**
	 * F�gt die Spalte column mit dem Namen columnName ein
	 * 
	 * @param columnName
	 * @param column
	 */
	private void showColumn(String columnName, TableColumn column) {
		TableColumnModel colModel = getColumnModel();
		if (!columnExists(columnName)) {
			colModel.addColumn(column);
			Debug.msg("Showing " + columnName + " column"); //$NON-NLS-1$
			colModel.getColumn(getColumnCount() - 1).setPreferredWidth(
					Integer.parseInt(Main.getProperty(
							"column." + columnName + ".width", "50"))); //$NON-NLS-1$, //$NON-NLS-2$
		}
	}

	/**
	 * Versteckt die Spalte column mit dem Namen columnName
	 * 
	 * @param columnName
	 */
	private void hideColumn(String columnName) {
		TableColumnModel colModel = getColumnModel();
		if (columnExists(columnName)) {
			try {
				// Try to remove Call-By-Call Column
				colModel.removeColumn(colModel.getColumn(colModel
						.getColumnIndex(columnName))); //$NON-NLS-1$
				Debug.msg("Hiding " + columnName + " column"); //$NON-NLS-1$
			} catch (IllegalArgumentException iae) {
				// No CbC-Column found
			}
		}
	}

	/**
	 * Blendet die Spalten ein oder aus
	 * 
	 */
	public void showHideColumns() {
		if (!JFritzUtils.parseBoolean(Main.getProperty(
				"option.showCallByCallColumn", "true"))) { //$NON-NLS-1$, //$NON-NLS-2$
			hideColumn("callbycall");
		} else {
			showColumn("callbycall", callByCallColumn);
		}

		if (!JFritzUtils.parseBoolean(Main.getProperty(
				"option.showCommentColumn", "true"))) { //$NON-NLS-1$,  //$NON-NLS-2$
			hideColumn("comment");
		} else {
			showColumn("comment", commentColumn);
		}

		if (!JFritzUtils.parseBoolean(Main.getProperty(
				"option.showPortColumn", "true"))) { //$NON-NLS-1$,  //$NON-NLS-2$
			hideColumn("port");
		} else {
			showColumn("port", portColumn);
		}
	}
}
