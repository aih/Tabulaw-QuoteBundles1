/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Mar 2, 2009
 */
package com.tabulaw.client.ui.msg;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.tabulaw.client.ui.HtmlListPanel;
import com.tabulaw.client.ui.ImageContainer;
import com.tabulaw.client.ui.P;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;

/**
 * MsgPanel - A panel able to display multiple {@link Msg}s.
 * @author jpk
 */
public class MsgPanel extends Composite {

	/**
	 * Styles - (msg.css)
	 * @author jpk
	 */
	static class Styles {

		/**
		 * Style applied to the containing div.
		 */
		public static final String CMSG = "cmsg";

		/**
		 * Style applied to to widgets containing messages.
		 */
		public static final String MSG = "msg";
	} // Styles

	/**
	 * Entry
	 * @author jpk
	 */
	static class Entry {
		final Integer classifier;
		final Msg msg;
		/**
		 * Constructor
		 * @param classifier
		 * @param msg
		 */
		public Entry(Integer classifier, Msg msg) {
			super();
			this.classifier = classifier;
			this.msg = msg;
		}
	} // Entry

	/**
	 * The DOM element property signifying the associated msg level. This property
	 * is set for all created sub-panels of {@link #container}.
	 */
	private static final String ELEM_PROP_MSG_LEVEL = "_ml";

	/**
	 * The associations of messages to ui artifact needed to realize message
	 * removability.
	 */
	private final HashMap<Entry, P> bindings = new HashMap<Entry, P>();

	/**
	 * Contains sub-panels categorized my {@link MsgLevel}.
	 */
	private final HorizontalPanel container = new HorizontalPanel();

	/**
	 * Constructor
	 */
	public MsgPanel() {
		super();
		container.setStyleName(Styles.CMSG);
		container.addStyleName(Styles.MSG);
		initWidget(container);
	}

	/**
	 * Removes all messages from this panel.
	 */
	public void clear() {
		container.clear();
		bindings.clear();
	}

	/**
	 * @return The number of contained messages.
	 */
	public int size() {
		return bindings.size();
	}

	/**
	 * Adds a single {@link Msg} to this panel.
	 * @param msg
	 * @param classifier optional classifier
	 */
	public void addMsg(Msg msg, Integer classifier) {
		final P p = new P(msg.getMsg());
		extract(getMsgLevelPanel(msg.getLevel(), true)).append(p);
		bindings.put(new Entry(classifier, msg), p);
	}

	/**
	 * Adds multiple {@link Msg}s to this panel.
	 * <p>
	 * NOTE: {@link Msg}s are added in the order based on the order of the
	 * provided iterable.
	 * @param msgs
	 * @param classifier optional classifier
	 */
	public void addMsgs(Iterable<Msg> msgs, Integer classifier) {
		if(msgs != null) {
			for(final Msg msg : msgs) {
				addMsg(msg, classifier);
			}
		}
	}

	/**
	 * Removes a single message.
	 * @param msg the message to be removed by logical equality.
	 */
	public void removeMsg(Msg msg) {
		if(msg != null) {
			Entry tormv = null;
			for(final Entry e : bindings.keySet()) {
				if(e.msg.equals(msg)) {
					tormv = e;
					break;
				}
			}
			if(tormv != null) {
				final P p = bindings.get(tormv);
				getMsgLevelPanel(tormv.msg.getLevel(), false).remove(p);
				bindings.remove(tormv);
			}
		}
	}

	/**
	 * Removes multiple messages.
	 * @param msgs the messages to be removed by logical equality.
	 */
	public void removeMsgs(Iterable<Msg> msgs) {
		if(msgs != null) {
			for(final Msg m : msgs) {
				removeMsg(m);
			}
		}
	}

	/**
	 * Removes all messages associated with the given classifier.
	 * @param classifier the classifier id
	 */
	public void removeMsgs(int classifier) {
		final ArrayList<Entry> tormv = new ArrayList<Entry>();
		for(final Entry e : bindings.keySet()) {
			if(e.classifier != null && e.classifier.intValue() == classifier) {
				tormv.add(e);
			}
		}
		remove(tormv);
	}

	/**
	 * Removes all messages that don't have an associated classifier id.
	 */
	public void removeUnclassifiedMsgs() {
		final ArrayList<Entry> tormv = new ArrayList<Entry>();
		for(final Entry e : bindings.keySet()) {
			if(e.classifier == null) {
				tormv.add(e);
			}
		}
		remove(tormv);
	}

	/**
	 * Toggles the display of the associated message level images.
	 * @param show
	 */
	public void setShowMsgLevelImages(boolean show) {
		HorizontalPanel mlp;
		for(final Object o : container) {
			mlp = (HorizontalPanel) o;
			if(show && mlp.getWidgetCount() == 1) {
				// no image so create it
				final MsgLevel level = MsgLevel.values()[mlp.getElement().getPropertyInt(ELEM_PROP_MSG_LEVEL)];
				// NOTE: since this is a clipped image, the width/height should be known
				mlp.insert(new ImageContainer(Util.getMsgLevelImage(level)), 0);
			}
			if(mlp.getWidgetCount() == 2) {
				mlp.getWidget(0).setVisible(show);
			}
		}
	}

	/**
	 * Removes multiple messages.
	 * @param tormv
	 */
	private void remove(ArrayList<Entry> tormv) {
		assert tormv != null;
		for(final Entry e : tormv) {
			final HorizontalPanel mlp = getMsgLevelPanel(e.msg.getLevel(), false);
			assert mlp != null;
			final P p = bindings.get(e);
			final HtmlListPanel hlp = extract(mlp);
			hlp.remove(p);
			bindings.remove(e);
		}
	}

	/**
	 * Extracts the {@link HtmlListPanel} that contains the actual ui message
	 * entries from the given message level panel.
	 * @param msgLevelPanel
	 * @return the nested {@link HtmlListPanel} containing the ui msg entries.
	 */
	private HtmlListPanel extract(HorizontalPanel msgLevelPanel) {
		return (HtmlListPanel) ((msgLevelPanel.getWidgetCount() == 2) ? msgLevelPanel.getWidget(1) : msgLevelPanel
				.getWidget(0));
	}

	/**
	 * Gets the msg sub-panel associated with the given msg level. If not present,
	 * it is created.
	 * @param level
	 * @param createIfAbsent
	 * @return the associated message panel or <code>null<code>
	 */
	private HorizontalPanel getMsgLevelPanel(MsgLevel level, boolean createIfAbsent) {
		HorizontalPanel mlp;
		for(final Object o : container) {
			mlp = (HorizontalPanel) o;
			final int i = mlp.getElement().getPropertyInt(ELEM_PROP_MSG_LEVEL);
			if(level.ordinal() == i) return mlp;
		}
		if(!createIfAbsent) return null;
		// stub the msg level panel
		// (child widget FORMAT: [{msg level img}]{ul html list of msg texts})
		mlp = new HorizontalPanel();
		mlp.addStyleName(Styles.MSG);
		mlp.addStyleName(level.getName().toLowerCase());
		mlp.getElement().setPropertyInt(ELEM_PROP_MSG_LEVEL, level.ordinal());
		mlp.add(new HtmlListPanel(false));
		container.add(mlp);
		return mlp;
	}
}
