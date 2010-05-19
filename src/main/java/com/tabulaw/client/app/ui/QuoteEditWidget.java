/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.common.model.Quote;

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

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 */
	public QuoteEditWidget(BundleEditWidget parentQuoteBundleWidget) {
		super(parentQuoteBundleWidget);
	}
}
