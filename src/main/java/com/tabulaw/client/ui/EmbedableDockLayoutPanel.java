package com.tabulaw.client.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class EmbedableDockLayoutPanel extends DockLayoutPanel {

	public EmbedableDockLayoutPanel(Unit unit) {
		super(unit);
		//to allow put panel to containers without layout
		getElement().getStyle().setPosition(Position.ABSOLUTE);
	}
	public EmbedableDockLayoutPanel(Unit unit, int top) {
		this(unit);
		getElement().getStyle().setTop(top, unit);
		getElement().getStyle().setBottom(0, unit);
	}

}