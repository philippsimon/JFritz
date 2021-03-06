package de.moonflower.jfritz.callmonitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.CallType;
import de.moonflower.jfritz.struct.PhoneNumber;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * Thread. Connects to FritzBox Port 1012. Captures Callermessages.
 * 
 * @author Robert Palmer
 * 
 */

public class FBoxCallMonitorV1 extends FBoxCallMonitor {

	private boolean connected = false;
	
    public FBoxCallMonitorV1() {
    	super();
        Debug.info("FBoxListener V1"); //$NON-NLS-1$
    }

    public void run() {
    	int failedConnections = 0;
    	while (!this.isConnected())
    	{
	        if (failedConnections % 50 == 0 && super.connect()) {
	            Debug.info("Connected"); //$NON-NLS-1$
	            readOutput();
	            failedConnections = 0;
	        }
	        else
	        {
	        	failedConnections++;
	        }
	        if (this.isRunning() == false)
	        {
	        	break;
	        }
	        try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
	        	break;
			}
    	}
    }

    protected void parseOutput(String line) {
        Debug.debug("Server: " + line); //$NON-NLS-1$
        String number = ""; //$NON-NLS-1$
        String provider = ""; //$NON-NLS-1$
        String[] split;
        split = line.split(";", 7); //$NON-NLS-1$
        for (int i = 0; i < split.length; i++) {
            Debug.debug("Split[" + i + "] = " + split[i]); //$NON-NLS-1$,  //$NON-NLS-2$
        }
        if (JFritzUtils.parseBoolean(Main.getProperty(
                "option.callmonitor.monitorIncomingCalls")) //$NON-NLS-1$, //$NON-NLS-2$
                && split[1].equals("RING")) { //$NON-NLS-1$
            if (split[3].equals("")) { //$NON-NLS-1$
                number = Main.getMessage("unknown"); //$NON-NLS-1$
            } else
                number = split[3];
            if (number.endsWith("#")) //$NON-NLS-1$
                number = number.substring(0, number.length() - 1); //$NON-NLS-1$

            if (split[4].equals("")) { //$NON-NLS-1$
                provider = Main.getMessage("fixed_network"); //$NON-NLS-1$
            } else
                provider = split[4];

            provider = JFritz.getSIPProviderTableModel().getSipProvider(
                    provider, provider);
            try {
                Call currentCall = new Call(new CallType(CallType.CALLIN),
                        new SimpleDateFormat("dd.MM.yy HH:mm:ss")
                                .parse(split[0]), new PhoneNumber(number, false), "0", provider, 0);
                JFritz.getCallMonitorList().addNewCall(
                        Integer.parseInt(split[2]), currentCall);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (JFritzUtils.parseBoolean(Main.getProperty(
                "option.callmonitor.monitorOutgoingCalls")) //$NON-NLS-1$,  //$NON-NLS-2$
                && split[1].equals("CALL")) { //$NON-NLS-1$
            if (split[5].equals("")) { //$NON-NLS-1$
                number = Main.getMessage("unknown"); //$NON-NLS-1$
            } else
                number = split[5];
            if (number.endsWith("#"))number = number.substring(0, number.length() - 1); //$NON-NLS-1$
            if (split[4].equals("")) { //$NON-NLS-1$
                provider = Main.getMessage("fixed_network"); //$NON-NLS-1$
            } else
                provider = split[4];
            provider = JFritz.getSIPProviderTableModel().getSipProvider(
                    provider, provider);

            try {
                Call currentCall = new Call(new CallType(CallType.CALLOUT),
                        new SimpleDateFormat("dd.MM.yy HH:mm:ss")
                                .parse(split[0]), 
                                new PhoneNumber(number, JFritzUtils.parseBoolean(Main.getProperty("option.activateDialPrefix"))),
                                split[3], provider, 0);
                JFritz.getCallMonitorList().addNewCall(
                        Integer.parseInt(split[2]), currentCall);
            } catch (ParseException e) {
                Debug.error("FBoxListenerV1: Could not convert call" + e);
            }
        } else if (split[1].equals("DISCONNECT")) { //$NON-NLS-1$
            try {
                int callId = Integer.parseInt(split[2]);
                Call call = JFritz.getCallMonitorList().getCall(callId);
                if (call != null) {
                    call.setDuration(Integer.parseInt(split[3]));
                    JFritz.getCallMonitorList().removeCall(
                            Integer.parseInt(split[2]), call);
                    Thread.sleep(zufallszahl.nextInt(3000));
                }
            } catch (InterruptedException e) {
                Debug.error(e.toString());
	        	Thread.currentThread().interrupt();
            }

        } else if (split[1].equals("CONNECT")) {
            int callId = Integer.parseInt(split[2]);
            String port = split[3];
            if (split[4].equals("")) { //$NON-NLS-1$
                number = Main.getMessage("unknown"); //$NON-NLS-1$
            } else
                number = split[4];
            if (number.endsWith("#")) //$NON-NLS-1$
                number = number.substring(0, number.length() - 1);

            Call call = JFritz.getCallMonitorList().getCall(callId);
            PhoneNumber pn = new PhoneNumber(number, false);
            if (pn.getIntNumber().equals(call.getPhoneNumber().getIntNumber())
        		|| pn.getIntNumber().equals(Main.getProperty("dial.prefix")+call.getPhoneNumber().getIntNumber())) {						
                try {
                    if (JFritz.getCallMonitorList().getCall(callId) != null) {
                        JFritz.getCallMonitorList().getCall(callId)
                                .setCalldate(
                                        new SimpleDateFormat(
                                                "dd.MM.yy HH:mm:ss")
                                                .parse(split[0]));
                        JFritz.getCallMonitorList().getCall(callId).setPort(
                                port);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                JFritz.getCallMonitorList().establishCall(callId);
            }
        }
    }

	public boolean isConnected() {
		return connected;
	}
}