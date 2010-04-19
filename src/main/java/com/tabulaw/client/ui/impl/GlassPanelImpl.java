/**
 * The Logic Lab
 * @author jpk
 * @since Oct 17, 2009
 */
package com.tabulaw.client.ui.impl;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tabulaw.client.ui.GlassPanel;


/**
 * GlassPanelImpl - Deferred binding implementation of {@link GlassPanel}.
 * @author jpk
 */
public abstract class GlassPanelImpl {

	protected static int getWindowScrollHeight() {
		return Document.get().getScrollHeight();
	}

	protected static int getWindowScrollWidth() {
		return Document.get().getScrollWidth();
	}

	public abstract void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize);

	public void matchParentSize(GlassPanel glassPanel, AbsolutePanel parent) {
		glassPanel.getElement().getStyle().setProperty("bottom", "0px");
		glassPanel.getElement().getStyle().setProperty("right", "0px");
		glassPanel.setWidth(parent.getOffsetWidth() + "px");
		glassPanel.setHeight(parent.getOffsetHeight() + "px");
	}
}
