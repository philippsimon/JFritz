/*
 * $Id$
 * 
 * Created on 07.05.2005
 *
 */
package de.moonflower.jfritz.callerlist;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.dialogs.phonebook.PhoneBookTable;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;

/**
 * Displays status information about selected calls 
 * 
 * @author Arno Willig
 *  
 */
public class SelectionListener implements ListSelectionListener {

	private CallerTable table;

	private int selectedCalls = 0;
	
	private int selectedCallsTotalMinutes = 0;
	
	// It is necessary to keep the table since it is not possible
	// to determine the table from the event's source
	public SelectionListener(JTable table) {
		this.table = (CallerTable) table;
	}

	/**
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Person person = null;
			Call call = null;
			int rows[] = table.getSelectedRows();
			selectedCalls = rows.length;
			selectedCallsTotalMinutes = 0;
			for (int i = 0; i < rows.length; i++) {
				call = (Call) JFritz.getCallerList().getFilteredCallVector().get(rows[i]);
				selectedCallsTotalMinutes += call.getDuration();
			}

			if (rows.length == 1) {
				// table.getJfritz().getJframe().getPhoneBookPanel().getPersonPanel().setPerson(person);
				PhoneBookTable pt = JFritz.getJframe()
						.getPhoneBookPanel().getPhoneBookTable();
				Vector persons = JFritz.getPhonebook()
						.getFilteredPersons();
				person = call.getPerson();
				for (int i = 0; i < persons.size(); i++) {
					Person p = (Person) persons.get(i);
					if (p == person) {
						pt.getSelectionModel().setSelectionInterval(i, i);
						JFritz.getJframe().getPhoneBookPanel()
								.showPersonPanel();
						
						// assuring that the newly selected row in the phonebook is visible
						JViewport viewport = (JViewport)pt.getParent();
						Rectangle rect = pt.getCellRect(i, 0, true);
						Point vp = viewport.getViewPosition();
						rect.setLocation(rect.x-vp.x, rect.y-vp.y);
						viewport.scrollRectToVisible(rect);
						
						break;
					}
				}
				JFritz.getJframe().getCallerListPanel()
						.setDeleteEntryButton();
				JFritz.getJframe().setStatus();
			} else if (rows.length > 0) {
				// Setze Statusbar mit Infos �ber selektierte Anrufe
				JFritz.getJframe().setStatus(JFritz.getMessage("entries").replaceAll( //$NON-NLS-1$
						"%N", Integer.toString(selectedCalls)) + ", "  //$NON-NLS-1$,  //$NON-NLS-2$
                        + JFritz.getMessage("total_duration") + ": " + (selectedCallsTotalMinutes / 60) + " min"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
				if (rows.length == table.getRowCount())
					JFritz.getJframe().getCallerListPanel()
							.setDeleteListButton();
				else
					JFritz.getJframe().getCallerListPanel()
							.setDeleteEntriesButton(rows.length);
			} else {
				JFritz.getJframe().getCallerListPanel()
						.disableDeleteEntriesButton();
			}
		}
	}
}
