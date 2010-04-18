/**
 * The Logic Lab
 * @author jpk Dec 11, 2007
 */
package com.tll.client.ui.option;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.tll.client.ui.PopupHideTimer;

/**
 * OptionsPopup - A context menu popup widget that pops up at mouse click
 * locations.
 * @author jpk
 */
public class OptionsPopup extends PopupPanel implements MouseDownHandler, MouseOverHandler, MouseOutHandler,
 IOptionHandler {

	/**
	 * The default timer duration to show the popup.
	 */
	public static final int DFLT_DURATION = -1;

	/**
	 * The default x offset in pixels.
	 */
	public static final int DFLT_OFST_X = 13;

	/**
	 * The default y offset in pixels.
	 */
	public static final int DFLT_OFST_Y = -5;

	protected final OptionsPanel<Option> optionsPanel = new OptionsPanel<Option>();

	private final PopupHideTimer timer;

	/**
	 * The show duration in mili-seconds.
	 */
	private final int duration;

	/**
	 * The pixel offset from point of click to show the popup.
	 */
	private final int offsetX, offsetY;

	/**
	 * Constructor
	 */
	public OptionsPopup() {
		this(DFLT_DURATION, DFLT_OFST_X, DFLT_OFST_Y);
	}

	/**
	 * Constructor
	 * @param duration The duration in mili-seconds. <code>-1</code> indicates
	 *        indefinite.
	 */
	public OptionsPopup(int duration) {
		this(duration, DFLT_OFST_X, DFLT_OFST_Y);
	}

	/**
	 * Constructor
	 * @param duration The duration in mili-seconds. <code>-1</code> indicates
	 *        indefinite.
	 * @param offsetX the pixel X offset from which to show the popup at point of
	 *        click
	 * @param offsetY the pixel Y offset from which to show the popup at point of
	 *        click
	 */
	public OptionsPopup(int duration, int offsetX, int offsetY) {
		super(true, false);
		timer = duration == -1 ? null : new PopupHideTimer(this);
		this.duration = duration;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		optionsPanel.addOptionHandler(this);
		setWidget(optionsPanel);
	}

	public void setOptions(Option[] options) {
		optionsPanel.setOptions(options);
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		if(!event.isCanceled()) {
			switch(event.getTypeInt()) {
				case Event.ONKEYDOWN: {
					//Log.debug("OptionsPopup.onPreviewNativeEvent: " + event.toDebugString());
					switch(event.getNativeEvent().getKeyCode()) {
						case KeyCodes.KEY_UP:
						case KeyCodes.KEY_DOWN:
						case KeyCodes.KEY_ENTER:
							DomEvent.fireNativeEvent(event.getNativeEvent(), optionsPanel);
							event.cancel();
							break;
						case KeyCodes.KEY_ESCAPE:
							if(timer != null) timer.cancel();
							hide();
							// event.cancel();
							break;
					}
				}
			}
		}
	}

	/**
	 * Show the popup at the given coordinates.
	 * @param x the x coord w/o offset
	 * @param y the y coord w/o offset
	 */
	protected final void showAt(final int x, final int y) {
		if(timer != null) timer.cancel();
		setPopupPositionAndShow(new PositionCallback() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				setPopupPosition(x + offsetX, y + offsetY);
			}
		});
		if(timer != null) timer.schedule(duration);
	}

	public void onMouseDown(final MouseDownEvent event) {
		showAt(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		//Log.debug("OptionsPopup.onMouseOut: " + event.toDebugString());
		if(timer != null) timer.schedule(duration);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		//Log.debug("OptionsPopup.onMouseOver: " + event.toDebugString());
		if(timer != null) timer.cancel();
	}

	@Override
	public void onOptionEvent(OptionEvent event) {
		if(timer != null) timer.cancel();
		switch(event.getOptionEventType()) {
			case SELECTED:
				hide();
				break;
		}
	}
}
