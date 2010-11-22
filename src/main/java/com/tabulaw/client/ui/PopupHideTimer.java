/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Feb 17, 2009
 */
package com.tabulaw.client.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * PopupHideTimer - Dedicated {@link Timer} for hiding {@link PopupPanel}s.
 * @author jpk
 */
public class PopupHideTimer extends Timer {

	private final PopupPanel popup;

	/**
	 * Constructor
	 * @param popup
	 */
	public PopupHideTimer(PopupPanel popup) {
		super();
		this.popup = popup;
	}

	@Override
	public void run() {
		popup.hide();
	}

}
