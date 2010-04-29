/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.ui;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.Resources;
import com.tabulaw.client.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
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
		
		public static final String DELETE = "delete";
		
		public static final String CURRENT = "current";
		
		public static final String X = "x";
	} // Styles

	/**
	 * Quote bundle header widget with edit butons.
	 * @author jpk
	 */
	static class EditHeader extends AbstractQuoteBundleWidget.Header {

		private final Image save, delete, current, close;

		/**
		 * Constructor
		 */
		public EditHeader() {
			super();

			save = new Image(Resources.INSTANCE.save());
			save.setStyleName(Styles.SAVE);
			save.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// save the quote bundle
					ClientModelCache.get().persist(mQuoteBundle, EditHeader.this);
					// server side
					ClientModelCache.get().updateBundleProps(mQuoteBundle);
				}
			});
			
			delete = new Image(Resources.INSTANCE.delete());
			delete.setStyleName(Styles.DELETE);
			delete.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(Window.confirm("Completely delete " + mQuoteBundle + "?\nNote: Any and all contained quotes will be deleted.")) {
						ClientModelCache.get().remove(mQuoteBundle.getModelKey(), EditHeader.this);
						// server side
						ClientModelCache.get().deleteBundle(mQuoteBundle.getId(), true);
						save.setVisible(false);
					}
				}
			});
			
			close = new Image(Resources.INSTANCE.XButton());
			close.setStyleName(Styles.X);
			
			current = new Image(Resources.INSTANCE.documentIcon());
			current.setStyleName(Styles.CURRENT);
			current.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(ClientModelCache.get().getUserState().setCurrentQuoteBundleId(mQuoteBundle.getId())) {
						Notifier.get().info("Current Quote Bundle set.", 1000);
						// we need to notify other views of the current quote bundle change
						// and we do it by firing a model change event
						fireEvent(new ModelChangeEvent(ModelChangeOp.UPDATED, mQuoteBundle, null));
					}
				}
			});

			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(Styles.BUTTONS);
			buttons.add(save);
			buttons.add(delete);
			buttons.add(current);
			buttons.add(close);

			header.insert(buttons, 0);
			
			super.pName.addValueChangeHandler(new ValueChangeHandler<String>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					save.setVisible(true);
				}
			});
		}

		@Override
		public void setModel(QuoteBundle mQuoteBundle) {
			super.setModel(mQuoteBundle);
			current.setTitle("Set as current");
			close.setTitle("Close");
		}

	} // EditHeader
	
	/**
	 * Constructor
	 * @param dragController optional
	 */
	public QuoteBundleEditWidget(PickupDragController dragController) {
		super(new EditHeader());
		setDragController(dragController);
	}
	
	public void setCloseHandler(ClickHandler closeHandler) {
		header.close.addClickHandler(closeHandler);
	}

	@Override
	protected QuoteEditWidget getNewQuoteWidget(Quote mQuote) {
		return new QuoteEditWidget(this, mQuote);
	}
	
	/**
	 * Tests to see whether this quote is already referenced in the underlying bundle.
	 * <p>NOTE: This may safely be called when dragging to/from.
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
}
