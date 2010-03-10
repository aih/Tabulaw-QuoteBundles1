/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tll.tabulaw.client.ui;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.model.PocModelStore;

/**
 * Supports editing capabilty for a quote bundle.
 * @author jpk
 */
public class QuoteBundleEditWidget extends AbstractQuoteBundleWidget<QuoteEditWidget, QuoteBundleEditWidget.EditHeader> {

	static class Styles {

		public static final String BUTTONS = "buttons";

		public static final String X = "x";

		public static final String CURRENT = "current";
	} // Styles

	/**
	 * Quote bundle header widget with edit butons.
	 * @author jpk
	 */
	static class EditHeader extends AbstractQuoteBundleWidget.Header {

		private final Image close, current;

		/**
		 * Constructor
		 */
		public EditHeader() {
			super();

			close = new Image("images/x-button.png");
			close.setStyleName(Styles.X);
			current = new Image("images/document-icon.png");
			current.setStyleName(Styles.CURRENT);
			current.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(PocModelStore.get().setCurrentQuoteBundle(mQuoteBundle, EditHeader.this)) {
						Notifier.get().info("Current Quote Bundle set.");
					}
				}
			});

			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(Styles.BUTTONS);
			buttons.add(current);
			buttons.add(close);

			header.insert(buttons, 0);
		}

		@Override
		public void setModel(Model mQuoteBundle) {
			super.setModel(mQuoteBundle);
			current.setTitle("Set as current");
			close.setTitle("Close");
		}

	} // EditHeader
	
	private final PickupDragController quoteController;

	/**
	 * Constructor
	 * @param quoteController
	 */
	public QuoteBundleEditWidget(PickupDragController quoteController) {
		super(new EditHeader());
		this.quoteController = quoteController;
	}
	
	public void setCloseHandler(ClickHandler closeHandler) {
		header.close.addClickHandler(closeHandler);
	}

	@Override
	protected QuoteEditWidget getNewQuoteWidget(Model mQuote) {
		return new QuoteEditWidget(this, mQuote);
	}
	
	/**
	 * Tests to see whether this quote is already referenced in the underlying bundle.
	 * <p>NOTE: This may safely be called when dragging to/from.
	 * @param mQuote the quote model to check
	 * @return true/false
	 */
	public boolean hasQuote(Model mQuote) {
		for(int i = 0; i < quotePanel.getWidgetCount(); i++) {
			Widget w = quotePanel.getWidget(i);
			if(w instanceof QuoteEditWidget) {
				if(PocModelStore.get().compareQuotes(mQuote, ((QuoteEditWidget) w).getModel())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public QuoteEditWidget addQuote(Model mQuote, boolean persist) {
		QuoteEditWidget qew = super.addQuote(mQuote, persist);
		if(qew != null) quoteController.makeDraggable(qew, qew.getDragHandle());
		return qew;
	}

	@Override
	public QuoteEditWidget removeQuote(Model mQuote, boolean persist, boolean deleteQuote) {
		QuoteEditWidget qew = super.removeQuote(mQuote, persist, deleteQuote);
		if(qew != null) quoteController.makeNotDraggable(qew);
		return qew;
	}
	
}
