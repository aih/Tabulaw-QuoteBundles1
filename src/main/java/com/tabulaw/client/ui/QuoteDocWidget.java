package com.tabulaw.client.ui;

import com.tll.common.model.Model;

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
	public QuoteDocWidget(AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget, Model mQuote) {
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
		parentQuoteBundleWidget.removeQuote(mQuote, true, true);
		
	}
}