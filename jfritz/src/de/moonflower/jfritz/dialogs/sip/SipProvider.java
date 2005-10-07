/*
 * $Id$
 * 
 * Created on 16.05.2005
 *
 */
package de.moonflower.jfritz.dialogs.sip;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * @author rob
 *  
 */
public class SipProvider {

    private int providerID;

    private boolean active;

    private String providerName, phoneNumber;

    private int startDate, festnetzTakt1, festnetzTakt2, festnetzFreiminuten,
            mobileTakt1, mobileTakt2, mobileFreiminuten, warnFreiminuten;

    private double festnetzKosten, mobileKosten;

    private double totalCosts = 0;

    private double nochFestnetzFreiminuten = 0, nochMobileFreiminuten = 0;

    Vector calls;

    public SipProvider(int providerID, String phoneNumber, String providerName) {
        calls = new Vector();
        this.providerID = providerID;
        this.providerName = providerName;
        this.phoneNumber = phoneNumber;
        active = false;
        startDate = 1;
        festnetzTakt1 = 60;
        festnetzTakt2 = 60;
        festnetzKosten = 1.5;
        festnetzFreiminuten = 0;
        mobileTakt1 = 60;
        mobileTakt2 = 60;
        mobileKosten = 23.0;
        mobileFreiminuten = 0;
    }
    
    /**
     * @return Returns phone number
     */
    public final String getNumber() {
        return phoneNumber;
    }

    /**
     * @return Returns name of sip-provider or IP
     */
    public final String getProvider() {
        return providerName;
    }

    public String toString() {
        return phoneNumber + "@" + providerName;
    }

    /**
     * @return Returns the providerID.
     */
    public final int getProviderID() {
        return providerID;
    }
    
    /**
     * 
     * @param providerID The providerID to set.
     */
    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }

    /**
     * Set VoIP-Provider active state
     * 
     * @param state
     */
    public final void setActive(boolean state) {
        active = state;
    }

    /**
     * 
     * @return VoIP-Provider active state
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Set start date of month
     * 
     * @param date
     */
    public final void setStartDate(int date) {
        startDate = date;
    }

    /**
     * 
     * @return Returns start date of month
     */
    public final int getStartDate() {
        return startDate;
    }

    /**
     * @return Returns XML String
     */
    public String toXML() {
        String sep = System.getProperty("line.separator", "\n");
        String output = "";
        output = ("<entry id=\"" + providerID + "\">" + sep);
        output = output + ("\t<name>" + providerName + "</name>" + sep);
        output = output + ("\t<number>" + phoneNumber + "</number>" + sep);
        output = output + ("\t<active>" + active + "</active>" + sep);
        output = output + ("\t<startdate>" + startDate + "</startdate>" + sep);
        output = output
                + ("\t<festnetztakt1>" + festnetzTakt1 + "</festnetztakt1>" + sep);
        output = output
                + ("\t<festnetztakt2>" + festnetzTakt2 + "</festnetztakt2>" + sep);
        output = output
                + ("\t<festnetzkosten>" + festnetzKosten + "</festnetzkosten>" + sep);
        output = output
                + ("\t<festnetzfreiminuten>" + festnetzFreiminuten
                        + "</festnetzfreiminuten>" + sep);
        output = output
                + ("\t<mobiletakt1>" + mobileTakt1 + "</mobiletakt1>" + sep);
        output = output
                + ("\t<mobiletakt2>" + mobileTakt2 + "</mobiletakt2>" + sep);
        output = output
                + ("\t<mobilekosten>" + mobileKosten + "</mobilekosten>" + sep);
        output = output
                + ("\t<mobilefreiminuten>" + mobileFreiminuten
                        + "</mobilefreiminuten>" + sep);
        output = output
                + ("\t<warnfreiminuten>" + warnFreiminuten
                        + "</warnfreiminuten>" + sep);
        output = output + ("</entry>");
        return output;
    }

    /**
     * @return Returns Festnetztaktung in der ersten Minute
     */
    public int getFestnetzTakt1() {
        return festnetzTakt1;
    }

    /**
     * @param festnetzTakt1
     *            Setze Festnetztaktung in der ersten Minute
     */
    public void setFestnetzTakt1(int festnetzTakt1) {
        this.festnetzTakt1 = festnetzTakt1;
    }

    /**
     * @return Returns Festnetztaktung ab der zweiten Minute
     */
    public int getFestnetzTakt2() {
        return festnetzTakt2;
    }

    /**
     * @param festnetzTakt2
     *            Setze Festnetztaktung ab der zweiten Minute
     */
    public void setFestnetzTakt2(int festnetzTakt2) {
        this.festnetzTakt2 = festnetzTakt2;
    }

    /**
     * @return Returns Kosten f�r ein Festnetzgespr�ch in cent pro Minute
     */
    public double getFestnetzKosten() {
        return festnetzKosten;
    }

    /**
     * @param festnetzKosten
     *            Setze Kosten f�r ein Festnetzgespr�ch in cent pro Minute
     */
    public void setFestnetzKosten(double festnetzKosten) {
        this.festnetzKosten = festnetzKosten;
    }

    /**
     * @return Returns Freiminuten ins Festnetz.
     */
    public int getFestnetzFreiminuten() {
        return festnetzFreiminuten;
    }

    /**
     * @param festnetzFreiminuten
     *            Setze Freiminuten ins Festnetz
     */
    public void setFestnetzFreiminuten(int festnetzFreiminuten) {
        this.festnetzFreiminuten = festnetzFreiminuten;
    }

    /**
     * @return Returns Freiminuten ins Mobilfunknetz.
     */
    public int getMobileFreiminuten() {
        return mobileFreiminuten;
    }

    /**
     * @param mobileFreiminuten
     *            Setze Freiminuten ins Mobilfunknetz.
     */
    public void setMobileFreiminuten(int mobileFreiminuten) {
        this.mobileFreiminuten = mobileFreiminuten;
    }

    /**
     * @return Returns Kosten f�r ein Mobilfunkgespr�ch.
     */
    public double getMobileKosten() {
        return mobileKosten;
    }

    /**
     * @param mobileKosten
     *            Setze Kosten f�r ein Mpbilfunkgespr�ch.
     */
    public void setMobileKosten(double mobileKosten) {
        this.mobileKosten = mobileKosten;
    }

    /**
     * @return Returns Mobilfunktaktung in der ersten Minute.
     */
    public int getMobileTakt1() {
        return mobileTakt1;
    }

    /**
     * @param mobileTakt1
     *            Setze Mobilfunktaktung in der ersten Minute.
     */
    public void setMobileTakt1(int mobileTakt1) {
        this.mobileTakt1 = mobileTakt1;
    }

    /**
     * @return Returns Mobilfunktaktung ab der zweiten Minute.
     */
    public int getMobileTakt2() {
        return mobileTakt2;
    }

    /**
     * @param mobileTakt2
     *            Mobilfunktaktung ab der zweiten Minute.
     */
    public void setMobileTakt2(int mobileTakt2) {
        this.mobileTakt2 = mobileTakt2;
    }

    /**
     * Berechnet die Kosten f�r den Anruf. Dabei werden auch Freiminuten
     * ber�cksichtigt.
     * 
     * @param call
     */
    public void calculateCost(Call call) {
        int takt1;
        int takt2;
        double kostenProMinute;
        double freiMinuten;
        if (call.getPhoneNumber().isMobile()) {
            takt1 = mobileTakt1;
            takt2 = mobileTakt2;
            kostenProMinute = mobileKosten;
            freiMinuten = nochMobileFreiminuten;
        } else {
            takt1 = festnetzTakt1;
            takt2 = festnetzTakt2;
            kostenProMinute = festnetzKosten;
            freiMinuten = nochFestnetzFreiminuten;
        }
        double kostenProTakt1 = ((double) takt1 / 60) * kostenProMinute;
        int restZeit = call.getDuration() - takt1;
        double kostenProTakt2 = ((double) takt2 / 60) * kostenProMinute;

        double freitakte = freiMinuten * 60 / takt1;
        if (freitakte < 0) {
            freitakte = 0;
        }
        double kosten = 0;
        if (freitakte < 1) {
            kosten = 1 * kostenProTakt1; // 1. Minute voll abrechnen
        } else {
            // erste Minute von Freiminuten abgedeckt => keine Kosten
            if (restZeit > 0) {
                kosten = 0;
            } else
                kosten = -2;
            freiMinuten -= takt1 / 60; // Freiminuten um diesen Takt erniedrigen
        }

        if (restZeit > 0) { // weitere Minuten berechnen
            int zuBerechnendeTakte;
            if (restZeit % takt2 == 0) { // Restzeit geht genau auf
                zuBerechnendeTakte = (restZeit / takt2);
            } else {
                zuBerechnendeTakte = (restZeit / takt2) + 1; // aufrunden
            }
            freitakte = freiMinuten * 60 / takt2;
            if (freitakte < 0) {
                freitakte = 0;
            }
            if (freitakte >= zuBerechnendeTakte) {
                // alle Minuten von Freiminuten abgedeckt => Freigespr�ch
                kosten = -2;
                freiMinuten -= zuBerechnendeTakte * takt2 / 60;
            } else {
                zuBerechnendeTakte -= freitakte;
                kosten = kosten + zuBerechnendeTakte * kostenProTakt2;
                freiMinuten -= freitakte * takt2 / 60;
            }
        }
        // restliche Freiminuten abspeichern
        if (call.getPhoneNumber().isMobile()) {
            nochMobileFreiminuten = freiMinuten;
        } else {
            nochFestnetzFreiminuten = freiMinuten;
        }

        totalCosts += kosten;

        // Kosten f�r den Anruf setzen
        call.setCost(kosten);

    }

    /**
     * Calculates costs of all calls in SipProvider specific CallerList
     *  
     */
    public void calculateCosts() {
        totalCosts = 0;
        // sortieren nach Datum
        Collections.sort(calls, new ColumnSorter());
        int lastMonth = 0;
        int lastDate = 0;
        Enumeration en = calls.elements();
        while (en.hasMoreElements()) {
            Call call = (Call) en.nextElement();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(call.getCalldate());

            int currMonth = calendar.get(Calendar.MONTH);
            int currDate = calendar.get(Calendar.DAY_OF_MONTH);

            if ((lastDate < startDate && lastDate < currDate && currDate >= startDate)
                    || (lastDate < startDate && currDate < startDate && currMonth > lastMonth)
                    || (lastDate >= startDate && currDate >= startDate && currMonth > lastMonth)
                    || (lastDate >= startDate && lastDate > currDate
                            && currDate < startDate && currMonth > lastMonth + 1)) {

                // Neuer Abrechnungsmonat angefangen
                // Setze Freiminuten auf Maximum

                nochFestnetzFreiminuten = festnetzFreiminuten;
                nochMobileFreiminuten = mobileFreiminuten;
                lastMonth = currMonth;
                lastDate = currDate;
            }
            calculateCost(call);
        }
        checkRestFreiminuten();
    }

    /**
     * Checkt, ob Freiminuten unter den Mindestfreiminuten liegen und warnt den
     * Nutzer
     *  
     */
    private void checkRestFreiminuten() {
        if (!JFritzUtils.parseBoolean(JFritz.getProperty(
                "state.warningFreeminutesShown", "false"))) {

            if (festnetzFreiminuten > 0
                    && nochFestnetzFreiminuten <= warnFreiminuten) {
                JOptionPane.showMessageDialog(null,
                        "Freiminutenlimit f�r Festnetzgespr�che �ber die Rufnummer "
                                + this.toString() + " unterschritten.",
                        "JFritz! - Warnung", JOptionPane.WARNING_MESSAGE);
                JFritz.setProperty("state.warningFreeminutesShown", "true");
            }
            if (mobileFreiminuten > 0
                    && nochMobileFreiminuten <= warnFreiminuten) {
                JOptionPane.showMessageDialog(null,
                        "Freiminutenlimit f�r Mobilfunkgespr�che �ber die Rufnummer "
                                + this.toString() + " unterschritten.",
                        "JFritz! - Warnung", JOptionPane.WARNING_MESSAGE);
                JFritz.setProperty("state.warningFreeminutesShown", "true");
            }
        }
    }

    /**
     * Adds a call to the SipProvider specific CallerList
     * 
     * @param call
     */
    public void addCall(Call call) {
        calls.add(call);
    }

    /**
     * Clears SipProvider specific CallerList
     *  
     */
    public void clearCalls() {
        calls.clear();
    }

    /**
     * This comparator is used to sort vectors of data
     */
    public class ColumnSorter implements Comparator {

        public int compare(Object a, Object b) {
            Object o1, o2;
            Call v1 = (Call) a;
            Call v2 = (Call) b;

            o1 = v1.getCalldate();
            o2 = v2.getCalldate();

            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2);
            } else {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }

    /**
     * For statistics
     * 
     * @return Total call costs
     */
    public double getTotalCosts() {
        return totalCosts;
    }

    /**
     * For statistics and warn
     * 
     * @return Restliche Freiminuten ins Festnetz
     */
    public double getRestFestnetzFreiminuten() {
        return nochFestnetzFreiminuten;
    }

    /**
     * For statistics and warn
     * 
     * @return Restliche Freiminuten ins Mobilfunknetz
     */
    public double getRestMobileFreiminuten() {
        return nochMobileFreiminuten;
    }

    /**
     * @return Returns the warnFreiminuten.
     */
    public int getWarnFreiminuten() {
        return warnFreiminuten;
    }

    /**
     * @param warnFreiminuten
     *            The warnFreiminuten to set.
     */
    public void setWarnFreiminuten(int warnFreiminuten) {
        this.warnFreiminuten = warnFreiminuten;
    }
}
