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
public class OrphanedQuoteWidget extends AbstractQuoteWidget<OrphanedQuoteContainerWidget> {

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param mQuote
	 */
	public OrphanedQuoteWidget(OrphanedQuoteContainerWidget parentQuoteBundleWidget, Quote mQuote) {
		super(parentQuoteBundleWidget, mQuote);
	}

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 */
	public OrphanedQuoteWidget(OrphanedQuoteContainerWidget parentQuoteBundleWidget) {
		super(parentQuoteBundleWidget);
	}
}
