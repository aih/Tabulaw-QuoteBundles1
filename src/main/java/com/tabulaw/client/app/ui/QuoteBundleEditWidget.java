/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Supports editing capabilty for a quote bundle.
 * @author jpk
 */
public class QuoteBundleEditWidget extends AbstractQuoteBundleWidget<QuoteEditWidget, QuoteBundleEditWidget.EditHeader> {

	static class Styles {

		public static final String BUTTONS = "buttons";

		public static final String SAVE = "save";

		public static final String UNDO = "undo";

		public static final String DELETE = "delete";

		public static final String CURRENT = "current";

		public static final String X = "x";
		
		public static final String QB_CURRENT = "qbcurrent";
	} // Styles

	/**
	 * Quote bundle header widget with edit butons.
	 * @author jpk
	 */
	static class EditHeader extends AbstractQuoteBundleWidget.Header {

		private final Image /*save, undo, */delete, current, close;

		/**
		 * Constructor
		 */
		public EditHeader() {
			super();

			delete = new Image(Resources.INSTANCE.delete());
			delete.setTitle("Delete Quote Bundle..");
			delete.setStyleName(Styles.DELETE);
			delete.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(Window.confirm("Remove '" + bundle.descriptor() + "'?")) {

						// client side
						List<Quote> quotes = bundle.getQuotes();
						if(quotes != null) {
							for(Quote q : bundle.getQuotes()) {
								ClientModelCache.get().remove(q.getModelKey(), Poc.getPortal());
							}
						}
						ClientModelCache.get().remove(bundle.getModelKey(), Poc.getPortal());

						// server side
						ClientModelCache.get().removeBundleUserBinding(bundle.getId());
					}
				}
			});

			close = new Image(Resources.INSTANCE.XButton());
			close.setTitle("Close");
			close.setStyleName(Styles.X);

			current = new Image(Resources.INSTANCE.documentIcon());
			current.setTitle("Set as current");
			current.setStyleName(Styles.CURRENT);
			current.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(ClientModelCache.get().getUserState().setCurrentQuoteBundleId(bundle.getId())) {
						Notifier.get().info("Current Quote Bundle set.", 1000);
						// we need to globally notify all views of the current quote bundle change
						// and we do it by firing a model change event
						Poc.getPortal().fireEvent(new ModelChangeEvent(current, ModelChangeOp.UPDATED, bundle, null));
					}
				}
			});

			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(Styles.BUTTONS);
			//buttons.add(save);
			//buttons.add(undo);
			buttons.add(delete);
			buttons.add(current);
			buttons.add(close);

			header.insert(buttons, 0);
		}
		
		@Override
		public void setModel(QuoteBundle mQuoteBundle) {
			super.setModel(mQuoteBundle);
			currentQuoteBundleCheck();
		}

		/**
		 * Sets relevant state based on the current quote bundle.
		 */
		void currentQuoteBundleCheck() {
			QuoteBundle cqb = ClientModelCache.get().getCurrentQuoteBundle();
			boolean isCurrent = cqb != null && cqb.equals(bundle);
			current.setVisible(!isCurrent);
			delete.setVisible(!isCurrent);
			close.setVisible(!isCurrent);
			if(isCurrent) {
				lblQb.setText("Current Quote Bundle");
				addStyleName(Styles.QB_CURRENT);
			}
			else {
				lblQb.setText("Quote Bundle");
				removeStyleName(Styles.QB_CURRENT);
			}
		}

	} // EditHeader

	/**
	 * Constructor
	 * @param dragController optional
	 */
	public QuoteBundleEditWidget(PickupDragController dragController) {
		super(new EditHeader());
		
		// TODO do we need to handle clean up?
		makeModelChangeAware();
		
		setDragController(dragController);
		dropAreaCheck();
	}

	public void setCloseHandler(ClickHandler closeHandler) {
		header.close.addClickHandler(closeHandler);
	}

	@Override
	public QuoteEditWidget removeQuote(Quote mQuote, boolean persist, boolean deleteQuote) {
		QuoteEditWidget w = super.removeQuote(mQuote, persist, deleteQuote);
		dropAreaCheck();
		return w;
	}

	/**
	 * Tests to see whether this quote is already referenced in the underlying
	 * bundle.
	 * <p>
	 * NOTE: This may safely be called when dragging to/from.
	 * @param mQuote the quote model to check
	 * @return true/false
	 */
	public boolean hasQuote(Quote mQuote) {
		for(int i = 0; i < quotePanel.getWidgetCount(); i++) {
			Widget w = quotePanel.getWidget(i);
			if(w instanceof QuoteEditWidget) {
				if(ClientModelCache.get().compareQuotes(mQuote, ((QuoteEditWidget) w).getModel())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected QuoteEditWidget getNewQuoteWidget(Quote mQuote) {
		return new QuoteEditWidget(this, mQuote);
	}

	@Override
	public QuoteEditWidget addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
		QuoteEditWidget w = super.addQuote(mQuote, persist, addToThisBundleModel);
		dropAreaCheck();
		return w;
	}
	
	private void dropAreaCheck() {
		// maintain a drop area
		if(quotePanel.getWidgetCount() == 0) {
			quotePanel.getElement().getStyle().setHeight(50, Unit.PX);
		}
		else {
			quotePanel.getElement().getStyle().clearHeight();
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		header.currentQuoteBundleCheck();
	}
}
