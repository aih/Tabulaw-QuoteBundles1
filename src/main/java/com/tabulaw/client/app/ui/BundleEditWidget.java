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
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

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

		private final Image delete, current, close;

		/**
		 * Constructor
		 * @param orphan is this header for an orphaned quote container instance?
		 */
		public EditHeader(boolean orphan) {
			super();

			if(!orphan) {
				delete = new Image(Resources.INSTANCE.trash());
				delete.setTitle("Remove Quote Bundle..");
				delete.setStyleName(Styles.DELETE);
				delete.addClickHandler(new ClickHandler() {
	
					@Override
					public void onClick(ClickEvent event) {
						if(Window.confirm("Remove '" + bundle.descriptor() + "'?")) {
	
							// client side (moved quote to orphaned container bundle then remove bundle)
							List<Quote> quotes = bundle.getQuotes();
							if(quotes != null) {
								QuoteBundle oqc = ClientModelCache.get().getOrphanedQuoteBundle();
								for(Quote q : bundle.getQuotes()) {
									//ClientModelCache.get().remove(q.getModelKey(), Poc.getPortal());
									oqc.addQuote(q);
								}
								ClientModelCache.get().persist(oqc, EditHeader.this);
							}
							ClientModelCache.get().remove(bundle.getModelKey(), EditHeader.this);
	
							// server side (move quotes to orphaned quotes container)
							ServerPersistApi.get().deleteBundle(bundle.getId(), false);
						}
					}
				});
				buttons.add(delete);
			}
			else {
				delete = null;
			}

			if(!orphan) {
				current = new Image(Resources.INSTANCE.documentIcon());
				current.setTitle("Set as current");
				current.setStyleName(Styles.CURRENT);
				current.addClickHandler(new ClickHandler() {
	
					@Override
					public void onClick(ClickEvent event) {
						if(ClientModelCache.get().getUserState().setCurrentQuoteBundleId(bundle.getId())) {
							Notifier.get().info("Current Quote Bundle set.");
							// we need to globally notify all views of the current quote bundle
							// change and we do it by firing a model change event
							Poc.getPortal().fireEvent(new ModelChangeEvent(current, ModelChangeOp.UPDATED, bundle, null));
						}
					}
				});
				buttons.add(current);
			}
			else {
				current = null;
			}

			close = new Image(Resources.INSTANCE.XButton());
			close.setTitle("Close");
			close.setStyleName(Styles.X);
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
			//close.setVisible(!isCurrent);
			if(isCurrent) {
				lblQb.setText("Current Quote Bundle");
				addStyleName(Styles.QB_CURRENT);
			}
			else {
				lblQb.setText("Quote Bundle");
				removeStyleName(Styles.QB_CURRENT);
			}
			if(current != null) current.setVisible(!isCurrent);
			//if(delete != null) delete.setVisible(!isCurrent);
		}

	} // EditHeader

	private final boolean orphanedQuoteContainer;

	/**
	 * Constructor
	 * @param dragController optional
	 * @param orphanedQuoteContainer
	 */
	public BundleEditWidget(PickupDragController dragController, boolean orphanedQuoteContainer) {
		super(new EditHeader(orphanedQuoteContainer));
		
		this.orphanedQuoteContainer = orphanedQuoteContainer;
		header.pName.setEditable(!orphanedQuoteContainer);
		header.pDesc.setEditable(!orphanedQuoteContainer);
		if(orphanedQuoteContainer) {
			addStyleName("orphaned");
		}

		setDragController(dragController);
	}

	public boolean isOrphanedQuoteContainer() {
		return orphanedQuoteContainer;
	}

	public void setCloseHandler(ClickHandler closeHandler) {
		header.close.addClickHandler(closeHandler);
	}

	@Override
	public QuoteEditWidget removeQuote(Quote mQuote, boolean removeFromModel, boolean persist) {
		QuoteEditWidget w = super.removeQuote(mQuote, removeFromModel, persist);
		dropAreaCheck();
		return w;
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
	protected void onLoad() {
		super.onLoad();
		header.modelStateCheck();
		dropAreaCheck();
		makeModelChangeAware();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		unmakeModelChangeAware();
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		header.modelStateCheck();
	}
}
