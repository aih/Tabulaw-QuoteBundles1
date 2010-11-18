/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.model.Quote;

/**
 * Widget holding quote data for editing eligible for drag and drop ops.
 * @author jpk
 */
public class QuoteEditWidget extends AbstractQuoteWidget<BundleEditWidget> {

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param mQuote
	 */
	public QuoteEditWidget(BundleEditWidget parentQuoteBundleWidget, Quote mQuote) {
		super(parentQuoteBundleWidget, mQuote);
	}

	@Override
	public void setParentQuoteBundleWidget(BundleEditWidget parentQuoteBundleWidget) {
		super.setParentQuoteBundleWidget(parentQuoteBundleWidget);
		boolean oc = parentQuoteBundleWidget.isOrphanedQuoteContainer();
		showDeleteButton(oc);
		showQuoteLinkButton(!oc);
		showXButton(!oc);
	}
}
