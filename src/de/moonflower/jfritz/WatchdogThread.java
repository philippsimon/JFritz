/*
 * Created on 06.03.2006
 *
 */
package de.moonflower.jfritz;

import java.util.Calendar;
import java.util.Date;

import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

public class WatchdogThread extends Thread {

    private int interval = 1;

    private JFritz jfritz;

    private Date now, lastTimestamp;

    private Calendar cal;

    /**
     * 
     * @param jfritz
     * @param interval
     *            in minutes
     */
    public WatchdogThread(JFritz jfritz, int interval) {
        cal = Calendar.getInstance();
        this.jfritz = jfritz;
        this.interval = interval;
        lastTimestamp = cal.getTime();
    }

    public void run() {
        if (jfritz.getJframe().getMonitorButton().isSelected()) {
//            Debug.msg("Watchdog: Check call monitor state");
            checkCallmonitor();
//            Debug.msg("Watchdog: Check done");
        }
    }

    private void checkCallmonitor() {
        cal = Calendar.getInstance();
        now = cal.getTime();

        if (now.getTime() - lastTimestamp.getTime() > 1.5 * interval * 60000) {
            // Mind. ein Interval wurde ausgelassen.
            // Computer wahrscheinlich im Ruhezustand gewesen.
            // Starte den Anrufmonitor neu.

            Debug.msg("Watchdog: Restarting call monitor"); //$NON-NLS-1$
            jfritz.stopCallMonitor();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jfritz.getJframe().startChosenCallMonitor();

			if (JFritzUtils.parseBoolean(JFritz.getProperty("option.watchdog.fetchAfterStandby", "true"))) //$NON-NLS-1$, //$NON-NLS-2$
				jfritz.getJframe().fetchList(JFritzUtils.parseBoolean(JFritz.getProperty("option.deleteAfterFetch", "true"))); //$NON-NLS-1$, //$NON-NLS-2$
        }
        setTimestamp();
    }

    private void setTimestamp() {
        lastTimestamp = Calendar.getInstance().getTime();
    }
}