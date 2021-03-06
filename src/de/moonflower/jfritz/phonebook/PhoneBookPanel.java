/*
 * Created on 05.06.2005
 *
 */
package de.moonflower.jfritz.phonebook;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.JFritzWindow;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.VCardList;
import de.moonflower.jfritz.utils.JFritzUtils;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.StatusBarController;

/**
 *  
 */
public class PhoneBookPanel extends JPanel implements ListSelectionListener,
		ActionListener, KeyListener {
	class PopupListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				int loc = splitPane.getDividerLocation();
				if (loc < PERSONPANEL_WIDTH) {
					splitPane.setDividerLocation(PERSONPANEL_WIDTH);
				} else {
					splitPane.setDividerLocation(0);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.show(e
						.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private static final long serialVersionUID = 1;

    private final int PERSONPANEL_WIDTH = 350;

	private PhoneBookTable phoneBookTable;

	private PersonPanel personPanel;

	private JSplitPane splitPane;

	
	private JPopupMenu popupMenu;
	private JFritzWindow parentFrame;
	public JButton resetButton;
	public JTextField searchFilter;
	public JToggleButton privateFilter;
	
	private PhoneBook phonebook;
	private StatusBarController statusBarController = new StatusBarController(); 

	public PhoneBookPanel(PhoneBook phonebook, JFritzWindow parentFrame) {
		this.phonebook = phonebook;
		this.parentFrame = parentFrame;
		setLayout(new BorderLayout());

		JPanel editPanel = createEditPanel();
		
		add(createPhoneBookToolBar(), BorderLayout.NORTH);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(editPanel);
		splitPane.setRightComponent(createPhoneBookTable());

		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		add(splitPane, BorderLayout.CENTER);
		if (phonebook.getUnfilteredPersons().size() == 0)
		{
			splitPane.setDividerLocation(0);
		}
		else
		{
			splitPane.setDividerLocation(PERSONPANEL_WIDTH);
		}
	}

	/**
     * Added the code from haeusler
     * DATE: 04.02.06, added by Brian
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("addPerson")) { //$NON-NLS-1$
			Person newPerson = new Person("", ""); //$NON-NLS-1$,  //$NON-NLS-2$
			Vector<Person> persons = new Vector<Person>();
			persons.add(newPerson);
			phonebook.addFilterException(newPerson);
			phonebook.addEntries(persons);
			phonebook.fireTableDataChanged();
			int index = phonebook.indexOf(newPerson); 
			phoneBookTable.getSelectionModel().setSelectionInterval(index, index);
			int loc = splitPane.getDividerLocation();
			if (loc < PERSONPANEL_WIDTH) {
				splitPane.setDividerLocation(PERSONPANEL_WIDTH);
			}
			personPanel.focusFirstName();
		} else if (e.getActionCommand().equals("deletePerson")) { //$NON-NLS-1$
			removeSelectedPersons();
		} else if (e.getActionCommand().equals("editPerson")) { //$NON-NLS-1$
			// Edit Panel anzeigen, falls verborgen
			int loc = splitPane.getDividerLocation();
			if (loc < PERSONPANEL_WIDTH) {
				splitPane.setDividerLocation(PERSONPANEL_WIDTH);
			}
			personPanel.focusFirstName();
			;
		} else if (e.getActionCommand().equals("filter_private")) { //$NON-NLS-1$
			setPrivateFilter(privateFilter.isSelected());
			phonebook.updateFilter();
		} else if (e.getActionCommand().equals("export_vcard")) { //$NON-NLS-1$
			exportVCard();
		} else if (e.getActionCommand().equals("import_xml")) { //$NON-NLS-1$
			importFromXML ();
		} else if (e.getActionCommand().equals("clearFilter")) { //$NON-NLS-1$
			clearAllFilter();
		} else {
			Debug.warning("Unsupported Command: " + e.getActionCommand()); //$NON-NLS-1$
		}
	}

	public JScrollPane createPhoneBookTable() {
		popupMenu = new JPopupMenu();
		JMenuItem menuItem;
		menuItem = new JMenuItem(Main.getMessage("phonebook_delPerson")); //$NON-NLS-1$
		menuItem.setActionCommand("deletePerson"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(Main.getMessage("phonebook_editPerson")); //$NON-NLS-1$
		menuItem.setActionCommand("editPerson"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(Main.getMessage("phonebook_vcardExport")); //$NON-NLS-1$
		menuItem.setActionCommand("export_vcard"); //$NON-NLS-1$
		menuItem.addActionListener(this);
		popupMenu.add(menuItem);

		//Add listener to components that can bring up popup menus.
		MouseAdapter popupListener = new PopupListener();

		phoneBookTable = new PhoneBookTable(this, phonebook);
		phoneBookTable.getSelectionModel().addListSelectionListener(this);
		phoneBookTable.addMouseListener(popupListener);
		return new JScrollPane(phoneBookTable);
	}

	public JToolBar createPhoneBookToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(true);
		JButton addButton = new JButton(Main.getMessage("new_entry")); //$NON-NLS-1$
		addButton.setIcon(getImage("add.png")); //$NON-NLS-1$
		addButton.setActionCommand("addPerson"); //$NON-NLS-1$
		addButton.addActionListener(this);
		toolBar.add(addButton);

		JButton delButton = new JButton(Main.getMessage("delete_entry")); //$NON-NLS-1$
		delButton.setToolTipText(Main.getMessage("delete_entry")); //$NON-NLS-1$
		delButton.setIcon(getImage("delete.png")); //$NON-NLS-1$
		delButton.setActionCommand("deletePerson"); //$NON-NLS-1$
		delButton.addActionListener(this);
		toolBar.add(delButton);

		toolBar.addSeparator();

		JButton exportVCardButton = new JButton();
		exportVCardButton.setIcon(getImage("vcard.png")); //$NON-NLS-1$
		exportVCardButton.setToolTipText(Main.getMessage("export_vcard")); //$NON-NLS-1$
		exportVCardButton.setActionCommand("export_vcard"); //$NON-NLS-1$
		exportVCardButton.addActionListener(this);
		toolBar.add(exportVCardButton);

		toolBar.addSeparator();
		//toolBar.addSeparator();
		//toolBar.addSeparator();

		privateFilter = new JToggleButton(getImage("addbook_grey.png"), true); //$NON-NLS-1$
		privateFilter.setSelectedIcon(getImage("addbook.png")); //$NON-NLS-1$
		privateFilter.setActionCommand("filter_private"); //$NON-NLS-1$
		privateFilter.addActionListener(this);
		privateFilter.setToolTipText(Main.getMessage("private_entry")); //$NON-NLS-1$
		privateFilter.setSelected(JFritzUtils.parseBoolean(Main.getStateProperty(
				"filter_private"))); //$NON-NLS-1$,  //$NON-NLS-2$
		toolBar.add(privateFilter);

		//toolBar.addSeparator();
		toolBar.addSeparator();
		
		JButton importXMLButton = new JButton();
		importXMLButton.setIcon(getImage("import.gif")); //$NON-NLS-1$
		importXMLButton.setToolTipText(Main.getMessage("phonebook_import")); //$NON-NLS-1$
		importXMLButton.setActionCommand("import_xml"); //$NON-NLS-1$
		importXMLButton.addActionListener(this);
		toolBar.add(importXMLButton);
		
		toolBar.addSeparator();

		resetButton = new JButton();
		toolBar.add(new JLabel(Main.getMessage("search") + ": ")); //$NON-NLS-1$, //$NON-NLS-2$
		searchFilter = new JTextField(Main.getStateProperty("filter.Phonebook.search"), //$NON-NLS-1$,  //$NON-NLS-2$
				10);
		searchFilter.addKeyListener(this);
		toolBar.add(searchFilter);
		
		resetButton = new JButton(Main.getMessage("clear")); //$NON-NLS-1$
		resetButton.setActionCommand("clearFilter"); //$NON-NLS-1$
		resetButton.addActionListener(this);
		toolBar.add(resetButton);

		return toolBar;
	}

	/**
	 * Exports VCard or VCardList
	 */
	public void exportVCard() {
		VCardList list = new VCardList();
		JFileChooser fc = new JFileChooser(Main.getStateProperty("options.exportVCARDpath")); //$NON-NLS-1$
		fc.setDialogTitle(Main.getMessage("export_vcard")); //$NON-NLS-1$
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".vcf"); //$NON-NLS-1$
			}

			public String getDescription() {
				return "VCard (.vcf)"; //$NON-NLS-1$
			}
		});
		int rows[] = getPhoneBookTable().getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			Person person = phonebook.getPersonAt(rows[i]);
			if ((person != null) && !person.getFullname().equals("")) { //$NON-NLS-1$
				list.addVCard(person);
			}
		}
		if (list.getCount() > 0) {
			if (list.getCount() == 1) {
				fc.setSelectedFile(new File(list.getPerson(0)
						.getStandardTelephoneNumber()
						+ ".vcf")); //$NON-NLS-1$
			} else if (list.getCount() > 1) {
				fc.setSelectedFile(new File("jfritz.vcf")); //$NON-NLS-1$
			}
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			    String path = fc.getSelectedFile().getPath();
			    path = path.substring(0,path.length()-fc.getSelectedFile().getName().length());
			    Main.setStateProperty("options.exportVCARDpath", path);  //$NON-NLS-1$
				File file = fc.getSelectedFile();
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(this, 
							Main.getMessage("overwrite_file").replaceAll("%F", file.getName()),  //$NON-NLS-1$,  //$NON-NLS-2$
							Main.getMessage("dialog_title_overwrite_file"),  //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
						list.saveToFile(file);
					}
				} else {
					list.saveToFile(file);
				}
			}
		} else {
		Debug.errDlg(Main.getMessage("error_no_row_chosen"));  //$NON-NLS-1$
		}
	}

	public ImageIcon getImage(String filename) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(
						"/de/moonflower/jfritz/resources/images/" + filename))); //$NON-NLS-1$
	}

	/**
	 * @return Returns the personPanel.
	 */
	public final PersonPanel getPersonPanel() {
		return personPanel;
	}

	/**
	 * @return Returns the phoneBookTable.
	 */
	public final PhoneBookTable getPhoneBookTable() {
		return phoneBookTable;
	}

	public void importFromXML () {
		JFileChooser fc = new JFileChooser(Main.getStateProperty("option.phonebook.import_xml_path"));  //$NON-NLS-1$
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".xml");  //$NON-NLS-1$
			}

			public String getDescription() {
				return Main.getMessage("xml_files");  //$NON-NLS-1$
			}
		});
		if (fc.showOpenDialog(parentFrame) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Main.setStateProperty("option.phonebook.import_xml_path", fc.getSelectedFile().getAbsolutePath());  //$NON-NLS-1$
		phonebook.loadFromXMLFile(fc.getSelectedFile().getAbsolutePath());
		phonebook.saveToXMLFile(Main.SAVE_DIR + JFritz.PHONEBOOK_FILE);
	}

	/**
	 *	added code form haeusler
	 *  DATE: 04.02.06 Brian
	 *	
	 */
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			JTextField search = (JTextField) arg0.getSource();
			
			// ignore whitespaces at the beginning and the end
			String filter = search.getText().trim();
			
			// only update filter when the search expression has changed
			if (! filter.equals(Main.getStateProperty("filter.Phonebook.search"))) {  //$NON-NLS-1$,  //$NON-NLS-2$
				Main.setStateProperty("filter.Phonebook.search", filter);  //$NON-NLS-1$
				phonebook.clearFilterExceptions();
				phonebook.updateFilter();
				phonebook.fireTableDataChanged();
			}
		}

	}

	public void keyReleased(KeyEvent arg0) {
		// unn�tig
		
	}

	public void keyTyped(KeyEvent arg0) {
		// unn�tig
		
	}

	/**
	 * Removes selected persons from phonebook
	 *  
	 */
	public void removeSelectedPersons() {
		if (getPhoneBookTable().getSelectedRowCount() > 0)
		{
			String message; 
			if (getPhoneBookTable().getSelectedRowCount() == 1)
			{
				message = Main.getMessage("delete_entry");
			}
			else
			{
				message = Main.getMessage("delete_entries").replaceAll("%N", Integer.toString(getPhoneBookTable().getSelectedRowCount()));
			}
			if (JOptionPane.showConfirmDialog(this,
					message, //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
					Main.PROGRAM_NAME, 
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
	
				personPanel.cancelEditing();
				phonebook.removePersons(getPhoneBookTable().getSelectedRows());
				phoneBookTable.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}
	public void setSearchFilter(String text) {
		searchFilter.setText(text);
	}
	
	private void setPrivateFilter(boolean enabled)
	{
		Main.setStateProperty("filter_private", Boolean //$NON-NLS-1$
				.toString(enabled));
		privateFilter.setSelected(enabled);
	}
	
	public void setStatus() {
		PhoneBook pb = (PhoneBook) phoneBookTable.getModel();
		int entries = pb.getFilteredPersons().size();
		statusBarController.fireStatusChanged(Main.getMessage("entries").  //$NON-NLS-1$
				replaceAll("%N", Integer.toString(entries)));  //$NON-NLS-1$
	}
	
	public void showPersonPanel() {
		splitPane.setDividerLocation(PERSONPANEL_WIDTH);
	}

	/**
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			int rows[] = phoneBookTable.getSelectedRows();
			if (rows.length == 1) {
				Person p = ((PhoneBook) phoneBookTable.getModel())
						.getPersonAt(rows[0]);
				personPanel.cancelEditing();
				if (p != null)
				{
					personPanel.setPerson(p, false);
				}
				setStatus();
			}
			else {
				statusBarController.fireStatusChanged(Main.getMessage("phonebook_chosenEntries")  //$NON-NLS-1$
						.replaceAll("%N", Integer.toString(rows.length))); //$NON-NLS-1$,  
			}
		}
	}

	private void clearAllFilter() {
		setSearchFilter("");  //$NON-NLS-1$
		Main.setStateProperty("filter.Phonebook.search", "");  //$NON-NLS-1$,   //$NON-NLS-2$
		setPrivateFilter(false);
		phonebook.clearFilterExceptions();
		phonebook.updateFilter();
		phonebook.fireTableDataChanged();
	}

	/**
	 * @return editPanel
	 */
	private JPanel createEditPanel() {
		JPanel editPanel = new JPanel(new BorderLayout());

		personPanel = new PersonPanel(new Person(), phonebook, parentFrame);		
		editPanel.add(personPanel, BorderLayout.CENTER);
		return editPanel;
	}

	public StatusBarController getStatusBarController() {
		return statusBarController;
	}

	public void setStatusBarController(StatusBarController statusBarController) {
		this.statusBarController = statusBarController;
	}
}
