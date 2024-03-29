/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.app.view;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.view.AbstractView;
import com.tabulaw.client.view.IViewInitializer;

/**
 * AbstractPocView
 * @author jpk
 * @param <I>
 */
public abstract class AbstractPocView<I extends IViewInitializer> extends AbstractView<I> implements IPocView<I> {

	/**
	 * Constructor
	 */
	public AbstractPocView() {
		super();
	}

	@Override
	protected void doInitialization(I initializer) {
		// base impl no-op
	}

	@Override
	protected void doDestroy() {
		// base impl no-op
	}

	@Override
	public Widget[] getNavColWidgets() {
		// base impl none
		return null;
	}

	public Widget getSearchWidget() {
		// base impl none
		return null;
		
	}
	
}
