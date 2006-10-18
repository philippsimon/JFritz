/*
 * Created on 05.06.2005
 *
 */
package de.moonflower.jfritz.callerlist;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.toedter.calendar.JDateChooser;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.callerlist.filter.CallByCallFilter;
import de.moonflower.jfritz.callerlist.filter.CallFilter;
import de.moonflower.jfritz.callerlist.filter.CallInFailedFilter;
import de.moonflower.jfritz.callerlist.filter.CallInFilter;
import de.moonflower.jfritz.callerlist.filter.CallOutFilter;
import de.moonflower.jfritz.callerlist.filter.CommentFilter;
import de.moonflower.jfritz.callerlist.filter.DateFilter;
import de.moonflower.jfritz.callerlist.filter.FixedFilter;
import de.moonflower.jfritz.callerlist.filter.HandyFilter;
import de.moonflower.jfritz.callerlist.filter.AnonymFilter;
import de.moonflower.jfritz.callerlist.filter.SearchFilter;
import de.moonflower.jfritz.callerlist.filter.SipFilter;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;
import de.moonflower.jfritz.utils.ThreeStateButton;
import de.moonflower.jfritz.utils.JFritzClipboard;
import de.moonflower.jfritz.utils.reverselookup.ReverseLookup;

/**
 * @author Arno Willig
 * @author marc
 */

//TODO evtl start und enddate richtig setzten, wenn man einen datefilter
//aktiviert und
//zeilen selektiert hat
//TODO delete button
public class CallerListPanel extends JPanel implements ActionListener,
KeyListener, PropertyChangeListener {

	class PopupListener extends MouseAdapter {
		JPopupMenu popupMenu;

		PopupListener(JPopupMenu popupMenu) {
			super();
			this.popupMenu = popupMenu;
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mouseClicked(MouseEvent e) {
//FIXME Listener in den table einbauen
			if ((e.getClickCount() > 1)
					&& (e.getComponent().getClass() != ThreeStateButton.class)) {
				JFritz.getJframe().activatePhoneBook();
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}

	private static final String DELETE_ENTRIES = "delete_entries";

	private static final String DELETE_ENTRY = "delete_entry";

	private static final String FALSE = "false";

	private static final String FILTER_CALLBYCALL = "filter_callbycall";

	private static final String FILTER_CALLIN = "filter_callin";

	public static final String FILTER_CALLINFAILED = "filter_callinfailed";

	private static final String FILTER_CALLOUT = "filter_callout";

	private static final String FILTER_COMMENT = "filter_comment";

	private static final String FILTER_DATE = "filter_date";

	private static final String FILTER_FIXED = "filter_fixed";

	private static final String FILTER_HANDY = "filter_handy";

	private static final String FILTER_ANONYM = "filter_number";

	private static final String FILTER_SEARCH = "filter_search";

	private static final String FILTER_SEARCH_TEXT = "filter_search.text";

	private static final String FILTER_SIP = "filter_sip";

	private static final long serialVersionUID = 1;

	private static final String FILTER_DATE_END = "FILTER_DATE_END";

	private static final String FILTER_DATE_START = "FILTER_DATE_START";

	private JButton applyFilterButton;


	private CallerList callerList;

	private CallerTable callerTable;

	private CallFilter  callByCallFilter, callInFailedFilter, callInFilter, callOutFilter
	, commentFilter, dateFilter, handyFilter, fixedFilter, searchFilter, sipFilter, anonymFilter;

	private ThreeStateButton dateFilterButton, callByCallFilterButton,
	callInFilterButton, callOutFilterButton, callInFailedFilterButton,
	anonymFilterButton, fixedFilterButton, handyFilterButton,
	sipFilterButton, commentFilterButton, searchFilterButton;

	private JButton deleteEntriesButton;

	private JDateChooser endDateChooser;

	private JTextField searchFilterTextField;

	private JLabel searchLabel;

	// private FixedFilter fixedFilter;

	private JDateChooser startDateChooser;

	private WindowAdapter wl;

	public CallerListPanel(CallerList callerList, JFrame parent) {
		super();
		this.callerList = callerList;
		createFilters(callerList);
		setLayout(new BorderLayout());
		add(createToolBar(), BorderLayout.NORTH);
		add(createCallerListTable(), BorderLayout.CENTER);
		wl = new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				writeButtonStatus();
			}
		};
		parent.addWindowListener(wl);
	}

	private void createFilters(CallerList callerList) {
		callByCallFilter = new CallByCallFilter(callerList.getSelectedCbCProviders());
		callerList.addFilter(callByCallFilter);
		callInFailedFilter = new CallInFailedFilter();
		callerList.addFilter(callInFailedFilter);
		callInFilter = new CallInFilter();
		callerList.addFilter(callInFilter);
		callOutFilter = new CallOutFilter();
		callerList.addFilter(callOutFilter);
		commentFilter = new CommentFilter();
		callerList.addFilter(commentFilter);
		dateFilter = new DateFilter(new Date(), new Date());
		callerList.addFilter(dateFilter);
		anonymFilter = new AnonymFilter();
		callerList.addFilter(anonymFilter);
		fixedFilter = new FixedFilter();
		callerList.addFilter(fixedFilter);
		handyFilter = new HandyFilter();
		callerList.addFilter(handyFilter);
		searchFilter = new SearchFilter("");
		callerList.addFilter(searchFilter);
		sipFilter = new SipFilter(callerList.getSelectedOrSipProviders());
		callerList.addFilter(sipFilter);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		handleAction(e.getActionCommand());
		// callerList.updateFilter();
		callerList.update();
	}

	/*
	 * disable all filters and hide the search and date stuff
	 */
	private void clearAllFilter() {
		callInFilterButton.setState(ThreeStateButton.NOTHING);
		callOutFilterButton.setState(ThreeStateButton.NOTHING);
		callInFailedFilterButton.setState(ThreeStateButton.NOTHING);
		anonymFilterButton.setState(ThreeStateButton.NOTHING);
		fixedFilterButton.setState(ThreeStateButton.NOTHING);
		handyFilterButton.setState(ThreeStateButton.NOTHING);
		dateFilterButton.setState(ThreeStateButton.NOTHING);
		searchFilterButton.setState(ThreeStateButton.NOTHING);
		searchFilterTextField.setVisible(false);
		startDateChooser.setVisible(false);
		endDateChooser.setVisible(false);
		searchLabel.setVisible(false);
		sipFilterButton.setState(ThreeStateButton.NOTHING);
		callByCallFilterButton.setState(ThreeStateButton.NOTHING);
		commentFilterButton.setState(ThreeStateButton.NOTHING);
		syncAllFilters();
		callerList.update();
	}

	public JScrollPane createCallerListTable() {
		callerTable = new CallerTable();
		JPopupMenu callerlistPopupMenu = new JPopupMenu();
		JMenuItem menuItem;
		menuItem = new JMenuItem(Main.getMessage("reverse_lookup")); //$NON-NLS-1$
		menuItem.setActionCommand("reverselookup"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		callerlistPopupMenu.add(menuItem);

		callerlistPopupMenu.addSeparator();

		menuItem = new JMenuItem(Main.getMessage("export_csv")); //$NON-NLS-1$
		menuItem.setActionCommand("export_csv"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		callerlistPopupMenu.add(menuItem);

		menuItem = new JMenuItem(Main.getMessage("import_callerlist_csv")); //$NON-NLS-1$
		menuItem.setActionCommand("import_callerlist_csv"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		menuItem.setEnabled(true);
		callerlistPopupMenu.add(menuItem);

		menuItem = new JMenuItem(Main.getMessage("export_xml")); //$NON-NLS-1$
		menuItem.setActionCommand("export_xml"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		callerlistPopupMenu.add(menuItem);

		menuItem = new JMenuItem(Main.getMessage("import_xml")); //$NON-NLS-1$
		menuItem.setActionCommand("import_xml"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		menuItem.setEnabled(false);
		callerlistPopupMenu.add(menuItem);

		callerlistPopupMenu.addSeparator();

		JMenu clipboardMenu = new JMenu(Main.getMessage("clipboard")); //$NON-NLS-1$
		clipboardMenu.setMnemonic(KeyEvent.VK_Z);

		JMenuItem item = new JMenuItem(Main.getMessage("number"), KeyEvent.VK_N); //$NON-NLS-1$
		item.setActionCommand("clipboard_number"); //$NON-NLS-1$
		item.addActionListener(this);
		clipboardMenu.add(item);

		item = new JMenuItem(Main.getMessage("address"), KeyEvent.VK_A); //$NON-NLS-1$
		item.setActionCommand("clipboard_adress"); //$NON-NLS-1$
		item.addActionListener(this);
		clipboardMenu.add(item);

		callerlistPopupMenu.add(clipboardMenu);

		MouseAdapter popupListener = new PopupListener(callerlistPopupMenu);

		callerTable.addMouseListener(popupListener);

		return new JScrollPane(callerTable);
	}

	public JPanel createToolBar() {
		JToolBar upperToolBar = new JToolBar();
		upperToolBar.setFloatable(true);
		JToolBar lowerToolbar = new JToolBar();
		lowerToolbar.setFloatable(true);

		JButton resetFiltersButton = new JButton();
		resetFiltersButton.setActionCommand("export_csv"); //$NON-NLS-1$
		resetFiltersButton.addActionListener(this);
		resetFiltersButton.setIcon(getImageIcon("csv_export.png")); //$NON-NLS-1$
		resetFiltersButton.setToolTipText(Main.getMessage("export_csv")); //$NON-NLS-1$
		upperToolBar.add(resetFiltersButton);

		resetFiltersButton = new JButton();
		resetFiltersButton.setActionCommand("import_csv"); //$NON-NLS-1$
		resetFiltersButton.addActionListener(this);
		resetFiltersButton.setIcon(getImageIcon("csv_import.png")); //$NON-NLS-1$
		resetFiltersButton.setToolTipText("CSV-Datei importieren"); //$NON-NLS-1$
		resetFiltersButton.setEnabled(false);
		upperToolBar.add(resetFiltersButton);

		resetFiltersButton = new JButton();
		resetFiltersButton.setActionCommand("export_xml"); //$NON-NLS-1$
		resetFiltersButton.addActionListener(this);
		resetFiltersButton.setIcon(getImageIcon("xml_export.png")); //$NON-NLS-1$
		resetFiltersButton.setToolTipText("XML-Datei exportieren"); //$NON-NLS-1$
		upperToolBar.add(resetFiltersButton);

		resetFiltersButton = new JButton();
		resetFiltersButton.setActionCommand("import_xml"); //$NON-NLS-1$
		resetFiltersButton.addActionListener(this);
		resetFiltersButton.setIcon(getImageIcon("xml_import.png")); //$NON-NLS-1$
		resetFiltersButton.setToolTipText("XML-Datei importieren"); //$NON-NLS-1$
		resetFiltersButton.setEnabled(false);
		upperToolBar.add(resetFiltersButton);

		upperToolBar.addSeparator();

		callInFilterButton = new ThreeStateButton(getImageIcon("callin.png"));
		callInFilterButton.setActionCommand(FILTER_CALLIN);
		callInFilterButton.addActionListener(this);
		callInFilterButton.setToolTipText(Main.getMessage(FILTER_CALLIN)); 

		callInFailedFilterButton = new ThreeStateButton(getImageIcon("callinfailed.png")); //$NON-NLS-1$
		callInFailedFilterButton.setActionCommand(FILTER_CALLINFAILED);
		callInFailedFilterButton.addActionListener(this);
		callInFailedFilterButton.setToolTipText(Main
				.getMessage(FILTER_CALLINFAILED)); 

		JPopupMenu missedPopupMenu = new JPopupMenu();
		JMenuItem menuItem;
		menuItem = new JMenuItem(Main
				.getMessage("missed_calls_without_comments_last_week")); //$NON-NLS-1$
		menuItem
		.setActionCommand("filter_callinfailed_allWithoutCommentLastWeek"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		missedPopupMenu.add(menuItem);
		menuItem = new JMenuItem(Main
				.getMessage("missed_calls_without_comments")); //$NON-NLS-1$
		menuItem.setActionCommand("filter_callinfailed_allWithoutComment"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		missedPopupMenu.add(menuItem);
		MouseAdapter popupListener = new PopupListener(missedPopupMenu);
		callInFailedFilterButton.addMouseListener(popupListener);

		callOutFilterButton = new ThreeStateButton(getImageIcon("callout.png"));
		callOutFilterButton.setActionCommand(FILTER_CALLOUT); 
		callOutFilterButton.addActionListener(this);
		callOutFilterButton.setToolTipText(Main.getMessage(FILTER_CALLOUT));

		anonymFilterButton = new ThreeStateButton(
				getImageIcon("phone_nonumber.png")); //$NON-NLS-1$
		anonymFilterButton.setActionCommand(FILTER_ANONYM);
		anonymFilterButton.addActionListener(this);
		anonymFilterButton.setToolTipText(Main.getMessage(FILTER_ANONYM)); 

		fixedFilterButton = new ThreeStateButton(getImageIcon("phone.png")); //$NON-NLS-1$
		fixedFilterButton.setActionCommand(FILTER_FIXED);
		fixedFilterButton.addActionListener(this);
		fixedFilterButton.setToolTipText(Main.getMessage(FILTER_FIXED)); 

		handyFilterButton = new ThreeStateButton(getImageIcon("handy.png")); //$NON-NLS-1$
		handyFilterButton.setActionCommand(FILTER_HANDY);
		handyFilterButton.addActionListener(this);
		handyFilterButton.setToolTipText(Main.getMessage(FILTER_HANDY)); 

		dateFilterButton = new ThreeStateButton(getImageIcon("calendar.png")); //$NON-NLS-1$
				
		dateFilterButton.setActionCommand(FILTER_DATE); 
		dateFilterButton.addActionListener(this);
		dateFilterButton.setToolTipText(Main.getMessage(FILTER_DATE));

		setDateFilterText();// TODO das hier mal anschauen
		JPopupMenu datePopupMenu = new JPopupMenu();
		menuItem = new JMenuItem(Main.getMessage("date_filter_today")); //$NON-NLS-1$
		menuItem.setActionCommand("setdatefilter_thisday"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		datePopupMenu.add(menuItem);
		menuItem = new JMenuItem(Main.getMessage("date_filter_yesterday")); //$NON-NLS-1$
		menuItem.setActionCommand("setdatefilter_yesterday"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		datePopupMenu.add(menuItem);
		menuItem = new JMenuItem(Main.getMessage("date_filter_this_month")); //$NON-NLS-1$
		menuItem.setActionCommand("setdatefilter_thismonth"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		datePopupMenu.add(menuItem);
		menuItem = new JMenuItem(Main.getMessage("date_filter_last_month")); //$NON-NLS-1$
		menuItem.setActionCommand("setdatefilter_lastmonth"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		datePopupMenu.add(menuItem);
		popupListener = new PopupListener(datePopupMenu);

		dateFilterButton.addMouseListener(popupListener);

		startDateChooser = new JDateChooser();
		startDateChooser.setVisible(false);
		startDateChooser.addPropertyChangeListener("date", this);
		endDateChooser = new JDateChooser();
		endDateChooser.setDate(Calendar.getInstance().getTime());
		endDateChooser.setVisible(false);
		endDateChooser.addPropertyChangeListener("date", this);

		sipFilterButton = new ThreeStateButton(getImageIcon("world.png")); //$NON-NLS-1$
		sipFilterButton.setActionCommand(FILTER_SIP); 
		sipFilterButton.addActionListener(this);
		sipFilterButton.setToolTipText(Main.getMessage(FILTER_SIP)); 

		callByCallFilterButton = new ThreeStateButton(
				getImageIcon("callbycall.png")); //$NON-NLS-1$
		callByCallFilterButton.setActionCommand(FILTER_CALLBYCALL); 
		callByCallFilterButton.addActionListener(this);
		callByCallFilterButton.setToolTipText(Main
				.getMessage(FILTER_CALLBYCALL)); 

		commentFilterButton = new ThreeStateButton(getImageIcon("commentFilter.png"));
		commentFilterButton.setActionCommand(FILTER_COMMENT); 
		commentFilterButton.addActionListener(this);
		commentFilterButton.setToolTipText(Main.getMessage(FILTER_COMMENT)); 

		searchFilterButton = new ThreeStateButton(getImageIcon("searchfilter.png"));
		searchFilterButton.setActionCommand(FILTER_SEARCH); 
		searchFilterButton.addActionListener(this);
		searchFilterButton.setToolTipText(Main.getMessage(FILTER_SEARCH)); 
		searchFilterTextField = new JTextField(10);

		deleteEntriesButton = new JButton();
		deleteEntriesButton.setToolTipText(Main.getMessage(DELETE_ENTRIES)
				.replaceAll("%N", "")); //$NON-NLS-1$,  //$NON-NLS-2$,  
		deleteEntriesButton.setActionCommand(DELETE_ENTRY); 
		deleteEntriesButton.addActionListener(this);
		deleteEntriesButton.setIcon(getImageIcon("delete.png")); //$NON-NLS-1$
		deleteEntriesButton.setFocusPainted(false);
		deleteEntriesButton.setEnabled(false);

		//applyFilterButton = new JButton();
		// applyFilterButton.setToolTipText(Main.getMessage("apply_filters").replaceAll("%N",
		// "")); //$NON-NLS-1$, //$NON-NLS-2$, //$NON-NLS-3$
		//applyFilterButton.setActionCommand("apply_filter"); //$NON-NLS-1$
		//applyFilterButton.addActionListener(this);
		//applyFilterButton.setIcon(getImage("apply_filter.png")); //$NON-NLS-1$
		//applyFilterButton.setVisible(false);

		searchLabel = new JLabel(Main.getMessage("search") + ": ");//$NON-NLS-1$,  //$NON-NLS-2$
		searchLabel.setVisible(false);
		searchFilterTextField.addKeyListener(this);
		searchFilterTextField.setVisible(false);

		resetFiltersButton = new JButton(Main.getMessage("clear")); //$NON-NLS-1$
		resetFiltersButton.setActionCommand("clearFilter"); //$NON-NLS-1$
		resetFiltersButton.addActionListener(this);
	//	ThreeStateButton testButton = new ThreeStateButton(getImage("searchfilter.png"));
		
		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BorderLayout());
		// Icons sind noch zu gro�, deshalb erst einmal auskommentiert
		// toolbarPanel.add(upperToolBar, BorderLayout.NORTH);
		
		/** **********add all Buttons and stuff to the lowerToolbar************** */
//		lowerToolbar.add(testButton);
		lowerToolbar.add(callInFilterButton);
		lowerToolbar.add(callInFailedFilterButton);
		lowerToolbar.add(callOutFilterButton);
		lowerToolbar.add(anonymFilterButton);
		lowerToolbar.add(fixedFilterButton);
		lowerToolbar.add(handyFilterButton);
		lowerToolbar.add(sipFilterButton);
		lowerToolbar.add(callByCallFilterButton);
		lowerToolbar.add(commentFilterButton);
		lowerToolbar.addSeparator();
		lowerToolbar.add(dateFilterButton);
		lowerToolbar.add(startDateChooser);
		lowerToolbar.add(endDateChooser);
		lowerToolbar.addSeparator();
		lowerToolbar.add(searchFilterButton);
		lowerToolbar.add(searchLabel);
		lowerToolbar.add(searchFilterTextField);
		lowerToolbar.addSeparator();
//		lowerToolbar.add(applyFilterButton);
		lowerToolbar.add(resetFiltersButton);
		lowerToolbar.addSeparator();
		lowerToolbar.addSeparator();
		lowerToolbar.add(deleteEntriesButton);
		toolbarPanel.add(lowerToolbar, BorderLayout.SOUTH);
		readButtonStatus();
		return toolbarPanel;
	}

	public void disableDeleteEntriesButton() {
		deleteEntriesButton.setToolTipText(Main.getMessage(DELETE_ENTRIES)
				.replaceAll("%N", "")); //$NON-NLS-1$,  //$NON-NLS-2$,  
		deleteEntriesButton.setEnabled(false);
	}

	// TODO reverseLookup nach Phonebook verschieben
	private void doReverseLookup() {
		int rows[] = callerTable.getSelectedRows();
		if (rows.length > 0) { // nur f�r markierte Eintr�ge ReverseLookup
			// durchf�hren
			for (int i = 0; i < rows.length; i++) {
				Call call = (Call) callerList.getFilteredCallVector().get(
						rows[i]);
				Person newPerson = ReverseLookup.lookup(call.getPhoneNumber());
				if (newPerson != null) {
					JFritz.getPhonebook().addEntry(newPerson);
					JFritz.getPhonebook().fireTableDataChanged();
					callerList.fireTableDataChanged();
				}
			}
		} else { // F�r alle Eintr�ge ReverseLookup durchf�hren
			JFritz.getJframe().reverseLookup();
		}
	}

	public CallerList getCallerList() {
		return callerList;
	}

	// public JToggleButton getCallByCallButton() {
	// return callByCallFilterButton;
	// }

	public CallerTable getCallerTable() {
		return callerTable;
	}

	public ImageIcon getImageIcon(String filename) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/" + filename))); //$NON-NLS-1$
	}

	public Image getImage(String filename) {
		return Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/" + filename)); //$NON-NLS-1$
	}

	private void handleAction(String command) {
	//	int[] rows = callerTable.getSelectedRows(); 
	//	if(rows.length ==0){
		if (command.equals(FILTER_CALLIN)) {
			syncFilterWithButton(callInFilter, callInFilterButton);
			return;
		}
		if (command.equals(FILTER_CALLINFAILED)) { 
			syncFilterWithButton(callInFailedFilter, callInFailedFilterButton);
			return;
		}
		if (command.equals(FILTER_CALLOUT)) { 
			syncFilterWithButton(callOutFilter, callOutFilterButton);
			return;
		}
		if (command.equals(FILTER_COMMENT)) { 
			syncFilterWithButton(commentFilter, commentFilterButton);
			return;
		}
		if (command.equals(FILTER_ANONYM)) { 
			syncFilterWithButton(anonymFilter, anonymFilterButton);
			return;
		}
		if (command.equals(FILTER_FIXED)) { 
			syncFilterWithButton(fixedFilter, fixedFilterButton);
			return;
		}
		if (command.equals(FILTER_HANDY)) { 
			syncFilterWithButton(handyFilter, handyFilterButton);
			return;
		}

		if (command.equals(FILTER_SEARCH)) {
			if(searchFilterButton.getState()==ThreeStateButton.NOTHING){
				searchFilterTextField.setVisible(false);
				searchLabel.setVisible(false);
				searchFilter.setEnabled(false);
			}
			else {
				searchFilterTextField.setVisible(true);
				searchLabel.setVisible(true);
				callerList.removeFilter(searchFilter);
				searchFilter = new SearchFilter(searchFilterTextField.getText());
				// do nothing if(searchFilterButton.getState()==ThreeStateButton.SELECTED)
				if(searchFilterButton.getState()==ThreeStateButton.INVERTED){
					searchFilter.setInvert(true);
				}
				callerList.addFilter(searchFilter);
			}
			
			return;
		}

		if (command.equals(FILTER_DATE)) { 
			syncFilterWithButton(dateFilter, dateFilterButton);
			if(dateFilterButton.getState()==ThreeStateButton.NOTHING){
				startDateChooser.setVisible(false);
				endDateChooser.setVisible(false);
			}
			else {
				callerList.removeFilter(dateFilter);
				dateFilter = new DateFilter(startDateChooser.getDate(), endDateChooser.getDate());
				startDateChooser.setVisible(true);
				endDateChooser.setVisible(true);
				if(dateFilterButton.getState()==ThreeStateButton.INVERTED){
					dateFilter.setInvert(true);
				}
				callerList.addFilter(dateFilter);
			}
			return;
		}

		if (command.equals("setdatefilter_thisday")) { //$NON-NLS-1$
			callerList.removeFilter(dateFilter);
			dateFilterButton.setSelected(false);
			Date start = Calendar.getInstance().getTime();
			Date end = Calendar.getInstance().getTime();
			setStartOfDay(start);
			setEndOfDay(end);
			dateFilter = new DateFilter(start, end);
			callerList.addFilter(dateFilter);
			startDateChooser.setDate(start);
			endDateChooser.setDate(end);
			startDateChooser.setVisible(true);
			endDateChooser.setVisible(true);
		}
		if (command.equals("setdatefilter_yesterday")) { //$NON-NLS-1$
			callerList.removeFilter(dateFilter);
			dateFilterButton.setSelected(false);
			Calendar cal = Calendar.getInstance();
			Date start = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
			Date end = cal.getTime();
			setStartOfDay(start);
			setEndOfDay(end);
			dateFilter = new DateFilter(start, end);
			callerList.addFilter(dateFilter);
			startDateChooser.setDate(start);
			endDateChooser.setDate(end);
			startDateChooser.setVisible(true);
			endDateChooser.setVisible(true);
		}
		if (command.equals("setdatefilter_thismonth")) { //$NON-NLS-1$
			callerList.removeFilter(dateFilter);
			dateFilterButton.setSelected(false);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date start = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date end = cal.getTime();
			setStartOfDay(start);
			setEndOfDay(end);
			dateFilter = new DateFilter(start, end);
			//callerList.addFilter(dateFilter);
			startDateChooser.setDate(start);
			endDateChooser.setDate(end);
			startDateChooser.setVisible(true);
			endDateChooser.setVisible(true);
		}
		if (command.equals("setdatefilter_lastmonth")) { //$NON-NLS-1$
			callerList.removeFilter(dateFilter);
			dateFilterButton.setSelected(false);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1); // last
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date start = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date end = cal.getTime();
			setStartOfDay(start);
			setEndOfDay(end);
			dateFilter = new DateFilter(start, end);
			callerList.addFilter(dateFilter);
			startDateChooser.setDate(start);
			endDateChooser.setDate(end);
			startDateChooser.setVisible(true);
			endDateChooser.setVisible(true);
		}
		if (command.equals("filter_callinfailed_allWithoutComment")) { //$NON-NLS-1$
			clearAllFilter();
			callInFailedFilterButton.setState(ThreeStateButton.SELECTED);
			commentFilterButton.setState(ThreeStateButton.INVERTED);
			syncAllFilters();
			return;
		}
		if (command.equals("filter_callinfailed_allWithoutCommentLastWeek")) { //$NON-NLS-1$
			clearAllFilter();
			callInFailedFilterButton.setState(ThreeStateButton.SELECTED);
			commentFilterButton.setState(ThreeStateButton.INVERTED);
			// dateFilter stuff for last week
			callerList.removeFilter(dateFilter);
			Calendar cal = Calendar.getInstance();
			Date start = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 7);
			Date end = cal.getTime();
			setStartOfDay(start);
			setEndOfDay(end);
			dateFilter = new DateFilter(end, start);
			callerList.addFilter(dateFilter);
			startDateChooser.setDate(end);
			endDateChooser.setDate(start);
			startDateChooser.setVisible(true);
			endDateChooser.setVisible(true);
			syncAllFilters();
			return;
		}
		if (command.equals(FILTER_SIP)) { 
			syncFilterWithButton(sipFilter, sipFilterButton);
			return;
		}
		if (command.equals(FILTER_CALLBYCALL)) { 
			syncFilterWithButton(callByCallFilter, callByCallFilterButton);
			return;
		}
		if (command.equals("clearFilter")) { //$NON-NLS-1$
			clearAllFilter();
			return;
		}
		if (command.equals(DELETE_ENTRY)) { 
			callerList.removeEntries();
			return;
		}
		if (command.equals("reverselookup")) { //$NON-NLS-1$
			doReverseLookup();
			return;
		}
		if (command.equals("export_csv")) { //$NON-NLS-1$
			JFritz.getJframe().exportCallerListToCSV();
			return;
		}
		if (command.equals("export_xml")) { //$NON-NLS-1$
			JFritz.getJframe().exportCallerListToXML();
			return;
		}
		if (command.equals("import_callerlist_csv")) { //$NON-NLS-1$
			JFritz.getJframe().importCallerlistCSV();
			return;
		}
		if (command.equals("clipboard_number")) { //$NON-NLS-1$
			Call call = callerList.getSelectedCall();
			if (call != null) {
				PhoneNumber number = call.getPhoneNumber();
				if ((number != null) && (call != null)) {
					JFritzClipboard.copy(number.convertToNationalNumber());
				}
			}
			// JFritz.getJframe().copyNumberToClipboard();
			return;
		}
		if (command.equals("clipboard_adress")) { //$NON-NLS-1$
			Call call = callerList.getSelectedCall();
			if (call != null) {
				Person person = call.getPerson();
				if (person != null) {
					JFritzClipboard.copy(person.getAddress());
				}
			}
			// JFritz.getJframe().copyAddressToClipboard();

		}
		/*
		if (command.equals("apply_filter")) {
			// TODO checken, ob sich der search filter ge�ndert hat
			if (!(dateFilterButton.getState() == ThreeStateButton.SELECTED)) {
				callerList.removeFilter(dateFilter);
				dateFilter = new DateFilter(setStartOfDay(startDateChooser
						.getDate()), setEndOfDay(endDateChooser.getDate()));
				callerList.addFilter(dateFilter);
			}
			if (!(searchFilterButton.getState() == ThreeStateButton.SELECTED)) {
				callerList.removeFilter(searchFilter);
				String str = searchFilterTextField.getText();
				Debug.msg(str);
				if (str.equals("")) {
					// add no filter
				} else {
					searchFilter = new SearchFilter(str);
					callerList.addFilter(searchFilter);
				}
			}
		}
*/
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			handleAction(FILTER_SEARCH);
			callerList.update();
			return;

		}

	}

	public void keyReleased(KeyEvent arg0) {
		// unn�tig

	}

	public void keyTyped(KeyEvent arg0) {
		// unn�tig

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 *      invoced from the JDateChoose components
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		handleAction(FILTER_DATE);
		callerList.update();
	}

	private void readButtonStatus() {

/*		
		callInFilterButton.setState(ThreeStateButton.NOTHING);
		callOutFilterButton.setState(ThreeStateButton.NOTHING);
		callInFailedFilterButton.setState(ThreeStateButton.NOTHING);
		anonymFilterButton.setState(ThreeStateButton.NOTHING);
		fixedFilterButton.setState(ThreeStateButton.NOTHING);
		handyFilterButton.setState(ThreeStateButton.NOTHING);
		dateFilterButton.setState(ThreeStateButton.NOTHING);
		searchFilterButton.setState(ThreeStateButton.NOTHING);
		sipFilterButton.setState(ThreeStateButton.NOTHING);
		callByCallFilterButton.setState(ThreeStateButton.NOTHING);
		commentFilterButton.setState(ThreeStateButton.NOTHING);
*/	
		// no more filters now set the other stuff
		searchFilterTextField.setVisible(false);
		searchLabel.setVisible(false);
		startDateChooser.setVisible(false);
		endDateChooser.setVisible(false);

		Debug.msg("reading Buttons");
		int state;
		state = Integer.parseInt(Main.getProperty(FILTER_COMMENT, "0"));
		commentFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_DATE, "0"));
		dateFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_SIP, "0"));
		sipFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_CALLBYCALL, "0"));
		callByCallFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_CALLOUT, "0"));
		callOutFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_ANONYM, "0"));
		anonymFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_FIXED, "0"));
		fixedFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_HANDY, "0"));
		handyFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_CALLIN, "0"));
		callInFilterButton.setState(state);
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_CALLINFAILED, "0"));
		callInFailedFilterButton.setState(state);
		searchFilterTextField.setText(Main.getProperty(FILTER_SEARCH_TEXT, ""));
		state = JFritzUtils.parseInt(Main.getProperty(FILTER_SEARCH, "0"));
		searchFilterButton.setState(state);

		DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
		Date start = new Date();
		Date end = new Date();
		try {
			start = df.parse(Main.getProperty(FILTER_DATE_START,
			"11.11.11 11:11"));
			end = df.parse(Main.getProperty(FILTER_DATE_END, "11.11.11 11:11"));
			startDateChooser.setDate(start);
			endDateChooser.setDate(end);
		} catch (ParseException e) {
			startDateChooser.setDate(Calendar.getInstance().getTime());
			endDateChooser.setDate(Calendar.getInstance().getTime());
			Debug
			.err("error parsing date while loading dates from main properties "
					+ e.toString());
		}

		syncAllFilters();
		callerList.update();
	}

	private void syncAllFilters() {
		syncFilterWithButton(callInFilter, callInFilterButton);
		syncFilterWithButton(callInFailedFilter, callInFailedFilterButton);
		syncFilterWithButton(callOutFilter, callOutFilterButton);
		syncFilterWithButton(commentFilter, commentFilterButton);
		syncFilterWithButton(anonymFilter, anonymFilterButton);
		syncFilterWithButton(fixedFilter, fixedFilterButton);
		syncFilterWithButton(handyFilter, handyFilterButton);
		syncFilterWithButton(searchFilter, searchFilterButton);
		syncFilterWithButton(dateFilter, dateFilterButton);
		syncFilterWithButton(sipFilter, sipFilterButton);
		syncFilterWithButton(callByCallFilter, callByCallFilterButton);
		

	}

	public void setCallerList(CallerList callerList) {
		this.callerList = callerList;
	}

	public void setDateFilterText() {
		/*
		 * if (JFritzUtils.parseBoolean(JFritz.getProperty("filter.date"))) {
		 * //$NON-NLS-1$ dateFilterButton.setState(state);
		 * 
		 * if (JFritz.getProperty("filter.date_from").equals( //$NON-NLS-1$
		 * JFritz.getProperty("filter.date_to"))) { //$NON-NLS-1$
		 * dateFilterButton .setText(JFritz.getProperty("filter.date_from"));
		 * //$NON-NLS-1$ } else {
		 * dateFilterButton.setText(JFritz.getProperty("filter.date_from")
		 * //$NON-NLS-1$ + " - " + JFritz.getProperty("filter.date_to"));
		 * //$NON-NLS-1$, //$NON-NLS-2$ } } else {
		 * dateFilterButton.setState(ThreeStateButton.SELECTED); dateFilterButton.setText("");
		 * //$NON-NLS-1$ }
		 */
	}

	public void setDeleteEntriesButton(int rows) {
		deleteEntriesButton.setToolTipText(Main.getMessage(DELETE_ENTRIES)
				.replaceAll("%N", Integer.toString(rows))); //$NON-NLS-1$,  
		deleteEntriesButton.setEnabled(true);
	}

	public void setDeleteEntryButton() {
		deleteEntriesButton.setToolTipText(Main.getMessage(DELETE_ENTRY)); 
		deleteEntriesButton.setEnabled(true);
	}

	public void setDeleteListButton() {
		deleteEntriesButton.setToolTipText(Main.getMessage("delete_list")); //$NON-NLS-1$
		// clearList-Icon to big, so use std. delete.png
		// deleteEntriesButton.setIcon(getImage("clearList.png"));
		deleteEntriesButton.setEnabled(true);
	}

	//todo fix sync methods
	private void syncFilterWithButton(CallFilter filter, ThreeStateButton button) {
		if (button.getState() == ThreeStateButton.SELECTED) {
			filter.setEnabled(true);
			filter.setInvert(false);
			Debug.msg("sel");
		}
		if (button.getState() == ThreeStateButton.INVERTED) {
			filter.setEnabled(true);
			filter.setInvert(true);
			Debug.msg("sel not");
		}
		if (button.getState() == ThreeStateButton.NOTHING) {
			filter.setEnabled(false);
			Debug.msg("nothing");
		}
	}

	

	public void writeButtonStatus() {
		Debug.msg("writing Buttons");
		Main.setProperty(FILTER_SEARCH_TEXT, searchFilterTextField.getText());
		Main.setProperty(FILTER_SEARCH, ""+searchFilterButton.getState());
		Main.setProperty(FILTER_COMMENT, ""+commentFilterButton.getState());
		Main.setProperty(FILTER_DATE, ""+dateFilterButton.getState());
		DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
		Date start = startDateChooser.getDate();
		Date end = endDateChooser.getDate();

		Main.setProperty(FILTER_DATE_START, df.format(start));
		Main.setProperty(FILTER_DATE_END, df.format(end));

		Main.setProperty(FILTER_SIP, ""+sipFilterButton.getState());
		Main
		.setProperty(FILTER_CALLBYCALL, ""+callByCallFilterButton
				.getState());
		Main.setProperty(FILTER_CALLOUT, ""+callOutFilterButton.getState());
		Main.setProperty(FILTER_ANONYM, ""+anonymFilterButton.getState());
		Main.setProperty(FILTER_FIXED, ""+fixedFilterButton.getState());
		Main.setProperty(FILTER_HANDY, ""+handyFilterButton.getState());
		Main.setProperty(FILTER_CALLIN, ""+callInFilterButton.getState());
		Main.setProperty(FILTER_CALLINFAILED, ""+callInFailedFilterButton
				.getState());
	}

	private Date setStartOfDay(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	private Date setEndOfDay(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}
}
