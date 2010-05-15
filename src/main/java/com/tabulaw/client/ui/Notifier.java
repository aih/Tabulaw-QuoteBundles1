/**
 * The Logic Lab
 * @author jpk
 * @since Mar 1, 2010
 */
package com.tabulaw.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.ui.msg.MsgPopup;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;

/**
 * Displays messages in the UI in set locaiton in a consistent manner.
 * @author jpk
 */
public class Notifier {

	private static final int DEFAULT_DURATION = 1000;

	private static Notifier instance;

	public static Notifier get() {
		if(instance == null) throw new IllegalStateException("Call init first");
		return instance;
	}

	/**
	 * Necessary init method which must be called prior to first use.
	 * @param refWidget messages are shown relative to this widget
	 * @param position how do we shoe the popups relative to the given ref widget?
	 * @param offsetTop pixel top offset relative to the given ref widget
	 * @param offsetLeft pixel left offset relative to the given ref widget
	 */
	public static void init(Widget refWidget, Position position, int offsetTop, int offsetLeft) {
		instance = new Notifier(refWidget, position, offsetTop, offsetLeft);
	}

	private final Widget refWidget;
	private final int offsetTop, offsetLeft;
	private final Position position;

	private Notifier(Widget refWidget, Position position, int offsetTop, int offsetLeft) {
		this.refWidget = refWidget;
		this.position = position;
		this.offsetTop = offsetTop;
		this.offsetLeft = offsetLeft;
	}

	public void info(String msg) {
		post(msg, MsgLevel.INFO, -1, false);
	}

	public void warn(String msg) {
		post(msg, MsgLevel.WARN, -1, false);
	}

	public void error(String msg) {
		post(msg, MsgLevel.ERROR, -1, false);
	}

	public void info(String msg, int duration) {
		post(msg, MsgLevel.INFO, duration, false);
	}

	public void warn(String msg, int duration) {
		post(msg, MsgLevel.WARN, duration, false);
	}

	public void error(String msg, int duration) {
		post(msg, MsgLevel.ERROR, duration, false);
	}

	public void post(Collection<Msg> msgs, int duration) {
		post(msgs, duration, false);
	}

	public void post(Collection<Msg> msgs) {
		post(msgs, DEFAULT_DURATION, false);
	}

	/**
	 * @param caught
	 */
	public void showFor(Throwable caught) {
		String emsg = caught.getMessage();
		error(emsg);
	}

	/**
	 * @param payload
	 */
	public void showFor(Payload payload) {
		showFor(payload, null);
	}

	/**
	 * @param payload
	 * @param defaultSuccessMsg
	 */
	public void showFor(Payload payload, String defaultSuccessMsg) {
		if(payload.hasErrors()) {
			// error
			List<Msg> errorMsgs = payload.getStatus().getMsgs(MsgAttr.EXCEPTION.flag);
			if(errorMsgs.size() > 0) {
				post(errorMsgs, -1);
			}
		}
		else {
			// success
			List<Msg> msgs = payload.getStatus().getMsgs();
			if(msgs == null) msgs = new ArrayList<Msg>();
			if(msgs.size() < 1 && defaultSuccessMsg != null) {
				msgs.add(new Msg(defaultSuccessMsg, MsgLevel.INFO));
			}
			post(msgs, DEFAULT_DURATION);
		}
	}

	public void post(String msg, MsgLevel level, int duration, boolean showImage) {
		final MsgPopup mp = getMsgPopup();
		mp.addMsg(new Msg(msg, level), null);
		// mp.setAnimationEnabled(true);
		mp.showMsgs(Position.TOP, duration, showImage);
	}

	public void post(Collection<Msg> msgs, int duration, boolean showImage) {
		final MsgPopup mp = getMsgPopup();
		for(Msg msg : msgs) {
			mp.addMsg(msg, null);
			// mp.setAnimationEnabled(true);
		}
		mp.showMsgs(Position.TOP, duration, showImage);
	}

	private MsgPopup getMsgPopup() {
		// TODO consider using the same popup?
		return new MsgPopup(refWidget, position, offsetTop, offsetLeft);
	}
}
