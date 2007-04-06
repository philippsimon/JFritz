package de.moonflower.jfritz.callerlist;

import java.util.Vector;

import de.moonflower.jfritz.struct.Call;

public interface CallerListListener {

	public void callsAdded(Vector<Call> newCalls);
	
	public void callsRemoved(Vector<Call> callsRemoved);
	
}
