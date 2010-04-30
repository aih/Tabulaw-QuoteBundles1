/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;
import com.tabulaw.client.model.MarkOverlay;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Quote bundle widget intended for use side by side with a document in view.
 * @author jpk
 */
public class QuoteBundleDocWidget extends AbstractQuoteBundleWidget<QuoteDocWidget, AbstractQuoteBundleWidget.Header> {

	static class DocHeader extends AbstractQuoteBundleWidget.Header {
		
		private final HTML htmlName, htmlDesc;

		public DocHeader() {
			super();
			htmlName = new HTML();
			htmlName.addStyleName(AbstractQuoteBundleWidget.Styles.NAME);
			htmlDesc = new HTML();
			htmlDesc.addStyleName(AbstractQuoteBundleWidget.Styles.DESC);
			header.add(htmlName);
			header.add(htmlDesc);
		}

		@Override
		public void setModel(QuoteBundle mQuoteBundle) {
			super.setModel(mQuoteBundle);
			String name = bundle.getName();
			String desc = bundle.getDescription();
			htmlName.setText(name == null ? "" : name);
			htmlDesc.setHTML(headerDescInnerHtmlSetter.convert(desc));
		}


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
	protected QuoteDocWidget getNewQuoteWidget(Quote mQuote) {
		return new QuoteDocWidget(this, mQuote);
	}

	@Override
	protected QuoteDocWidget addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
		QuoteDocWidget w = super.addQuote(mQuote, persist, addToThisBundleModel);
		String wDocId = w.getModel().getDocument().getId();
		if(docId != null && docId.equals(wDocId)) {
			// highlight
			MarkOverlay mark = (MarkOverlay) w.getModel().getMark();
			if(mark == null) {
				String stoken = w.getModel().getSerializedMark();
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

		return w;
	}

	@Override
	public QuoteDocWidget removeQuote(Quote mQuote, boolean persist, boolean deleteQuote) {
		QuoteDocWidget w = super.removeQuote(mQuote, persist, deleteQuote);

		// un-highlight
		MarkOverlay mark = (MarkOverlay) w.getModel().getMark();
		if(mark != null) mark.unhighlight();

		return w;
	}
}
