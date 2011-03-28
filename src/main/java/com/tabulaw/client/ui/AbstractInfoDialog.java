package com.tabulaw.client.ui;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/*
 * Abstract class for creating dialogs which displaying different info messages
 * 
 */

public abstract class AbstractInfoDialog extends Dialog implements ClickHandler{
	private VerticalPanel dialogBoxContents = new VerticalPanel();
	private FlowPanel msgPanel = new FlowPanel();
	private HorizontalPanel buttonsPanel = new HorizontalPanel();
	public AbstractInfoDialog(String title) {
		super();
		if (title!=null) {
			setText(title);
		}
		buttonsPanel.setWidth("95%");
		buttonsPanel.addStyleName("buttons-panel");

		dialogBoxContents.add(msgPanel);
		dialogBoxContents.add(buttonsPanel);
		this.setWidget(dialogBoxContents);

	}
	
	public void addButton(Widget button) {
		buttonsPanel.add(button);
		//fisrt button is left-aligned, all other are right-aligned
		buttonsPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT);
	}
	public void addContents(Widget contentItem) {
		msgPanel.add(contentItem);
	}
	

}
