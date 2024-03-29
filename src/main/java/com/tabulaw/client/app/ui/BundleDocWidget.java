/**
 * 
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.model.Quote;


/**
 * Quote bundle widget intended for use side by side with a document in view.
 * @author jpk
 */
public class BundleDocWidget extends AbstractBundleWidget<BundleDocWidget, QuoteDocWidget, EditableBundleHeader> {

	//private String docId;

	//private JavaScriptObject domDocBodyRef;

	private final IQuoteHandler quoteEventHandler;
	private final HasResizeHandlers resizeHandlerManager;

	private Map<QuoteDocWidget, HandlerRegistration> hrQuoteEventBindings;

	private final DockLayoutPanel panel = new DockLayoutPanel(Unit.PX); 

	
	/**
	 * Constructor
	 * @param aQuoteEventHandler optional quote event handler
	 */
	public BundleDocWidget(IQuoteHandler aQuoteEventHandler, HasResizeHandlers resizeHandlerManager) {
		super();
		this.header = new EditableBundleHeader();
		panel.setStyleName("qbundle");
		panel.addNorth(header,90);
		panel.add(quotePanel);
		initWidget(panel);
		
		this.quoteEventHandler = aQuoteEventHandler;
		this.resizeHandlerManager=resizeHandlerManager;
		
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
		if (resizeHandlerManager != null) {
			resizeHandlerManager.addResizeHandler(w);
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
		if(w != null) {
	
			// un-highlight
			MarkOverlay mark = (MarkOverlay) w.getModel().getMark();
			if(mark != null) mark.unhighlight();
	
			// un-bind quote event registration
			if(hrQuoteEventBindings != null) hrQuoteEventBindings.remove(w);
		}
		return w;
	}
}
