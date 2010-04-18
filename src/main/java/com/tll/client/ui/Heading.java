package com.tll.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

/**
 * Br - HTML h[1-6] tag in widget form.
 * @author jpk
 */
public final class Heading extends Widget {

	/**
	 * Constructor
	 * @param n the heading number (1-6).
	 * @param text the heading text
	 */
	public Heading(int n, String text) {
		setElement(Document.get().createHElement(n));
		getElement().setInnerText(text);
	}
}