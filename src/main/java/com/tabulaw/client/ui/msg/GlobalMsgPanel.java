/**
 * The Logic Lab
 * @author jpk
 * Mar 2, 2009
 */
package com.tabulaw.client.ui.msg;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.ui.IWidgetRef;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;

/**
 * GlobalMsgPanel - Displayes sourced and un-sourced messages that are
 * removable.
 * @author jpk
 */
public class GlobalMsgPanel extends Composite implements IMsgDisplay {

	/**
	 * Styles - (msg.css)
	 * @author jpk
	 */
	static class Styles {

		/**
		 * The root message style
		 */
		public static final String MSG = "msg";

		/**
		 * Style for the container widget.
		 */
		public static final String GLOBAL = "gmsg";
	}

	private static final MsgLevel[] order = new MsgLevel[] {
		MsgLevel.FATAL, MsgLevel.ERROR, MsgLevel.WARN, MsgLevel.INFO };

	private static final int index(MsgLevel level) {
		for(int i = 0; i < order.length; i++) {
			if(order[i] == level) {
				return i;
			}
		}
		throw new IllegalStateException("Unable to resolve msg level index for: " + level);
	}

	/**
	 * Container for the child msg level panels.
	 */
	private final FlowPanel container;

	/**
	 * Constructor
	 */
	public GlobalMsgPanel() {
		container = new FlowPanel();
		container.addStyleName(Styles.MSG);
		container.addStyleName(Styles.GLOBAL);
		MutableMsgLevelPanel p;
		for(final MsgLevel l : order) {
			p = new MutableMsgLevelPanel(l);
			p.setVisible(false);
			container.add(p);
		}
		initWidget(container);
	}

	@Override
	public Widget getDisplayWidget() {
		return this;
	}

	/**
	 * @param level the msg level
	 * @return the queried for msg panel bound the given msg level.
	 */
	private MutableMsgLevelPanel getMsgLevelPanel(MsgLevel level) {
		return (MutableMsgLevelPanel) container.getWidget(index(level));
	}

	@Override
	public void add(IWidgetRef wref, Iterable<Msg> msgs, Integer classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.add(wref, msgs, classifier);
			if(p.size() > 0) p.setVisible(true);
		}
	}

	@Override
	public void add(IWidgetRef wref, Msg msg, Integer classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.add(wref, msg, classifier);
			if(p.size() > 0) p.setVisible(true);
		}
	}

	@Override
	public void add(Iterable<Msg> msgs, Integer classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.add(msgs, classifier);
			if(p.size() > 0) p.setVisible(true);
		}
	}

	@Override
	public void add(Msg msg, Integer classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.add(msg, classifier);
			if(p.size() > 0) p.setVisible(true);
		}
	}

	@Override
	public void remove(IWidgetRef wref, Integer classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.remove(wref, classifier);
			if(p.size() == 0) p.setVisible(false);
		}
	}

	@Override
	public void removeUnsourced(Integer classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.remove(null, classifier);
			if(p.size() == 0) p.setVisible(false);
		}
	}

	@Override
	public void remove(MsgLevel level) {
		final MutableMsgLevelPanel p = getMsgLevelPanel(level);
		p.clear();
		p.setVisible(false);
	}

	@Override
	public void remove(int classifier) {
		MutableMsgLevelPanel p;
		for(final MsgLevel level : order) {
			p = getMsgLevelPanel(level);
			p.remove(classifier);
			if(p.size() == 0) p.setVisible(false);
		}
	}

	@Override
	public void clear() {
		for(final MsgLevel level : order) {
			remove(level);
		}
	}

	@Override
	public int size(MsgLevel level) {
		return getMsgLevelPanel(level).size();
	}

	@Override
	public int size() {
		int c = 0;
		for(final MsgLevel l : order) {
			c += size(l);
		}
		return c;
	}
}
