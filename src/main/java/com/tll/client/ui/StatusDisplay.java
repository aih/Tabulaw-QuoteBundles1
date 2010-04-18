/**
 * The Logic Lab
 * @author jpk Sep 1, 2007
 */
package com.tll.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tll.client.data.rpc.IStatusHandler;
import com.tll.client.data.rpc.StatusEvent;
import com.tll.client.data.rpc.StatusEventDispatcher;
import com.tll.common.data.Status;
import com.tll.common.msg.Msg;
import com.tll.common.msg.Msg.MsgAttr;

/**
 * StatusDisplay - Console like window that displays messages contained w/in a
 * Status object.
 * @author jpk
 */
public class StatusDisplay extends Composite implements IStatusHandler {

	/**
	 * Styles - (status.css)
	 * @author jpk
	 */
	protected static class Styles {

		public static final String STATUS_DISPLAY = "statusDisplay";
	} // Styles

	/**
	 * StatusMsgDisplay
	 * @author jpk
	 */
	private static final class StatusMsgDisplay extends Composite {

		private final Label msg;

		public StatusMsgDisplay(Msg statusMsg) {
			msg = new Label(statusMsg.getMsg());
			msg.setStylePrimaryName(statusMsg.getLevel().getName().toLowerCase());
			initWidget(msg);
		}
	}

	private final ScrollPanel sp = new ScrollPanel();
	private final VerticalPanel vp = new VerticalPanel();
	private final int attribs;

	/**
	 * Constructor
	 * @param attribs The desired {@link MsgAttr} flags ORd together.
	 */
	public StatusDisplay(int attribs) {
		super();
		sp.setStylePrimaryName(Styles.STATUS_DISPLAY);
		sp.setTitle("Status History");
		sp.add(vp);
		initWidget(sp);
		this.attribs = attribs;
	}

	private void handleStatus(Status status) {
		final List<Msg> msgs = status.getMsgs(attribs);
		if(msgs != null) {
			for(final Msg msg : msgs) {
				vp.insert(new StatusMsgDisplay(msg), 0);
			}
		}
	}

	public void onStatusEvent(StatusEvent event) {
		final Status status = event.getStatus();
		if(status != null) {
			handleStatus(status);
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		StatusEventDispatcher.get().addStatusHandler(this);
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		StatusEventDispatcher.get().removeStatusHandler(this);
	}

}
