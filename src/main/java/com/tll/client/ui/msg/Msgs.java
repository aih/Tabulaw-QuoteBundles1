/**
 * The Logic Lab
 * @author jpk
 * Feb 18, 2009
 */
package com.tll.client.ui.msg;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.ui.Position;
import com.tll.common.msg.Msg;

/**
 * Msg - Simple factory methods for creating and displaying un-managed ui popup
 * messages. Therefore, the life-cycle of these created popup messages is
 * dictated on behalf of itself.
 * <p>
 * If an extended life-cycle is needed, employ a {@link MsgPopupRegistry}.
 * @author jpk
 * @see PopupPanel for understanding the life-cycles of message popups created
 *      via one of the factory methods here.
 */
public abstract class Msgs {

	/**
	 * Post a message relative to a given ui widget for display.
	 * @param msg the message to show
	 * @param w the target widget <code>-1</code>, the message dispays
	 *        indefinitely auto-hiding upon a subsequent ui interaction event.
	 */
	public static void post(Msg msg, Widget w) {
		final MsgPopup mp = new MsgPopup(w);
		mp.addMsg(msg, null);
		mp.showMsgs(true);
	}

	/**
	 * Post a message relative to a given ui widget for display.
	 * @param msg the message to show
	 * @param w the target widget
	 * @param p the positioning scheme
	 * @param duration the time in milliseconds to display the message. If
	 *        <code>-1</code>, the message dispays indefinitely auto-hiding upon a
	 *        subsequent ui interaction event.
	 * @param showMsgLevelImage Show a message level image along-side the message?
	 */
	public static void post(Msg msg, Widget w, Position p, int duration, boolean showMsgLevelImage) {
		final MsgPopup mp = new MsgPopup(w);
		mp.addMsg(msg, null);
		mp.showMsgs(p, duration, showMsgLevelImage);
	}

	/**
	 * Post messages relative to a given ui widget for display.
	 * @param msgs the messages to show
	 * @param w the target widget <code>-1</code>, the message dispays
	 *        indefinitely auto-hiding upon a subsequent ui interaction event.
	 */
	public static void post(Iterable<Msg> msgs, Widget w) {
		final MsgPopup mp = new MsgPopup(w);
		mp.addMsgs(msgs, null);
		mp.showMsgs(true);
	}

	/**
	 * Post messages relative to a given ui widget for display.
	 * @param msgs the messages to show
	 * @param w the target widget
	 * @param p the positioning scheme
	 * @param duration the time in milliseconds to display the message. If
	 *        <code>-1</code>, the message dispays indefinitely auto-hiding upon a
	 *        subsequent ui interaction event.
	 * @param showMsgLevelImage Show a message level image along-side the message?
	 */
	public static void post(Iterable<Msg> msgs, Widget w, Position p, int duration, boolean showMsgLevelImage) {
		final MsgPopup mp = new MsgPopup(w);
		mp.addMsgs(msgs, null);
		mp.showMsgs(p, duration, showMsgLevelImage);
	}
}
