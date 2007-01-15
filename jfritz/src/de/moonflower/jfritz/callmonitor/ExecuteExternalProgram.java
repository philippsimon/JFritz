/*
 * Created on 13.09.2006
 *
 */
package de.moonflower.jfritz.callmonitor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

public class ExecuteExternalProgram extends CallMonitorAdaptor {

	private String replaceParameter(String parameter, String callerNumber, String calledNumber, Person person) {
		parameter = JFritzUtils.deconvertSpecialChars(parameter);
        parameter = parameter.replaceAll("%Number", callerNumber); //$NON-NLS-1$
        parameter = parameter.replaceAll("%Name", person.getFullname()); //$NON-NLS-1$
        parameter = parameter.replaceAll("%Called", calledNumber); //$NON-NLS-1$
        parameter = parameter.replaceAll("%Firstname", person.getFirstName()); //$NON-NLS-1$
        parameter = parameter.replaceAll("%Surname", person.getLastName()); //$NON-NLS-1$
        parameter = parameter.replaceAll("%Company", person.getCompany()); //$NON-NLS-1$

        if (parameter.indexOf("%URLENCODE") > -1) { //$NON-NLS-1$
            try {
                Pattern p;
                p = Pattern.compile("%URLENCODE\\(([^;]*)\\);"); //$NON-NLS-1$
                Matcher m = p.matcher(parameter);
                while (m.find()) {
                    String toReplace = m.group();
                    toReplace = toReplace.replaceAll("\\\\", "\\\\\\\\"); //$NON-NLS-1$,  //$NON-NLS-2$
                    toReplace = toReplace.replaceAll("\\(", "\\\\("); //$NON-NLS-1$, //$NON-NLS-2$
                    toReplace = toReplace.replaceAll("\\)", "\\\\)"); //$NON-NLS-1$, //$NON-NLS-2$
                    String toEncode = m.group(1);
                    parameter = parameter.replaceAll(toReplace,
                            URLEncoder.encode(toEncode, "UTF-8")); //$NON-NLS-1$
                }
            } catch (UnsupportedEncodingException uee) {
                Debug.err("JFritz.class: UnsupportedEncodingException: " //$NON-NLS-1$
                        + uee.toString());
            }
        }
        return parameter;
	}
	
	private void startProgram(String programString) {
        if (programString.equals("")) { //$NON-NLS-1$
            Debug.errDlg(Main.getMessage("no_external_program") //$NON-NLS-1$
                    + programString);
            return;
        }
        Debug.msg("Start external Program: " + programString); //$NON-NLS-1$
        try {
            Runtime.getRuntime().exec(programString);
        } catch (IOException e) {
            Debug.errDlg(Main.getMessage("not_external_program_start") //$NON-NLS-1$
                    + programString);
            Debug.err(e.toString());
        }
	}
	
    public void pendingCallIn(Call call) {
    }

    public void establishedCallIn(Call call) {
    }

    public void pendingCallOut(Call call) {
    }

    public void establishedCallOut(Call call) {
    }

    public void endOfCall(Call call) {
    }
}
