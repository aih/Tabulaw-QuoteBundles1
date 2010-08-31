/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 30, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;

/**
 * Widget for admins to manage Clauses and Clause Bundles.
 * @author jopaki
 */
public class ClausesManageWidget extends AbstractModelChangeAwareWidget {

	private static ManageClausesWidgetUiBinder uiBinder = GWT.create(ManageClausesWidgetUiBinder.class);

	interface ManageClausesWidgetUiBinder extends UiBinder<AbsolutePanel, ClausesManageWidget> {
	}

	@UiField
	AbsolutePanel boundaryPanel;

	@UiField
	HorizontalPanel columns;
	
	/**
	 * Constructor
	 * @param firstName
	 */
	public ClausesManageWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
