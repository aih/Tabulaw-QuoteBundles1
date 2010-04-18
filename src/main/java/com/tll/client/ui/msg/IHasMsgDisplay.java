/**
 * The Logic Lab
 * @author jpk
 * @since Mar 9, 2009
 */
package com.tll.client.ui.msg;

/**
 * IHasMsgDisplay - A way to generically get/set an {@link IMsgDisplay}.
 * @author jpk
 */
public interface IHasMsgDisplay {

	/**
	 * @return the {@link IMsgDisplay}.
	 */
	IMsgDisplay getMsgDisplay();

	/**
	 * Sets the {@link IMsgDisplay}.
	 * @param msgDisplay the msg display to set
	 */
	void setMsgDisplay(IMsgDisplay msgDisplay);
}
