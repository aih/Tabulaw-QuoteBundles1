/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.view.DocsView;
import com.tabulaw.client.view.IViewInitializer;
import com.tabulaw.client.view.StaticViewInitializer;

/**
 * Shows the Document listing view when clicked.
 * @author jpk
 */
public class DocsNavButton extends AbstractNavButton {

	static final IViewInitializer vi = new StaticViewInitializer(DocsView.klas);

	public DocsNavButton() {
		super("Documents", "docs", Resources.INSTANCE.documentIcon());
		setTitle("View documents");
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return vi;
	}
}
