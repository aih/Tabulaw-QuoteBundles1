/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.common.model.Quote;

/**
 * Quote bundle widget intended for use side by side with a document in view.
 * @author jpk
 */
public class BundleDocWidget extends AbstractBundleWidget<BundleDocWidget, QuoteDocWidget, EditableBundleHeader> {

	//private String docId;

	//private JavaScriptObject domDocBodyRef;

	private final IQuoteHandler quoteEventHandler;

	private Map<QuoteDocWidget, HandlerRegistration> hrQuoteEventBindings;

	/**
	 * Constructor
	 * @param aQuoteEventHandler optional quote event handler
	 */
	public BundleDocWidget(IQuoteHandler aQuoteEventHandler) {
		super(new EditableBundleHeader());
		this.quoteEventHandler = aQuoteEventHandler;
	}

	/*
	public void init(String aDocId, JavaScriptObject aDomDocBodyRef) {
		this.docId = aDocId;
		this.domDocBodyRef = aDomDocBodyRef;
	}
	*/

	/**
	 * Must be called to properly un-bind any quote events that may have been
	 * registered.
	 */
	public void clearQuoteEventBindings() {
		if(hrQuoteEventBindings != null) {
			for(HandlerRegistration hr : hrQuoteEventBindings.values()) {
				hr.removeHandler();
			}
			hrQuoteEventBindings.clear();
		}
	}

	@Override
	protected QuoteDocWidget getNewQuoteWidget(Quote mQuote) {
		QuoteDocWidget w = new QuoteDocWidget(this, mQuote);
		if(quoteEventHandler != null) {
			if(hrQuoteEventBindings == null) {
				hrQuoteEventBindings = new HashMap<QuoteDocWidget, HandlerRegistration>();
			}
			hrQuoteEventBindings.put(w, w.addQuoteHandler(quoteEventHandler));
		}
		return w;
	}

	@Override
	public QuoteDocWidget addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
		QuoteDocWidget w = super.addQuote(mQuote, persist, addToThisBundleModel);
		assert mQuote == w.getModel();
		/*
		DocRef doc = mQuote.getDocument();
		String wDocId = doc == null ? null : doc.getId();
		
		if(docId != null && docId.equals(wDocId)) {
			// highlight
			MarkOverlay mark = (MarkOverlay) w.getModel().getMark();
			if(mark == null) {
				String stoken = w.getModel().getSerializedMark();
				if(stoken != null) {
					mark = MarkOverlay.deserialize(domDocBodyRef, stoken);
					w.getModel().setMark(mark); // cache
				}
			}

			if(mark != null) {
				try {
					mark.highlight();
				}
				catch(Throwable t) {
					Log.error("Unable to re-highlight quote: " + t.getMessage());
				}
			}
		}
		*/

		return w;
	}

	@Override
	public QuoteDocWidget removeQuote(Quote mQuote, boolean removeFromModel, boolean persist) {
		QuoteDocWidget w = super.removeQuote(mQuote, removeFromModel, persist);

		// un-highlight
		MarkOverlay mark = (MarkOverlay) w.getModel().getMark();
		if(mark != null) mark.unhighlight();

		// un-bind quote event registration
		if(hrQuoteEventBindings != null) hrQuoteEventBindings.remove(w);

		return w;
	}
}
