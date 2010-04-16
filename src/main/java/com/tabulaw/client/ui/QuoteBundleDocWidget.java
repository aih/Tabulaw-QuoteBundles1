/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.tabulaw.client.model.MarkOverlay;
import com.tll.common.model.Model;

/**
 * Quote bundle widget intended for use side by side with a document in view.
 * @author jpk
 */
public class QuoteBundleDocWidget extends AbstractQuoteBundleWidget<QuoteDocWidget, AbstractQuoteBundleWidget.Header> {

	static class DocHeader extends AbstractQuoteBundleWidget.Header {

	}

	private String docId;
	
	private JavaScriptObject domDocBodyRef;

	/**
	 * Constructor
	 */
	public QuoteBundleDocWidget() {
		super(new DocHeader());
	}

	public void init(String aDocId, JavaScriptObject aDomDocBodyRef) {
		this.docId = aDocId;
		this.domDocBodyRef = aDomDocBodyRef;
	}

	@Override
	protected QuoteDocWidget getNewQuoteWidget(Model mQuote) {
		return new QuoteDocWidget(this, mQuote);
	}

	@Override
	protected QuoteDocWidget addQuote(Model mQuote, boolean persist, boolean addToThisBundleModel) {
		QuoteDocWidget w = super.addQuote(mQuote, persist, addToThisBundleModel);

		if(docId != null && w.getModel().propertyExists("document.id")) {
			String quoteDocId = w.getModel().asString("document.id");
			if(quoteDocId.equals(docId)) {
				// highlight
				MarkOverlay mark = null;
				if(w.getModel().propertyExists("mark")) {
					mark = (MarkOverlay) w.getModel().getPropertyValue("mark");
				}
				else if(w.getModel().propertyExists("serializedMark")) {	
					String stoken = w.getModel().asString("serializedMark");
					mark = MarkOverlay.deserialize(domDocBodyRef, stoken);
				}
				
				// TODO temp HACK wrap w/ try/catch
				try {
					if(mark != null) mark.highlight();
				}
				catch(Throwable t) {
					Log.error("Unable to re-highlight quote.");
				}
			}
		}
		
		return w;
	}

	@Override
	public QuoteDocWidget removeQuote(Model mQuote, boolean persist, boolean deleteQuote) {
		QuoteDocWidget w = super.removeQuote(mQuote, persist, deleteQuote);

		// un-highlight
		if(w.getModel().propertyExists("mark")) {
			MarkOverlay mark = (MarkOverlay) w.getModel().getPropertyValue("mark");
			if(mark != null) mark.unhighlight();
		}

		return w;
	}
}
