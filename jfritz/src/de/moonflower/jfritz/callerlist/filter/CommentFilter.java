package de.moonflower.jfritz.callerlist.filter;

import de.moonflower.jfritz.struct.Call;

public class CommentFilter extends CallFilter {

	public boolean passFilter(Call currentCall) {

		if (!currentCall.getComment().equals(""))
			return true;
		return false;
	}
}
