/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 28, 2009
 */
package com.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * RpcUiHandler - Provides UI indication that an RPC call is in progress.
 * @author jpk
 */
public class RpcUiHandler implements IRpcHandler {

	private final Widget overlayWidget;

	private final GlassPanel busyPanel;

	private AbsolutePanel overlay;

	/**
	 * Constructor
	 * @param overlayWidget The widget that determines the ui overlay location and
	 *        dimensions. If <code>null</code>, an overlay is applied to the
	 *        entire browser window area.
	 */
	public RpcUiHandler(Widget overlayWidget) {
		super();
		this.overlayWidget = overlayWidget;
		this.busyPanel = new BusyPanel(false);
	}

	@Override
	public void onRpcEvent(RpcEvent event) {
		if(overlayWidget != null && !overlayWidget.isAttached()) {
			Log.warn("RpcUiHandler.onRpcEvent(): Overlay widget is not attached.");
			return;
		}
		if(RootPanel.get() != null) {
			switch(event.getType()) {
				case SENT:
					// add overlay
					if(overlayWidget != null) {
						// local overlay
						overlay = BusyPanel.createOverlay(overlayWidget);
						overlay.add(busyPanel, 0, 0);
					}
					else {
						// global overlay
						if(RootPanel.get() != null) RootPanel.get().add(busyPanel, 0, 0);
					}
					break;
				case RECEIVED:
				case ERROR:
					if(overlay != null) {
						// local overlay
						RootPanel.get().remove(overlay);
						overlay = null;
					}
					else {
						// global overlay
						RootPanel.get().remove(busyPanel);
					}
					break;
			}
		}
	}

}
