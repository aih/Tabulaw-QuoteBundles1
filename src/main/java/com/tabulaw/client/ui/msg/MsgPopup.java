/**
 * The Logic Lab
 * @author jpk Sep 12, 2007
 */
package com.tabulaw.client.ui.msg;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.DOMExt;
import com.tabulaw.client.ui.DragEvent;
import com.tabulaw.client.ui.PopupHideTimer;
import com.tabulaw.client.ui.Position;
import com.tabulaw.common.msg.Msg;

/**
 * MsgPopup - UI widget designed to display one or more {@link Msg}s.
 * @see Msg
 * @author jpk
 */
public class MsgPopup extends PopupPanel implements IMsgOperator {

	private static final Position DEFAULT_POSITION = Position.BOTTOM;

	/**
	 * The non-<code>null</code> reference ui widget.
	 */
	private Widget refWidget;
	
	/**
	 * Pixel offset top and left for displaying the popup relative to the ref widget.
	 */
	private int offsetTop = 0, offsetLeft = 0;

	private int duration = -1;

	private Position position = DEFAULT_POSITION;

	/**
	 * Used to schedule hiding of the popup when a duration is specified for
	 * showing the popup.
	 */
	private PopupHideTimer hideTimer;

	private final MsgPanel msgPanel = new MsgPanel();

	/**
	 * Constructor
	 */
	public MsgPopup() {
		super(false, false);
		setWidget(msgPanel);
	}

	/**
	 * Constructor
	 * @param refWidget
	 */
	public MsgPopup(Widget refWidget) {
		this();
		setRefWidget(refWidget);
	}

	/**
	 * @return The number of contained messages.
	 */
	public int getNumMsgs() {
		return msgPanel.size();
	}

	/**
	 * @return the reference element
	 */
	public Widget getRefWidget() {
		return refWidget;
	}

	/**
	 * Sets the reference element.
	 * @param refWidget the reference element. Can't be <code>null</code>.
	 */
	public void setRefWidget(Widget refWidget) {
		if(refWidget == null) throw new IllegalArgumentException("Null ref widget");
		this.refWidget = refWidget;
	}
	
	/**
	 * Set the display offsets relative to the ref widget.
	 * @param top pixels
	 * @param left pixels
	 */
	public void setRefWidgetOffset(int top, int left) {
		this.offsetTop = top;
		this.offsetLeft = left;
	}

	@Override
	public void addMsg(Msg msg, Integer classifier) {
		msgPanel.addMsg(msg, classifier);
	}

	@Override
	public void addMsgs(Iterable<Msg> msgs, Integer classifier) {
		msgPanel.addMsgs(msgs, classifier);
	}

	@Override
	public void removeMsg(Msg msg) {
		msgPanel.removeMsg(msg);
	}

	@Override
	public void removeMsgs(Iterable<Msg> msgs) {
		msgPanel.removeMsgs(msgs);
	}

	@Override
	public void removeMsgs(int classifier) {
		msgPanel.removeMsgs(classifier);
	}

	@Override
	public void removeUnclassifiedMsgs() {
		msgPanel.removeUnclassifiedMsgs();
	}

	@Override
	public void showMsgs(Position desiredPosition, int milliDuration, boolean showMsgLevelImages) {
		setPosition(desiredPosition);
		setDuration(milliDuration);
		msgPanel.setShowMsgLevelImages(showMsgLevelImages);
		showMsgs(true);
	}

	void setDuration(int milliseconds) {
		this.duration = milliseconds;
	}

	void setPosition(Position position) {
		this.position = position;
	}

	@Override
	public void showMsgs(boolean show) {
		if(show) {
			// don't show when there are no messages to show
			if(getNumMsgs() < 1) return;
			// make sure we aren't currently cloaked!
			if(!DOMExt.isCloaked(refWidget.getElement())) {
				setAutoHideEnabled(duration <= 0);
				assert refWidget != null;
				setPopupPositionAndShow(new PositionCallback() {

					@SuppressWarnings("synthetic-access")
					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						int left = 0, top = 0;
						final int rel = refWidget.getAbsoluteLeft();
						final int ret = refWidget.getAbsoluteTop();
						switch(position) {
							default:
							case TOP:
								// position the msg panel left-aligned and directly above the ref
								// widget
								left = rel;
								top = ret;
								break;
							case BOTTOM:
								// position the msg panel left-aligned and directly beneath the ref
								// widget
								left = rel;
								top = ret + refWidget.getOffsetHeight();
								break;
							case CENTER: {
								left = rel + (refWidget.getOffsetWidth() / 2) - (offsetWidth / 2);
								top = ret + (refWidget.getOffsetHeight() / 2) - (offsetHeight / 2);
								break;
							}
						}
						left += offsetLeft;
						top += offsetTop;
						setPopupPosition(Math.max(0, left), Math.max(0, top));
					}
				});
				if(duration > 0) {
					if(hideTimer == null) {
						hideTimer = new PopupHideTimer(this);
					}
					hideTimer.schedule(duration);
				}
			}
		}
		else {
			hide();
		}
	}

	public void clearMsgs() {
		hide();
		msgPanel.clear();
	}

	@Override
	public void onDrag(DragEvent event) {
		switch(event.dragMode) {
			case DRAGGING:
				break;
			case START:
				hide();
				break;
			case END:
				show();
				break;
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		hide();
	}
}
