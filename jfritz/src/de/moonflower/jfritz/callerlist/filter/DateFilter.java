/*
 * Created on 07.09.2006
 *
 */
package de.moonflower.jfritz.callerlist.filter;

import java.util.Date;
import com.toedter.calendar.JDateChooser;
import de.moonflower.jfritz.struct.Call;

/**
 * Date filter for call list
 * 
 * @author Robert Palmer
 */
public class DateFilter extends CallFilter {
    public JDateChooser d;
    private Date startDate;
    private Date endDate;

//    public DateFilter(Date from, Date to) {
        public DateFilter(Date from, Date to) {
        	// make sure from is not after to
        	if(from.after(to)){
        		Date temp = from;
        		from = to;
        		to = temp;
        	}
//        	Debug.msg(from.toLocaleString());
//        	Debug.msg(to.toLocaleString());
        	//TODO status updaten
        	startDate = from;
        	endDate = to;
        	
    }



    public boolean passInternFilter(Call currentCall) {
            if (currentCall.getCalldate().after(startDate)
                && currentCall
                    .getCalldate().before(endDate)) { 
                return true;
            } else
                return false;
    }



	public Date getEndDate() {
		return endDate;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public Date getStartDate() {
		return startDate;
	}



	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}