/**
 * The Logic Lab
 * @author jpk
 * @since May 18, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.ui.Image;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.QuoteBundleEditWidget.Styles;
import com.tabulaw.common.model.Quote;

/**
 * Dedicated {@link QuoteBundleEditWidget} exclusively housing orphaned quotes
 * in an ad-hoc bundle.
 * @author jpk
 */
public class OrphanedQuoteContainerWidget extends AbstractQuoteBundleWidget<OrphanedQuoteContainerWidget, OrphanedQuoteWidget, OrphanedQuoteContainerWidget.Header> {

	/**
	 * Quote bundle header widget with edit butons.
	 * @author jpk
	 */
	static class Header extends AbstractQuoteBundleWidget.Header {

		private final Image close;

		/**
		 * Constructor
		 */
		public Header() {
			super();

			close = new Image(Resources.INSTANCE.XButton());
			close.setTitle("Close");
			close.setStyleName(Styles.X);

			buttons.add(close);
			
			pName.setEditable(false);
			pDesc.setEditable(false);
		}
	} // Header

	/**
	 * Constructor
	 */
	public OrphanedQuoteContainerWidget() {
		super(new Header());
	}

	@Override
	protected OrphanedQuoteWidget getNewQuoteWidget(Quote mQuote) {
		return new OrphanedQuoteWidget(this, mQuote);
	}	
}
