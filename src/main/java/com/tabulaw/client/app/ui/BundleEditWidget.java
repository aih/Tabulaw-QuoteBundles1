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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.util.ObjectUtil;

/**
 * Supports editing capabilty for a quote bundle.
 * @author jpk
 */
public class BundleEditWidget extends AbstractBundleWidget<BundleEditWidget, QuoteEditWidget, BundleEditWidget.EditHeader> {

	static class Styles {

		public static final String BUTTONS = "qbbuttons";

		public static final String DELETE = "delete";

		public static final String CURRENT = "current";

		public static final String X = "x";

		public static final String QB_CURRENT = "qbcurrent";
	} // Styles

	/**
	 * Quote bundle header widget with edit butons.
	 * @author jpk
	 */
	static class EditHeader extends EditableBundleHeader {

		final Image /*save, undo, */delete, current, close;

		/**
		 * Constructor
		 */
		public EditHeader() {
			super();

			delete = new Image(Resources.INSTANCE.deleteLarger());
			delete.setTitle("Remove Quote Bundle..");
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
						ServerPersistApi.get().removeBundleUserBinding(bundle.getId());
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
						Notifier.get().info("Current Quote Bundle set.");
						// we need to globally notify all views of the current quote bundle
						// change
						// and we do it by firing a model change event
						Poc.getPortal().fireEvent(new ModelChangeEvent(current, ModelChangeOp.UPDATED, bundle, null));
					}
				}
			});

			// buttons.add(save);
			// buttons.add(undo);
			buttons.add(delete);
			buttons.add(current);
			buttons.add(close);
		}

		@Override
		public void setModel(QuoteBundle bundle) {
			super.setModel(bundle);
			modelStateCheck();
		}

		/**
		 * Sets relevant state based on the current quote bundle and the current
		 * bundle model.
		 */
		void modelStateCheck() {
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

	private boolean orphanedQuoteContainer;

	/**
	 * Constructor
	 * @param dragController optional
	 */
	public BundleEditWidget(PickupDragController dragController) {
		super(new EditHeader());

		// TODO do we need to handle clean up?
		makeModelChangeAware();

		setDragController(dragController);
		dropAreaCheck();
	}

	public boolean isOrphanedQuoteContainer() {
		return orphanedQuoteContainer;
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
	// TODO consider moving to super
	public boolean hasQuote(Quote mQuote) {
		for(int i = 0; i < quotePanel.getWidgetCount(); i++) {
			Widget w = quotePanel.getWidget(i);
			if(w instanceof QuoteEditWidget) {

				// compare quotes
				Quote q1 = mQuote;
				Quote q2 = ((QuoteEditWidget) w).getModel();
				DocRef doc1 = q1.getDocument();
				DocRef doc2 = q2.getDocument();
				if(doc1.equals(doc2) && ObjectUtil.equals(q1.getQuote(), q2.getQuote())) {
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
	public void setModel(QuoteBundle bundle) {
		super.setModel(bundle);
		this.orphanedQuoteContainer = false; // TODO!!!
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		header.modelStateCheck();
	}
}
