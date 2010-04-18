/**
 * The Logic Lab
 * @author jpk
 * @since Mar 9, 2009
 */
package com.tll.client.ui.msg;

import com.google.gwt.user.client.ui.Widget;
import com.tll.client.ui.IWidgetRef;
import com.tll.common.msg.Msg;
import com.tll.common.msg.Msg.MsgLevel;

/**
 * IMsgDisplay - Generic definition for posting and removing messages in the ui
 * of varying type.
 * @author jpk
 */
public interface IMsgDisplay {

	/**
	 * @return The widget that holds the messages.
	 */
	Widget getDisplayWidget();

	/**
	 * Add a single sourced message with an optional classifier id.
	 * @param wref
	 * @param msg
	 * @param classifier classifier id which may be <code>null</code>.
	 */
	void add(IWidgetRef wref, Msg msg, Integer classifier);

	/**
	 * Add multiple sourced messages with an optional classifier id.
	 * @param wref
	 * @param msgs
	 * @param classifier classifier id which may be <code>null</code>.
	 */
	void add(IWidgetRef wref, Iterable<Msg> msgs, Integer classifier);

	/**
	 * Add multiple un-sourced messages with an optional classifier id.
	 * @param msgs
	 * @param classifier classifier id which may be <code>null</code>.
	 */
	void add(Iterable<Msg> msgs, Integer classifier);

	/**
	 * Add a single un-sourced message with an optional classifier id.
	 * @param msg
	 * @param classifier classifier id which may be <code>null</code>.
	 */
	void add(Msg msg, Integer classifier);

	/**
	 * Remove all posted messages that source to the given widget with the
	 * following optional classifier id.
	 * @param wref the widget reference
	 * @param classifier classifier id which may be <code>null</code>.
	 */
	void remove(IWidgetRef wref, Integer classifier);

	/**
	 * Remove all posted un-sourced messages.
	 * @param classifier classifier id which may be <code>null</code>.
	 */
	void removeUnsourced(Integer classifier);

	/**
	 * Removes all messages of the given level.
	 * @param level
	 */
	void remove(MsgLevel level);

	/**
	 * Removes all messages associated with the given classifier.
	 * @param classifier the clasifier id
	 */
	void remove(int classifier);

	/**
	 * Remove <em>all</em> messages from this display.
	 */
	void clear();

	/**
	 * @param level
	 * @return the number of posted messages of the given level.
	 */
	int size(MsgLevel level);

	/**
	 * @return the total number of posted messages.
	 */
	int size();
}