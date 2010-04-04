/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tll.tabulaw.client.ui;

import com.tll.common.model.Model;
import com.tll.tabulaw.client.model.MarkOverlay;

/**
 * Quote bundle widget intended for use side by side with a document in view.
 * @author jpk
 */
public class QuoteBundleDocWidget extends AbstractQuoteBundleWidget<QuoteDocWidget, AbstractQuoteBundleWidget.Header> {

	static class DocHeader extends AbstractQuoteBundleWidget.Header {
		
	}
	
	/**
	 * Constructor
	 */
	public QuoteBundleDocWidget() {
		super(new DocHeader());
	}

	@Override
	protected QuoteDocWidget getNewQuoteWidget(Model mQuote) {
		return new QuoteDocWidget(this, mQuote);
	}

	@Override
	protected QuoteDocWidget addQuote(Model mQuote, boolean persist, boolean addToThisBundleModel) {
		QuoteDocWidget w = super.addQuote(mQuote, persist, addToThisBundleModel);
		// highlight
		MarkOverlay mark = (MarkOverlay) w.getModel().getPropertyValue("mark");
		if(mark != null) mark.highlight();
		return w;
	}

	@Override
	public QuoteDocWidget removeQuote(Model mQuote, boolean persist, boolean deleteQuote) {
		QuoteDocWidget w = super.removeQuote(mQuote, persist, deleteQuote);
		// un-highlight
		MarkOverlay mark = (MarkOverlay) w.getModel().getPropertyValue("mark");
		if(mark != null) mark.unhighlight();
		return w;
	}
}
