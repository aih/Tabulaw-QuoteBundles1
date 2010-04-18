/**
 * The Logic Lab
 * @author jpk Dec 5, 2007
 */
package com.tll.client.data.rpc;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

/**
 * IRpcCommand - Definition for an RPC command callback.
 * @author jpk
 */
public interface IRpcCommand extends Command {

	/**
	 * Sets the sourcing widget.
	 * @param source the widget that will source rpc events
	 */
	void setSource(Widget source);
}
