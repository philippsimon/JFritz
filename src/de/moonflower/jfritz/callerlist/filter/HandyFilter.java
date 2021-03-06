package de.moonflower.jfritz.callerlist.filter;

import de.moonflower.jfritz.struct.Call;

public class HandyFilter extends CallFilter {

	private static final String type = FILTER_HANDY;
	
	public HandyFilter() {
	}

	public boolean passInternFilter(Call currentCall) {
		if (currentCall.getPhoneNumber() != null
				&& currentCall.getPhoneNumber().isMobile())
			return true;

		return false;
	}
	
	public String getType(){
		return type;
	}
	
	public HandyFilter clone(){
		HandyFilter hf = new HandyFilter();
		hf.setEnabled(this.isEnabled());
		hf.setInvert(this.isInvert());
		return hf;
	}
}
