/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 21, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extension of vertical panel that permanently keeps a label child widget to
 * avoid css collapse of the panel when no children exist.
 * @author jpk
 */
public class VerticalPanelWithSpacer extends VerticalPanel {

	static class Styles {

		/**
		 * Primary style name of the permanent child widget acting as the spacer. At
		 * a minimum, this style should a height since the point is to avoid css
		 * collapse.
		 */
		public static final String SPACER = "spacer";
	}

	public VerticalPanelWithSpacer() {
		super();
		Label spacerLabel = new Label("");
		spacerLabel.setStylePrimaryName(Styles.SPACER);
		super.add(spacerLabel);
	}

	@Override
	public void add(Widget w) {
		super.insert(w, super.getWidgetCount() - 1);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		if(beforeIndex == super.getWidgetCount()) {
			beforeIndex--;
		}
		super.insert(w, beforeIndex);
	}

	@Override
	public Widget getWidget(int index) {
		if(index == super.getWidgetCount()) throw new IllegalArgumentException();
		return super.getWidget(index);
	}

	@Override
	public int getWidgetCount() {
		return super.getWidgetCount() - 1;
	}

	@Override
	public boolean remove(int index) {
		if(index == super.getWidgetCount()) throw new IllegalArgumentException();
		return super.remove(index);
	}

	@Override
	public void clear() {
		for(int i = 0; i < getWidgetCount(); i++) {
			super.remove(i);
		}
	}

}
