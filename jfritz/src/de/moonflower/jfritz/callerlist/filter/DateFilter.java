/*
 * Created on 07.09.2006
 *
 */
package de.moonflower.jfritz.callerlist.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.toedter.calendar.JDateChooser;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.utils.Debug;

/**
 * Date filter for call list
 * 
 * @author Robert Palmer
 */
public class DateFilter extends CallFilter {
    public JDateChooser d;
    public static final int DATEFILTER_SELECTION = 0;

    public static final int DATEFILTER_TODAY = 1;

    public static final int DATEFILTER_THIS_MONTH = 2;

    public static final int DATEFILTER_LAST_MONTH = 3;

    public static final int DATEFILTER_YESTERDAY = 4;

    private int filterType = 0;

    private String filterFromDate;

    private String filterToDate;

//    public DateFilter(Date from, Date to) {
        public DateFilter() {
            filterFromDate = Main.getProperty("filter.date_from", new SimpleDateFormat("dd.MM.yy").format(Calendar.getInstance().getTime()));
            filterToDate = Main.getProperty("filter.date_to", new SimpleDateFormat("dd.MM.yy").format(Calendar.getInstance().getTime()));
            filterType = Integer.parseInt(Main.getProperty("filter.date_type", "0")); //$NON-NLS-1$ $NON-NLS-2$
    }

    public boolean passFilter(Call currentCall) {
       try {
            if (currentCall.getCalldate().after(
                    new SimpleDateFormat("dd.MM.yy") //$NON-NLS-1$
                            .parse(filterFromDate))
                && currentCall
                    .getCalldate().before(
                            new SimpleDateFormat(
                                    "dd.MM.yy HH:mm") //$NON-NLS-1$
                                    .parse(filterToDate
                                            + " 23:59"))) { //$NON-NLS-1$
                return true;
            } else
                return false;
        } catch (ParseException e) {
            Debug.err("Problems parsing a date"+ e.toString());
            return false;
        }
    }

    public void setFilter(int datefilter) {
        Date from = null;
        Date to = null;
        filterType = datefilter;
        Main.setProperty("filter.date_type", Integer.toString(filterType)); //$NON-NLS-1$
        switch (datefilter) {
        case DATEFILTER_SELECTION: {
            try {
                int rows[] = JFritz.getJframe().getCallerTable()
                        .getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    Call call = (Call) JFritz.getCallerList()
                            .getFilteredCallVector().get(rows[i]);

                    if (to == null || call.getCalldate().after(to))
                        to = call.getCalldate();

                    if (from == null || call.getCalldate().before(from))
                        from = call.getCalldate();
                }
                filterFromDate = new SimpleDateFormat("dd.MM.yy").format(from);
                filterToDate = new SimpleDateFormat("dd.MM.yy").format(to);

            } catch (Exception e) {
                Debug.err(e.toString());
            }
            break;
        }
        }
        updateDateFilter();
        JFritz.getCallerList().updateFilter();
    }

    public void updateDateFilter() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        switch (filterType) {
        case DATEFILTER_TODAY:  
            filterFromDate = sdf.format(cal.getTime());
            filterToDate = filterFromDate;
            break;
        case DATEFILTER_YESTERDAY:
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
            filterFromDate = sdf.format(cal.getTime());
            filterToDate = filterFromDate;
            break;
        case DATEFILTER_THIS_MONTH:
            filterFromDate = sdf.format(cal.getTime());
            filterToDate = filterFromDate;
            cal.set(Calendar.DAY_OF_MONTH, 1);
            filterFromDate = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            filterToDate = sdf.format(cal.getTime());
            break;
        case DATEFILTER_LAST_MONTH:
            filterFromDate = sdf.format(cal.getTime());
            filterToDate = filterFromDate;
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1); // last
            // month
            // 0=januar,
            // ...,
            // 11=dezember
            cal.set(Calendar.DAY_OF_MONTH, 1);
            filterFromDate = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            filterToDate = sdf.format(cal.getTime());
            break;
        case DATEFILTER_SELECTION: {
            if (filterFromDate == null)
                filterFromDate = sdf.format(cal.getTime());

            if (filterToDate == null)
                filterToDate = sdf.format(cal.getTime());

            break;
        }
        }

        Main.setProperty("filter.date_from", filterFromDate); //$NON-NLS-1$ $NON-NLS-2$
        Main.setProperty("filter.date_to", filterToDate); //$NON-NLS-1$ $NON-NLS-2$        
    }

}