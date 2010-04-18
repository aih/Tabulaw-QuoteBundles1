package com.tll.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

/**
 * Br - HTML br tag in widget form.
 * @author jpk
 */
public final class Br extends Widget {

	public Br() {
		setElement(Document.get().createBRElement());
	}
}