package com.tabulaw.client.ui.edit;

import com.google.gwt.user.client.ui.Image;

public class IndicatorFieldGroupEditPanel extends FieldGroupEditPanel {

	private final Image indicator = new Image("images/ajax-loader.gif");

	public IndicatorFieldGroupEditPanel(String saveText) {
		super(saveText, null, null, null);
		pnlButtonRow.insert(indicator, 1);
		setIndicatorVisible(false);
	}
	
	public void setIndicatorVisible(boolean visible){
		indicator.setVisible(visible);
	}
}