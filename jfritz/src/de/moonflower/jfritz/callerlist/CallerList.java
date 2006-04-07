/*
 * $Id$
 * 
 * Created on 08.04.2005
 *
 */
package de.moonflower.jfritz.callerlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.CallType;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.CopyFile;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * This class manages the caller list.
 * 
 * @author Arno Willig
 */
public class CallerList extends AbstractTableModel {
    private static final long serialVersionUID = 1;

    private static final String CALLS_DTD_URI = "http://jfritz.moonflower.de/dtd/calls.dtd";

    private static final String CALLS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<!-- DTD for JFritz calls -->"
            + "<!ELEMENT calls (comment?,entry*)>"
            + "<!ELEMENT comment (#PCDATA)>"
            + "<!ELEMENT date (#PCDATA)>"
            + "<!ELEMENT caller (#PCDATA)>"
            + "<!ELEMENT port (#PCDATA)>"
            + "<!ELEMENT route (#PCDATA)>"
            + "<!ELEMENT duration (#PCDATA)>"
            + "<!ELEMENT comment (#PCDATA)>"
            + "<!ELEMENT entry (date,caller?,port?,route?,duration?,comment?)>"
            + "<!ATTLIST entry calltype (call_in|call_in_failed|call_out) #REQUIRED>";
    
    private final static String PATTERN_CSV = "(\\||;)";
    
    private final static String EXPORT_CSV_FORMAT_JFRITZ = "\"CallType\";\"Date\";\"Time\";\"Number\";\"Route\";\"" +
        "Port\";\"Duration\";\"Name\";\"Address\";\"City\";\"CallByCall\";\"Comment\"";

    private final static String EXPORT_CSV_FORMAT_FRITZBOX = "Typ;Datum;Rufnummer;Nebenstelle;Eigene Rufnummer;Dauer";
    
    //Is the type eyported from a 7170
    private final static String EXPORT_CSV_FORMAT_FRITZBOX_NEWFIRMWARE = "Typ; Datum; Rufnummer; Nebenstelle; Eigene Rufnummer; Dauer";
    
    private JFritz jfritz;

    private Vector filteredCallerData;

    private Vector unfilteredCallerData;

    private Vector alreadyKnownCalls;

    private int sortColumn;

    private boolean sortDirection = false;

    /**
     * CallerList Constructor
     * 
     * @param jfritz
     */
    public CallerList(JFritz jfritz) {
        filteredCallerData = new Vector();
        unfilteredCallerData = new Vector();
        this.jfritz = jfritz;
        alreadyKnownCalls = new Vector();
        sortColumn = 1;
    }

    /**
     * 
     * @return Unfiltered Vector of Calls
     */
    public Vector getUnfilteredCallVector() {
        return unfilteredCallerData;
    }

    /**
     * 
     * @return Filtered Vector of Calls
     */
    public Vector getFilteredCallVector() {
        return filteredCallerData;
    }

    /**
     * 
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String columnName = getRealColumnName(columnIndex);
        if (columnName.equals("participant")) {
            return ((Call) filteredCallerData.get(rowIndex)).getPhoneNumber() != null;
        } else if (columnName.equals("comment")) {
            return true;
        } else if (columnName.equals("number")) {
        	return true;
        } 
        return false;
    }

    /**
     * @param columnIndex
     * @return class of column
     */
    public Class getColumnClass(int columnIndex) {
        Object o = getValueAt(0, columnIndex);
        if (o == null) {
            return Object.class;
        } else {
            return o.getClass();
        }

    }

    /**
     * Saves caller list to xml file.
     * 
     * @param filename
     *            Filename to save to
     * @param wholeCallerList
     *            Save whole caller list or only selected entries
     */
    public void saveToXMLFile(String filename, boolean wholeCallerList) {
        Debug.msg("Saving to file " + filename);
        try {
            BufferedWriter pw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "UTF8"));
            pw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.newLine();
//            pw.write("<!DOCTYPE calls SYSTEM \"" + CALLS_DTD_URI + "\">");
//            pw.newLine();
            pw.write("<calls>");
            pw.newLine();
            pw.write("<comment>Calls for " + JFritz.PROGRAM_NAME + " v"
                    + JFritz.PROGRAM_VERSION + "</comment>");
            pw.newLine();

            int rows[] = null;
            if (jfritz != null && jfritz.getJframe() != null) {
                rows = jfritz.getJframe().getCallerTable().getSelectedRows();
            }
            if (!wholeCallerList && rows != null && rows.length > 0) {
                for (int i = 0; i < rows.length; i++) {
                    Call currentCall = (Call) filteredCallerData
                            .elementAt(rows[i]);
                    pw.write(currentCall.toXML());
                    pw.newLine();
                }
            } else if (wholeCallerList) { // Export ALL UNFILTERED Calls
                Enumeration en = unfilteredCallerData.elements();
                while (en.hasMoreElements()) {
                    Call call = (Call) en.nextElement();
                    pw.write(call.toXML());
                    pw.newLine();
                }
            } else {// Export ALL FILTERED Calls
                Enumeration en = filteredCallerData.elements();
                while (en.hasMoreElements()) {
                    Call call = (Call) en.nextElement();
                    pw.write(call.toXML());
                    pw.newLine();
                }
            }
            pw.write("</calls>");

            pw.close();
        } catch (UnsupportedEncodingException e) {
            Debug.err("UTF-8 wird nicht unterst?tzt.");
        } catch (FileNotFoundException e) {
            Debug.err("Could not write " + filename + "!");
        } catch (IOException e) {
            Debug.err("IOException " + filename);
        }
    }

    /**
     * Saves callerlist to csv file
     * 
     * @param filename
     *            Filename to save to
     * @param wholeCallerList
     *            Save whole caller list or only selected entries
     */
    public void saveToCSVFile(String filename, boolean wholeCallerList) {
        Debug.msg("Saving to csv file " + filename);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filename);
            PrintWriter pw = new PrintWriter(fos);
            pw
                    .println("\"CallType\";\"Date\";\"Time\";\"Number\";\"Route\";\"Port\";\"Duration\";\"Name\";\"Address\";\"City\";\"CallByCall\";\"Comment\"");
            int rows[] = null;
            if (jfritz != null && jfritz.getJframe() != null) {
                rows = jfritz.getJframe().getCallerTable().getSelectedRows();
            }
            if (!wholeCallerList && rows != null && rows.length > 0) {
                for (int i = 0; i < rows.length; i++) {
                    Call currentCall = (Call) filteredCallerData
                            .elementAt(rows[i]);
                    pw.println(currentCall.toCSV());
                }
            } else if (wholeCallerList) { // Export ALL UNFILTERED Calls
                Enumeration en = getUnfilteredCallVector().elements();
                while (en.hasMoreElements()) {
                    Call call = (Call) en.nextElement();
                    pw.println(call.toCSV());
                }
            } else { // Export ALL FILTERED Calls
                Enumeration en = getFilteredCallVector().elements();
                while (en.hasMoreElements()) {
                    Call call = (Call) en.nextElement();
                    pw.println(call.toCSV());
                }
            }
            pw.close();
        } catch (FileNotFoundException e) {
            Debug.err("Could not write " + filename + "!");
        }
    }

   
    /**
     * Loads calls from xml file
     * 
     * @param filename
     */
    public void loadFromXMLFile(String filename) {
        try {

            // Workaround for SAX parser
            // File dtd = new File("calls.dtd");
            // dtd.deleteOnExit();
            // if (!dtd.exists()) dtd.createNewFile();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false); // FIXME Something wrong with the
                                            // DTD
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            reader.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException x) throws SAXException {
                    // Debug.err(x.toString());
                    throw x;
                }

                public void fatalError(SAXParseException x) throws SAXException {
                    // Debug.err(x.toString());
                    throw x;
                }

                public void warning(SAXParseException x) throws SAXException {
                    // Debug.err(x.toString());
                    throw x;
                }
            });
            reader.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId,
                        String systemId) throws SAXException, IOException {
                    if (systemId.equals(CALLS_DTD_URI)
                            || systemId.equals("calls.dtd")) {
                        InputSource is;
                        is = new InputSource(new StringReader(CALLS_DTD));
                        is.setSystemId(CALLS_DTD_URI);
                        return is;
                    }
                    throw new SAXException("Invalid system identifier: "
                            + systemId);
                }

            });
            reader.setContentHandler(new CallFileXMLHandler(this));
            reader.parse(new InputSource(new FileInputStream(filename)));
            sortAllUnfilteredRows();

        } catch (ParserConfigurationException e) {
            Debug.err("Error with ParserConfiguration!");
        } catch (SAXException e) {
            Debug.err("Error on parsing " + filename + "!");
            if (e.getLocalizedMessage().startsWith("Relative URI")
                    || e.getLocalizedMessage().startsWith(
                            "Invalid system identifier")) {
                Debug.err(e.getLocalizedMessage());
                Debug
                        .errDlg("STRUKTUR�NDERUNG!\n\nBitte in der Datei jfritz.calls.xml\n "
                                + "die Zeichenkette \"calls.dtd\" durch\n \""
                                + CALLS_DTD_URI + "\"\n ersetzen!");
                System.exit(0);
            }
        } catch (IOException e) {
            Debug.err("Could not read " + filename + "!");
        }
    }

    /**
     * Removes all duplicate whitespaces from inputStr
     * 
     * @param inputStr
     * @return outputStr
     */
    public static String removeDuplicateWhitespace(String inputStr) {
        Pattern p = Pattern.compile("\\s+");
        Matcher matcher = p.matcher(inputStr);
        return matcher.replaceAll(" ");
    }

    /**
     * Adds new Call to CallerList
     * 
     * @param symbol
     * @param datum
     * @param number
     * @param port
     * @param route
     * @param duration
     */
    public boolean addEntry(CallType symbol, Date datum, PhoneNumber number,
            String port, String route, int duration, String comment) {
        Call call = new Call(jfritz, symbol, datum, number, port, route,
                duration);
        call.setComment(comment);
        return addEntry(call);
    }

    /**
     * adds new Call to CallerList
     * 
     * @param call
     * @return true if call was added successfully
     */

    public boolean addEntry(Call call) {
        boolean newEntry = true;
        Enumeration en = alreadyKnownCalls.elements();
        while (en.hasMoreElements()) {
            Call c = (Call) en.nextElement();
            String nr1 = "", nr2 = "";
            if (c.getPhoneNumber() != null)
                nr1 = c.getPhoneNumber().getFullNumber();
            if (call.getPhoneNumber() != null)
                nr2 = call.getPhoneNumber().getFullNumber();
            String route1 = "", route2 = "";
            if (c.getRoute() != null)
                route1 = c.getRoute();
            if (call.getRoute() != null)
                route2 = call.getRoute();
            if (c.getCalldate().equals(call.getCalldate()) && (nr1).equals(nr2)
                    && (c.getPort().equals(call.getPort()))
                    && (c.getDuration() == call.getDuration())
                    && (c.getCalltype().toInt() == call.getCalltype().toInt())
                    && (route1.equals(route2))) {
                newEntry = false; // We already have this call
                alreadyKnownCalls.remove(c);
                break;
            }
        }

        if (newEntry) { // Add new entry to table model
            unfilteredCallerData.add(call);
        }
        return newEntry;
    }

    /**
     * Retrieves data from FRITZ!Box
     *   
     * @throws WrongPasswordException
     * @throws IOException
     */
    public void getNewCalls() throws WrongPasswordException, IOException {
    	getNewCalls(false);
    }
    /**
     * Retrieves data from FRITZ!Box
     * 
     * @param deleteFritzBoxCallerList
     * 				true indicates that fritzbox callerlist should be deleted without considering number of entries or config
     * @throws WrongPasswordException
     * @throws IOException
     */
    public void getNewCalls(boolean deleteFritzBoxCallerList) throws WrongPasswordException, IOException {
        alreadyKnownCalls = (Vector) unfilteredCallerData.clone();
        Debug.msg("box.address: " + JFritz.getProperty("box.address"));
        Debug.msg("box.password: " + JFritz.getProperty("box.password"));
        Debug.msg("box.firmware: " + JFritz.getProperty("box.firmware"));
/**
        JFritzUtils.retrieveCSVList(JFritz
                .getProperty("box.address"), Encryption.decrypt(JFritz
                .getProperty("box.password")), JFritzUtils.detectBoxType(
                        JFritz.getProperty("box.firmware"), JFritz
                                .getProperty("box.address"), Encryption
                                .decrypt(JFritz.getProperty("box.password"))));
        Vector data = new Vector();
**/

        Vector data = JFritzUtils.retrieveCSVList(JFritz
                .getProperty("box.address"), Encryption.decrypt(JFritz
                .getProperty("box.password")), JFritz
                .getProperty("country.prefix"), JFritz
                .getProperty("country.code"),
                JFritz.getProperty("area.prefix"), JFritz
                        .getProperty("area.code"), JFritzUtils.detectBoxType(
                        JFritz.getProperty("box.firmware"), JFritz
                                .getProperty("box.address"), Encryption
                                .decrypt(JFritz.getProperty("box.password"))), jfritz);

        if (data == null) return;
        Debug.msg(data.toString());
        
        int newEntries = 0;
        for (Enumeration el = data.elements(); el.hasMoreElements();) {
            boolean newEntry = addEntry((Call) el.nextElement());
            if (newEntry)
                newEntries++;
        }

        // Notify user?
        if ((JFritz.getProperty("option.notifyOnCalls", "true").equals("true"))
                && (newEntries > 0)) {
            jfritz.getJframe().setVisible(true);
            jfritz.getJframe().toFront();
        }
        if (newEntries > 0) {
			sortAllUnfilteredRows();
			saveToXMLFile(JFritz.CALLS_FILE, true);
            String msg;
            // TODO: I18N
            if (newEntries == 1) {
                msg = "Ein neuer Anruf empfangen!";
            } else {
                msg = newEntries + " neue Anrufe empfangen!";
            }
            JFritz.infoMsg(msg);

        }
        // Clear data on fritz box ?
        // deleteFritzBoxCallerList=true indicates that list should be deleted in any case
        if ((newEntries > 0
                && JFritz.getProperty("option.deleteAfterFetch", "false")
                        .equals("true")) 
           || deleteFritzBoxCallerList) {
            JFritzUtils.clearListOnFritzBox(JFritz.getProperty("box.address"),
                    JFritz.getProperty("box.password"), JFritzUtils
                            .detectBoxType(JFritz.getProperty("box.firmware"),
                                    JFritz.getProperty("box.address"),
                                    Encryption.decrypt(JFritz
                                            .getProperty("box.password"))));
        }
        
        //Make back-up after fetching the caller list?
        if (newEntries > 0
                && JFritzUtils.parseBoolean(JFritz.getProperty("option.createBackupAfterFetch", "false"))) {
            doBackup();
        }
        
    }

    /**
     * returns number of rows in CallerList
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return filteredCallerData.size();
    }

    /**
     * returns number of columns of a call
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        // 9 Columns on the Table
        return 9;
    }

    /**
     * @param rowIndex
     * @param columnIndex
     * @return the value at a specific position
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Call call = (Call) filteredCallerData.get(rowIndex);
        String columnName = getRealColumnName(columnIndex);
        if (columnName.equals("type")) {
            return call.getCalltype();
        } else if (columnName.equals("date")) {
            return call.getCalldate();
        } else if (columnName.equals("callbycall")) {
            if (call.getPhoneNumber() != null)
                return call.getPhoneNumber().getCallByCall();
            else
                return null;
        } else if (columnName.equals("number")) {
            return call.getPhoneNumber();
        } else if (columnName.equals("participant")) {
            return call.getPerson();
        } else if (columnName.equals("port")) {
            return call.getPort();
        } else if (columnName.equals("route")) {
            if (call.getRoute().startsWith("SIP"))
                return jfritz.getSIPProviderTableModel().getSipProvider(
                        call.getRoute(), call.getRoute());
            return call.getRoute();
        } else if (columnName.equals("duration")) {
            return Integer.toString(call.getDuration());
        } else if (columnName.equals("comment")) {
            return call.getComment();
            /**
             * } else if (columnName.equals("Kosten")) { return
             * Double.toString(call.getCost());
             */
        }

        // default: return null
        return null;
    }

    /**
     * Sets a value to a specific position
     */
    public void setValueAt(Object object, int rowIndex, int columnIndex) {
        String columnName = getRealColumnName(columnIndex);
        if (columnName.equals("participant")) {
            setPerson((Person) object, rowIndex);
        } else if (columnName.equals("comment")) {
            setComment((String) object, rowIndex);
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void setComment(String comment, int rowIndex) {
        Call call = (Call) filteredCallerData.get(rowIndex);
        call.setComment(comment);
    }

    public void setPerson(Person person, int rowIndex) {
        Call call = (Call) filteredCallerData.get(rowIndex);

        if (call.getPhoneNumber() != null) { // no empty numbers
            if (person == null) {
                Debug
                        .err("Callerlist.setPerson():  IMPLEMENT ME (remove person)");
            } else {
                if (call.getPerson() == null) {
                    if (!person.isEmpty())
                        jfritz.getPhonebook().addEntry(person);
                } else if (!call.getPerson().equals(person)) {
                    call.getPerson().copyFrom(person);
                }
            }
            fireTableDataChanged();
        }

    }

    /**
     * Sort table model rows by a specific column and direction
     * 
     * @param col
     *            Index of column to be sorted by
     * @param asc
     *            Order of sorting
     */
    public void sortAllFilteredRowsBy(int col, boolean asc) {
        // Debug.msg("Sorting column " + col + " " + asc);
        Collections.sort(filteredCallerData, new ColumnSorter(col, asc));
        fireTableDataChanged();
        fireTableStructureChanged();
    }

    /**
     * Sort table model rows by a specific column. The direction is determined
     * automatically.
     * 
     * @param col
     *            Index of column to be sorted by
     */
    public void sortAllFilteredRowsBy(int col) {
        if ((sortColumn == col) && (sortDirection == false)) {
            sortDirection = true;
        } else {
            sortColumn = col;
            sortDirection = false;
        }
        sortAllFilteredRowsBy(sortColumn, sortDirection);
    }

    public void sortAllUnfilteredRows() {
        Debug.msg("Sorting unfiltered data");
        Collections.sort(unfilteredCallerData, new ColumnSorter(1, false));
        // Resort filtered data
        Collections.sort(filteredCallerData, new ColumnSorter(sortColumn,
                sortDirection));
        updateFilter();
        fireTableStructureChanged();
    }

    /**
     * This comparator is used to sort vectors of data
     */
    public class ColumnSorter implements Comparator {
        int columnIndex;

        boolean ascending;

        ColumnSorter(int columnIndex, boolean ascending) {
            this.columnIndex = columnIndex;
            this.ascending = ascending;
        }

        public int compare(Object a, Object b) {
            Object o1 = null, o2 = null;
            Call v1 = (Call) a;
            Call v2 = (Call) b;
            String columnName = getRealColumnName(columnIndex);

            if (columnName.equals("type")) {
                o1 = v1.getCalltype().toString();
                o2 = v2.getCalltype().toString();
            } else if (columnName.equals("date")) {
                o1 = v1.getCalldate();
                o2 = v2.getCalldate();
            } else if (columnName.equals("callbycall")) {
                if (v1.getPhoneNumber() != null)
                    o1 = v1.getPhoneNumber().getCallByCall();
                else
                    o1 = null;
                if (v2.getPhoneNumber() != null)
                    o2 = v2.getPhoneNumber().getCallByCall();
                else
                    o2 = null;
            } else if (columnName.equals("number")) {
                if (v1.getPhoneNumber() != null)
                    o1 = v1.getPhoneNumber().getIntNumber();
                else
                    o1 = null;
                if (v2.getPhoneNumber() != null)
                    o2 = v2.getPhoneNumber().getIntNumber();
                else
                    o2 = null;
            } else if (columnName.equals("participant")) {
                if (v1.getPerson() != null)
                    o1 = v1.getPerson().getFullname().toUpperCase();
                else
                    o1 = null;
                if (v2.getPerson() != null)
                    o2 = v2.getPerson().getFullname().toUpperCase();
                else
                    o2 = null;
            } else if (columnName.equals("port")) {
                o1 = v1.getPort();
                o2 = v2.getPort();
            } else if (columnName.equals("route")) {
                o1 = v1.getRoute();
                o2 = v2.getRoute();
            } else if (columnName.equals("duration")) {
                if (v1.getDuration() != 0)
                    o1 = format(Integer.toString(v1.getDuration()), 10);
                else
                    o1 = null;
                if (v2.getDuration() != 0)
                    o2 = format(Integer.toString(v2.getDuration()), 10);
                else
                    o2 = null;
            } else if (columnName.equals("comment")) {
                o1 = v1.getComment().toUpperCase();
                o2 = v2.getComment().toUpperCase();
            } else {
                // Sort by Date
                o1 = v1.getCalldate();
                o2 = v2.getCalldate();
            }

            // Treat empty strings like nulls
            if (o1 instanceof String && ((String) o1).trim().length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String) o2).trim().length() == 0) {
                o2 = null;
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable) o1).compareTo(o2);
                } else {
                    return ((Comparable) o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }

        public String format(String s, int places) {
            int j = places - s.length();
            if (j > 0) {
                StringBuffer sb = null;
                sb = new StringBuffer(j);
                for (int k = 0; k < j; k++)
                    sb.append(' ');
                return sb.toString() + s;
            } else
                return s;
        }
    }

    // ************************************************************************************************************
    /**
     * Updates the call filter.
     */
    public void updateFilter() {
        boolean filterCallIn = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.callin"));
        boolean filterCallInFailed = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.callinfailed"));
        boolean filterCallOut = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.callout"));
        boolean filterNumber = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.number"));
        boolean filterFixed = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.fixed"));
        boolean filterHandy = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.handy"));
        boolean filterDate = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.date"));
        boolean filterSip = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.sip"));
        boolean filterCallByCall = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.callbycall"));
        boolean filterComment = JFritzUtils.parseBoolean(JFritz
                .getProperty("filter.comment"));
        String filterSearch = JFritz.getProperty("filter.search", "");
        String filterDateFrom = JFritz.getProperty("filter.date_from", "");
        String filterDateTo = JFritz.getProperty("filter.date_to", "");

        try {
            jfritz.getJframe().getCallerTable().getCellEditor()
                    .cancelCellEditing();
        } catch (NullPointerException e) {
        }

        if ((!filterCallIn) && (!filterCallInFailed) && (!filterCallOut)
                && (!filterNumber) && (!filterDate) && (!filterHandy)
                && (!filterFixed) && (!filterSip) && (!filterCallByCall)
                && (!filterComment) && (filterSearch.length() == 0)) {
            // Use unfiltered data
            filteredCallerData = unfilteredCallerData;
            sortAllFilteredRowsBy(sortColumn, sortDirection);
        } else { // Data got to be filtered
            Vector filteredSipProviders = new Vector();
            if (filterSip) {
                String providers = JFritz.getProperty("filter.sipProvider",
                        "[]");
                if (providers.equals("[]")) { // No entries selected
                    filterSip = false;
                }
                providers = providers.replaceAll("\\[", "");
                providers = providers.replaceAll("\\]", "");
                String[] providerEntries = providers.split(",");
                for (int i = 0; i < providerEntries.length; i++) {
                    if (providerEntries[i].length() > 0) {
                        if (providerEntries[i].charAt(0) == 32) { // delete
                            // first SPACE
                            providerEntries[i] = providerEntries[i]
                                    .substring(1);
                        }
                    }
                    filteredSipProviders.add(providerEntries[i]);
                }
            }

            Vector filteredCallByCallProviders = new Vector();
            if (filterCallByCall) {
                String providers = JFritz.getProperty(
                        "filter.callbycallProvider", "[]");
                if (providers.equals("[]")) { // No entries selected
                    filterCallByCall = false;
                }
                providers = providers.replaceAll("\\[", "");
                providers = providers.replaceAll("\\]", "");
                String[] providerEntries = providers.split(",");
                for (int i = 0; i < providerEntries.length; i++) {
                    if (providerEntries[i].length() > 0) {
                        if (providerEntries[i].charAt(0) == 32) { // delete
                            // first SPACE
                            providerEntries[i] = providerEntries[i]
                                    .substring(1);
                        }
                    }
                    filteredCallByCallProviders.add(providerEntries[i]);
                }
            }

            Vector filteredcallerdata;
            filteredcallerdata = new Vector();
            Enumeration en = unfilteredCallerData.elements();
            while (en.hasMoreElements()) {
                Call call = (Call) en.nextElement();
                boolean dateFilterPassed = true;
                boolean searchFilterPassed = true;
                boolean handyFilterPassed = true;
                boolean fixedFilterPassed = true;
                boolean sipFilterPassed = true;
                boolean commentFilterPassed = true;

                // SearchFilter: Number, Participant, Date
                String parts[] = filterSearch.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (part.length() > 0
                            && (call.getPhoneNumber() == null || call
                                    .getPhoneNumber().getAreaNumber().indexOf(
                                            parts[i]) == -1)
                            && (call.getPerson() == null || call.getPerson()
                                    .getFullname().toLowerCase().indexOf(
                                            part.toLowerCase()) == -1)) {
                        searchFilterPassed = false;
                        break;
                    }
                }

                if (filterSip) {
                    String route = call.getRoute();
                    if (route.equals("")) {
                        route = "FIXEDLINE";
                    }
                    if (!filteredSipProviders.contains(route)) {
                        searchFilterPassed = false;
                    }
                }

                if (filterCallByCall) {
                    if (call.getPhoneNumber() != null) {
                        String callbycallprovider = call.getPhoneNumber()
                                .getCallByCall();
                        if (callbycallprovider.equals("")) {
                            callbycallprovider = "NONE";
                        }
                        if (!filteredCallByCallProviders
                                .contains(callbycallprovider)) {
                            searchFilterPassed = false;
                        }
                    } else { // Hide calls without number
                        if (!filteredCallByCallProviders.contains("NONE")) {
                            searchFilterPassed = false;
                        }
                    }
                }

                try {
                    if (filterDate
                            && !(call.getCalldate().after(
                                    new SimpleDateFormat("dd.MM.yy")
                                            .parse(filterDateFrom)) && call
                                    .getCalldate().before(
                                            new SimpleDateFormat(
                                                    "dd.MM.yy HH:mm")
                                                    .parse(filterDateTo
                                                            + " 23:59")))) {
                        dateFilterPassed = false;
                    }
                } catch (ParseException e1) {
                }

                if (filterFixed && call.getPhoneNumber() != null
                        && !call.getPhoneNumber().isMobile())
                    fixedFilterPassed = false;

                if (filterHandy && call.getPhoneNumber() != null
                        && call.getPhoneNumber().isMobile())
                    handyFilterPassed = false;
                
                if (filterComment && !call.getComment().equals(JFritz.getProperty("filter.comment.text",""))) {
                    commentFilterPassed = false;
                }

                if (searchFilterPassed && dateFilterPassed && handyFilterPassed
                        && fixedFilterPassed && sipFilterPassed && commentFilterPassed)
                    if (!(filterNumber && call.getPhoneNumber() == null)) {
                        if ((!filterCallIn)
                                && (call.getCalltype().toInt() == CallType.CALLIN))
                            filteredcallerdata.add(call);
                        else if ((!filterCallInFailed)
                                && (call.getCalltype().toInt() == CallType.CALLIN_FAILED))
                            filteredcallerdata.add(call);
                        else if ((!filterCallOut)
                                && (call.getCalltype().toInt() == CallType.CALLOUT))
                            filteredcallerdata.add(call);
                    }
            }
            filteredCallerData = filteredcallerdata;
            sortAllFilteredRowsBy(sortColumn, sortDirection);
        }
        if (jfritz.getJframe() != null)
            jfritz.getJframe().setStatus();
    }

    /**
     * @return Total duration of all (filtered) calls
     */
    public int getTotalDuration() {
        Enumeration en = getFilteredCallVector().elements();
        int total = 0;
        while (en.hasMoreElements()) {
            Call call = (Call) en.nextElement();
            total += call.getDuration();
        }
        return total;
    }

    /**
     * @return Total costs of all filtered calls
     */
    public int getTotalCosts() {
        Enumeration en = getFilteredCallVector().elements();
        int total = 0;
        while (en.hasMoreElements()) {
            Call call = (Call) en.nextElement();
            if (call.getCost() > 0) // Negative Kosten => unbekannte kosten
                total += call.getCost();
        }
        return total;
    }

    /**
     * @return Returns the jfritz.
     */
    public final JFritz getJfritz() {
        return jfritz;
    }

    /**
     * @param person
     * @return Returns last call of person
     */
    public Call findLastCall(Person person) {
        // FIXME: Nicht nur Standardnummer suchen
        Enumeration en = unfilteredCallerData.elements();
        while (en.hasMoreElements()) {
            Call call = (Call) en.nextElement();
            if (call.getPhoneNumber() != null
                    && person.getStandardTelephoneNumber() != null
                    && call.getPhoneNumber().getIntNumber().equals(
                            person.getStandardTelephoneNumber().getIntNumber())) {
                return call;
            }
        }
        return null;
    }

    public void clearList() {
        Debug.msg("Clearing caller Table");
        unfilteredCallerData.clear();
        if ((jfritz.getJframe() != null) && jfritz.getJframe().getCallerTable() != null)
            jfritz.getJframe().getCallerTable().clearSelection();
        saveToXMLFile(JFritz.CALLS_FILE, true);
        fireTableDataChanged();
    }

    public void removeEntries() {
        if (JOptionPane.showConfirmDialog(jfritz.getJframe(), "Wirklich " // TODO
                // I18N
                + JFritz.getMessage("delete_entries") + "?",
                JFritz.PROGRAM_NAME, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Debug.msg("Removing entries");
            int row[] = jfritz.getJframe().getCallerTable().getSelectedRows();
            if (row.length > 0) {
                Vector personsToDelete = new Vector();
                for (int i = 0; i < row.length; i++) {
                    personsToDelete.add(filteredCallerData.get(row[i]));
                }
                Enumeration en = personsToDelete.elements();
                while (en.hasMoreElements()) {
                    unfilteredCallerData.remove(en.nextElement());
                }
                saveToXMLFile(JFritz.CALLS_FILE, true);
                updateFilter();
                fireTableDataChanged();
            }
        }
    }

    public void fireTableDataChanged() {
        // Kostenberechnung deaktiviert, weil es zu ungenau ist
        // und nur zu Problemen f�hrt
        // calculateCosts();
        super.fireTableDataChanged();
    }

    /**
     * calculateCosts Deaktiviert, da eh zu ungenau
     * 
     */
    /*
     * private void calculateCosts() {
     *  // L�sche Anrufliste der SipProvider for (int k = 0; k <
     * jfritz.getSIPProviderTableModel().getProviderList() .size(); k++) {
     * SipProvider sipprovider = (SipProvider) jfritz
     * .getSIPProviderTableModel().getProviderList().get(k);
     * sipprovider.clearCalls(); }
     * 
     * for (int i = unfilteredCallerData.size(); i > 0; i--) { Call call =
     * (Call) unfilteredCallerData.get(i - 1); if (call.getCalltype().toInt() ==
     * 3) { // Nur abgehende Gespr�che // berechnen if
     * (call.getPhoneNumber().isEmergencyCall() ||
     * call.getPhoneNumber().isFreeCall() ||
     * call.getPhoneNumber().isSIPNumber()) { call.setCost(0); } else if
     * (call.getRoute().startsWith("SIP")) { SipProvider sipProvider = null;
     * 
     * for (int j = 0; j < jfritz.getSIPProviderTableModel()
     * .getProviderList().size(); j++) { sipProvider = (SipProvider) jfritz
     * .getSIPProviderTableModel().getProviderList() .get(j);
     * 
     * if (sipProvider.getProviderID() == Integer
     * .parseInt(call.getRoute().substring(3))) { break; } else { sipProvider =
     * null; } } if (sipProvider != null) { // F?ge Anrufe zur //
     * SipProvider-Anrufliste sipProvider.addCall(call); } } else {// Es wurde
     * kein (oder unbekannter) VoIP-Provider // benutzt => unbekannte Kosten
     * call.setCost(-1); } } else { // Ankommende oder verpasse Anrufe => keine
     * Kosten call.setCost(0); } }
     *  // Berechne die Kosten f�r die Anrufe, die �ber SipProvider gef�hrt //
     * wurden for (int k = 0; k <
     * jfritz.getSIPProviderTableModel().getProviderList() .size(); k++) {
     * SipProvider sipprovider = (SipProvider) jfritz
     * .getSIPProviderTableModel().getProviderList().get(k);
     * sipprovider.calculateCosts(); } }
     */
    public String getRealColumnName(int columnIndex) {
        String columnName = "";
        if (jfritz != null && jfritz.getJframe() != null) {
            Enumeration en = jfritz.getJframe().getCallerTable()
                    .getTableHeader().getColumnModel().getColumns();
            while (en.hasMoreElements()) {
                TableColumn col = (TableColumn) en.nextElement();
                if (col.getModelIndex() == columnIndex)
                    columnName = col.getIdentifier().toString();
            }
        }
        return columnName;
    }
    
    private static void doBackup() {
        CopyFile backup = new CopyFile();
        backup.copy(".","xml");
    }

	public Call getSelectedCall()
	{
		int rows[]=null;
        if ((jfritz != null) && (jfritz.getJframe() != null)) 
            rows = jfritz.getJframe().getCallerTable().getSelectedRows();
    	
        if (rows != null && rows.length==1)
        	return (Call)this.filteredCallerData.elementAt(rows[0]);
    	else
    		Debug.errDlg("Bitte (nur) einen Anruf markieren.");
    	
		return null;
	}
	
	 /**
	   * @author Brian Jensen
	   * 
	   * function reads the file and processes it line by line
	   * using the appropriate parse function based on the structure
	   * 
	   * currently supported file types: 
	   * JFritz's own export format: EXPORT_CSV_FOMAT_JFRITZ
	   * Exported files from the fritzbox's web interface: EXPORT_CSV_FORMAT_FRITZBOX 
	   * Exported files from newer boxes (7170) EXPORT_CSV_FORMAT_FRITZBOX_NEWFIRMWARE
	   * 
	   * function also has the ability to 'nicely' handle broken CSV lines
	   *  
	   * @param filename of the csv file to import from
	   */
	  public void importFromCSVFile(String filename){
	    //Is the performace gain from this really worth it?
	    //And if there are duplicate calls, only the first one gets filtered out
	    alreadyKnownCalls = (Vector) unfilteredCallerData.clone();
	    Debug.msg("Importing from csv file " + filename);
	    String line = "";
	    boolean isJFritzExport = false; //flag to check which type to parse
	    boolean isNewFirmware = false;  //check if its was exported with a new box
	    
	    try {
	      FileReader fr = new FileReader(filename);
	          BufferedReader br = new BufferedReader(fr);
	          line = br.readLine();   
	          
	          //check if we have a correct header
	          if(line.equals(EXPORT_CSV_FORMAT_JFRITZ) || line.equals(EXPORT_CSV_FORMAT_FRITZBOX)
	        		  || line.equals(EXPORT_CSV_FORMAT_FRITZBOX_NEWFIRMWARE)){
	        	  
	        	  if(line.equals(EXPORT_CSV_FORMAT_JFRITZ))
	        		  isJFritzExport = true;
	        	  else if(line.equals(EXPORT_CSV_FORMAT_FRITZBOX_NEWFIRMWARE))
	        	  		isNewFirmware = true;
	        	  
	        	  int linesRead = 0;
	        	  int newEntries = 0;
	        	  Call c;
	        	  while(null != (line = br.readLine())){
	        		  linesRead++;
	        		  
	        		  //call the apropriate parse function
	        		  if(isJFritzExport)
	        			  c = parseCallJFritzCSV(line);
	        		  else
	        			  c = parseCallFritzboxCSV(line, isNewFirmware);
	        		  
	        		  if(c == null)
	        			  Debug.msg("Error encountered processing the csv file, continuing");
	        		  else if(addEntry(c)){
	        			  newEntries++;
	                
	        		  }
	        	  }
	            
	        	  Debug.msg(linesRead+" Lines read from csv file "+filename);
	        	  Debug.msg(newEntries+" New entries processed");
	                                 
	        	  if (newEntries > 0) {
	        		  sortAllUnfilteredRows();
	        		  saveToXMLFile(JFritz.CALLS_FILE, true);
	        		  String msg;
	              
	        		  if (newEntries == 1) {
	        			  msg = JFritz.getMessage("imported_call");
	        		  } else {
	        			  msg = newEntries + " "+JFritz.getMessage("imported_calls");
	        		  }
	
	        		  JFritz.infoMsg(msg);
	          
	        	  }else{
	        		  JFritz.infoMsg(JFritz.getMessage("no_imported_calls"));
	        	  }
	                           
	          }else{
	        	  //Invalid file header
	        	  Debug.err("Wrong file type or corrupted file");
	          }
	          
	          br.close();
	          } catch (FileNotFoundException e) {
	              Debug.err("Could not read from " + filename + "!");
	          } catch(IOException e){
	            Debug.err("IO Exception reading csv file");
	          }
	          
	  }
	  
	  /**
	   * @author Brian Jensen
	   * 
	   * function first splits the line into substrings, then strips the quotationmarks(do those have to be?)
	   * functions parses according to the format EXPORT_CSV_FORMAT_JFRITZ
	   * 
	   * 
	   * @param line contains the line to be processed from a csv file
	   * @return returns a call object, or null if the csv line is invalid
	   */
	  public Call parseCallJFritzCSV(String line){
	    String[] field = line.split(PATTERN_CSV);
	    Call call;
	    CallType calltype;
	    Date calldate;
	    PhoneNumber number;
	    
	    //check if line has correct amount of entries
	    if(field.length < 12){
	      Debug.err("Invalid CSV format!");
	      return null;
	    }
	    
	    //Strip those damn quotes
	    for(int i=0; i < 12; i++)
	      field[i] = field[i].substring(1, field[i].length()-1);
	      
	    //Call type
	    //Perhaps it would be nice to standardize the calltype and export strings
	    if(field[0].equals("Incoming")){
	        calltype = new CallType("call_in");
	    }else if(field[0].equals("Missed")){
	      calltype = new CallType("call_in_failed");
	    }else if(field[0].equals("Outgoing")){
	      calltype = new CallType("call_out");
	    }else{
	      Debug.err("Invalid Call type in CSV file!");
	      return null;
	    }
	    
	    //Call date and time
	    if(field[1] != null && field[2] != null){
	      
	      try{
	        calldate = new SimpleDateFormat("dd.MM.yy HH:mm").parse(field[1]+" "+field[2]);
	      }catch(ParseException e){
	        Debug.err("Invalid date format in csv file!");
	        return null;
	      }
	    }else{
	      Debug.err("Invalid CSV file!");
	      return null;
	    }

	    //Phone number
	    if(field[3] != null){
	      number = new PhoneNumber(field[3]);
	      number.setCallByCall(field[10]);
	    }else
	      number = null;
	    
	    //now make the call object
	    //TODO: change the order of the Call constructor to fit 
	    //the oder of the csv export function or vice versa!!!
	    call = new Call(jfritz, calltype, calldate, number, field[5], field[4], 
	        Integer.parseInt(field[6]));
	    
	    //TODO: perhaps split export function into two functions
	    //exportCallListCSV() and exportPhoneBookCSV()
	    //the few entries in the current export format are not complete
	    //enough to reconstruct the phonebook correctly
	    call.setComment(field[11]);

	    return call;
	  }

	  /**
	   * @author Brian Jensen
	   * function parses a line of a csv file, that was directly exported
	   * from the Fritzbox web interface
	   * function parses according to format: EXPORT_CSV_FORMAT_FRITZBOX 
	   * and EXPORT_CSV_FORMAT_FRITZBOX_NEWFIRMWARE
	   * 
	   * @param line contains the line to be processed
	   * @return is call object, or null if the csv was invalid
	   */
	  public Call parseCallFritzboxCSV(String line, boolean isNewFirmware){
		  String[] field = line.split(PATTERN_CSV);
		    Call call;
		    CallType calltype;
		    Date calldate;
		    PhoneNumber number;
	  
		    //check if line has correct amount of entries
		    if(field.length < 6){
		      Debug.err("Invalid CSV format!");
		      return null;
		    }
	  
		    //Call type
		    //Why would they change the cvs format in the new firmware???
		    if((field[0].equals("1") && !isNewFirmware)
		    		|| (field[0].equals("2") && isNewFirmware)){
		        calltype = new CallType("call_in");
		    }else if((field[0].equals("2") && !isNewFirmware)
		    		|| (field[0].equals("3") && isNewFirmware)){
		      calltype = new CallType("call_in_failed");
		    }else if((field[0].equals("3") && !isNewFirmware)
		    		|| (field[0].equals("1") && isNewFirmware)){
		      calltype = new CallType("call_out");
		    }else{
		      Debug.err("Invalid Call type in CSV file!");
		      return null;
		    }
		    
		    //Call date and time
		    if(field[1] != null){
		    	try{
			        calldate = new SimpleDateFormat("dd.MM.yy HH:mm").parse(field[1]);
			      }catch(ParseException e){
			        Debug.err("Invalid date format in csv file!");
			        return null;
			      }
		    }else{
		    	Debug.err("Invalid CSV file!");
		    	return null;
		    }
		    
		    //Phone number
		    if(field[2] != null)
		      number = new PhoneNumber(field[2]);
		    else
		      number = null;
		    
		    //split the duration into two stings, hours:minutes
		    String[] time = field[5].split(":");
		    //make the call object
		    call = new Call(jfritz, calltype, calldate, number, field[3], field[4], 
		        Integer.parseInt(time[0])*3600 + Integer.parseInt(time[1])*60);
		    
		    return call;
	  
	  }

}
