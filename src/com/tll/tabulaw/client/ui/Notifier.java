/**
 * The Logic Lab
 * @author jpk
 * @since Mar 1, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.tll.client.ui.Position;
import com.tll.client.ui.msg.MsgPopup;
import com.tll.common.msg.Msg;
import com.tll.common.msg.Msg.MsgLevel;


/**
 * Displays temporary fading messages in the ui.
 * @author jpk
 */
public class Notifier {
	
	//private static final int SHOW_DURATION_MILLIS = 2000;
	
	private static Notifier instance;
	
	public static Notifier get() {
		if(instance == null) throw new IllegalStateException("Call init first");
		return instance;
	}

	public static void init(Widget refWidget) {
		instance = new Notifier(refWidget);
	}
	
	private final Widget refWidget;
	
	private Notifier(Widget refWidget) {
		this.refWidget = refWidget;
	}
	
	public void info(String msg) {
		show(msg, MsgLevel.INFO);
	}

	public void warn(String msg) {
		show(msg, MsgLevel.WARN);
	}
	
	public void error(String msg) {
		show(msg, MsgLevel.ERROR);
	}
	
	private void show(String msg, MsgLevel level) {
		final MsgPopup mp = new MsgPopup(refWidget);
		mp.addMsg(new Msg(msg, level), null);
		mp.setAnimationEnabled(true);
		mp.showMsgs(Position.BOTTOM, -1, true);
	}
}
