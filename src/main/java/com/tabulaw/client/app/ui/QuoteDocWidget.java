package com.tabulaw.client.app.ui;

import com.tabulaw.common.model.Quote;

/**
 * Quote widgtet that is <code>Mark</code> aware.
 * @author jpk
 */
public class QuoteDocWidget extends AbstractQuoteWidget {

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param mQuote
	 */
	public QuoteDocWidget(AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget, Quote mQuote) {
		super(parentQuoteBundleWidget, mQuote);
	}

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 */
	public QuoteDocWidget(AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget) {
		super(parentQuoteBundleWidget);
	}

	@Override
	protected String getXTitle() {
		return "Permanantly delete quote";
	}

	@Override
	protected boolean allowXClick() {
		return true;
	}

	@Override
	protected void handleXClick() {
		// remove from bundle and permanantly delete the quote
		parentQuoteBundleWidget.removeQuote(quote, true, true);
	}
}