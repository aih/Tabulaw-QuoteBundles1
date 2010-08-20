package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.QuoteEvent.QuoteType;
import com.tabulaw.model.Quote;

/**
 * Quote widgtet that is <code>Mark</code> aware.
 * @author jpk
 */
public class QuoteDocWidget extends AbstractQuoteWidget<BundleDocWidget> {

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param mQuote
	 */
	public QuoteDocWidget(BundleDocWidget parentQuoteBundleWidget, Quote mQuote) {
		super(parentQuoteBundleWidget, mQuote);

		showQuoteLinkButton(true);
		showXButton(true);

		// add quote copy icon
		Image img = new Image(Resources.INSTANCE.plus());
		img.setTitle("Copy to cursor");
		img.addStyleName("copyQuoteIcon");
		img.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// fire a quote paste event
				QuoteEvent.fireQuoteEvent(QuoteDocWidget.this, QuoteType.CURRENT_PASTE);
			}
		});
		header.insertButton(img, 0);
	}
}