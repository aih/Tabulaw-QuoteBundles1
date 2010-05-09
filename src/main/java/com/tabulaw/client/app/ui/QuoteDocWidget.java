package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.common.model.DocRef;
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
	public QuoteDocWidget(QuoteBundleDocWidget parentQuoteBundleWidget, Quote mQuote) {
		super(parentQuoteBundleWidget, mQuote);
		
		if(mQuote.getDocument() != null && mQuote.getDocument().getCaseRef() == null) {
			// add quote copy icon
			Image img = new Image(Resources.INSTANCE.plus());
			img.setTitle("Copy to cursor");
			img.addStyleName("plus");
			img.addClickHandler(new ClickHandler() {
	
				@Override
				public void onClick(ClickEvent event) {
					// copy quote text to cursor point when doc is in edit mode
					DocRef docRef = quote.getDocument();
					final DocViewInitializer dvi = new DocViewInitializer(docRef.getModelKey());
					ViewManager.get().dispatch(new ShowViewRequest(dvi, new Command() {
	
						@Override
						public void execute() {
							// TODO goto highlight
							MarkOverlay mark = (MarkOverlay) quote.getMark();
							if(mark != null) {
								Element elm = mark.getStartNode();
								if(elm != null) {
									DOM.scrollIntoView(elm);
								}
							}
						}
					}));
				}
			});
			header.addButton(img);
		}
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