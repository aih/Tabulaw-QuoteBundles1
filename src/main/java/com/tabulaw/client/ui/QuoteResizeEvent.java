package com.tabulaw.client.ui;

import com.google.gwt.event.logical.shared.ResizeEvent;

public class QuoteResizeEvent extends ResizeEvent {
	/*
	 * Fires when quotas internal elements have to be aligned to new quote size
	 * Created just to make constructor public
	 */
	public QuoteResizeEvent () {
		super(0,0);
	}
}
