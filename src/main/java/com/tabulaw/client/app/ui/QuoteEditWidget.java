/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.Window;
import com.tabulaw.common.model.Quote;

/**
 * Widget holding quote data for editing eligible for drag and drop ops.
 * @author jpk
 */
public class QuoteEditWidget extends AbstractQuoteWidget<QuoteBundleEditWidget> {

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param mQuote
	 */
	public QuoteEditWidget(QuoteBundleEditWidget parentQuoteBundleWidget, Quote mQuote) {
		super(parentQuoteBundleWidget, mQuote);
	}

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 */
	public QuoteEditWidget(QuoteBundleEditWidget parentQuoteBundleWidget) {
		super(parentQuoteBundleWidget);
	}

	@Override
	protected String getXTitle() {
		return "Remove quote from bundle";
	}

	@Override
	protected boolean allowXClick() {
		return Window.confirm("Remove " + getModel().descriptor() + " from this Quote Bundle?");
	}

	@Override
	protected void handleXClick() {
		parentQuoteBundleWidget.removeQuote(quote, true, false);
	}
}
