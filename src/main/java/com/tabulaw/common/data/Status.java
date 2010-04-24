/**
 * 
 */
package com.tabulaw.common.data;

import java.util.ArrayList;
import java.util.List;

import com.tabulaw.IMarshalable;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;

/**
 * Status - Wrapper around a collection of {@link Msg}s usually employed for
 * decorating client-bound payloads.
 * @author jpk
 */
public final class Status implements IMarshalable {

	private boolean errors = false;

	private List<Msg> msgs;

	/**
	 * Constructor
	 */
	public Status() {
	}

	/**
	 * Constructor
	 * @param msgs
	 */
	public Status(List<Msg> msgs) {
		addMsgs(msgs);
	}

	/**
	 * Constructor
	 * @param msg
	 */
	public Status(Msg msg) {
		addMsg(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param level
	 */
	public Status(String msg, MsgLevel level) {
		addMsg(new Msg(msg, level));
	}

	/**
	 * Constructor
	 * @param msg the message
	 * @param level the msg level
	 * @param attribs the message attribs
	 */
	public Status(String msg, MsgLevel level, int attribs) {
		addMsg(new Msg(msg, level, attribs, null));
	}

	public boolean hasErrors() {
		return errors;
	}

	/**
	 * @return All held {@link Msg}s.
	 */
	public List<Msg> getMsgs() {
		return msgs;
	}

	/**
	 * @return A non-<code>null</code> {@link List} of messages having attributes
	 *         matching those given.
	 * @param attribs The desired attributes to filter against
	 */
	public List<Msg> getMsgs(int attribs) {
		final List<Msg> list = new ArrayList<Msg>();
		if(msgs != null) {
			for(final Msg msg : msgs) {
				final int mas = msg.getAttributes();
				for(final MsgAttr a : MsgAttr.values()) {
					if(((mas & a.flag) == a.flag) && ((attribs & a.flag) == a.flag)) {
						list.add(msg);
					}
				}
			}
		}
		return list;
	}

	public void addMsgs(List<Msg> messages) {
		if(messages != null) {
			for(final Msg m : messages) {
				addMsg(m);
			}
		}
	}

	public void addMsg(Msg msg) {
		if(msg == null) return;
		if(msgs == null) {
			msgs = new ArrayList<Msg>();
		}
		msgs.add(msg);
		errors = (errors || msg.getLevel().isError());
	}

	/**
	 * Adds a {@link Msg} with a specified ref token.
	 * <p>
	 * NOTE: {@link MsgAttr#STATUS} attribute flag is ORd to the message
	 * attributes since this message is part of this {@link Status} instance.
	 * @param msg
	 * @param level
	 * @param attribs
	 * @param refToken
	 * @see Msg#Msg(String, MsgLevel, int, String)
	 */
	public void addMsg(String msg, MsgLevel level, int attribs, String refToken) {
		addMsg(new Msg(msg, level, attribs, refToken));
	}

	/**
	 * Adds a {@link Msg} w/ no ref token.
	 * @param msg
	 * @param level
	 * @param attribs
	 */
	public void addMsg(String msg, MsgLevel level, int attribs) {
		addMsg(msg, level, attribs, null);
	}

	@Override
	public String toString() {
		if(msgs == null || msgs.size() < 1) return "";
		final StringBuilder msg = new StringBuilder();
		for(final Msg sm : msgs) {
			msg.append(sm.toString());
			msg.append("  ");
		}
		return msg.toString();
	}
}
