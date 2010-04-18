/**
 * The Logic Lab
 * @author jpk Aug 28, 2007
 */
package com.tll.client.ui;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Dialog
 * @author jpk
 */
public class Dialog extends DialogBox {

	private final Focusable focusOnCloseWidget;

	private final GlassPanel glassPanel;

	/**
	 * Constructor
	 */
	public Dialog() {
		this(null, false);
	}

	/**
	 * Constructor
	 * @param focusOnCloseWidget
	 * @param showOverlay
	 */
	public Dialog(Focusable focusOnCloseWidget, boolean showOverlay) {
		super(false, true);
		this.focusOnCloseWidget = focusOnCloseWidget;
		if(showOverlay) {
			this.glassPanel = new GlassPanel(false);
			this.glassPanel.setVisible(false);
			RootPanel.get().add(glassPanel);
		}
		else {
			this.glassPanel = null;
		}
	}

	@Override
	public void onAttach() {
		super.onAttach();
		if(glassPanel != null) {
			glassPanel.setVisible(true);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(glassPanel != null) {
			glassPanel.setVisible(false);
		}
		if(focusOnCloseWidget != null) {
			DeferredCommand.addCommand(new FocusCommand(focusOnCloseWidget, true));
		}
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		if(!event.isCanceled()) {
			if(event.getTypeInt() == Event.ONKEYDOWN) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
				}

			}
		}
	}
}
