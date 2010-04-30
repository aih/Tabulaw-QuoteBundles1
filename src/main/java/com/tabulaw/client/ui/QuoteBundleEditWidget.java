/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.ui;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.Poc;
import com.tabulaw.client.Resources;
import com.tabulaw.client.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.edit.EditableTextWidget;
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
	} // Styles

	/**
	 * Quote bundle header widget with edit butons.
	 * @author jpk
	 */
	static class EditHeader extends AbstractQuoteBundleWidget.Header {

		private final EditableTextWidget pName, pDesc;
		private final Image save, undo, delete, current, close;

		/**
		 * Constructor
		 */
		public EditHeader() {
			super();

			pName = new EditableTextWidget();
			pName.addStyleName(AbstractQuoteBundleWidget.Styles.NAME);
			pName.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					bundle.setName(event.getValue());
				}
			});

			pDesc = new EditableTextWidget(headerDescTextExtractor, headerDescInnerHtmlSetter);
			pDesc.addStyleName(AbstractQuoteBundleWidget.Styles.DESC);
			pDesc.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					bundle.setDescription(event.getValue());
				}
			});
			
			save = new Image(Resources.INSTANCE.save());
			save.setStyleName(Styles.SAVE);
			save.setTitle("Click to save Name and Description");
			save.setVisible(false);
			save.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// save the quote bundle
					ClientModelCache.get().persist(bundle, EditHeader.this);
					// server side
					ClientModelCache.get().updateBundleProps(bundle);

					save.setVisible(false);
					undo.setVisible(false);
				}
			});

			undo = new Image(Resources.INSTANCE.undo());
			undo.setStyleName(Styles.UNDO);
			undo.setTitle("Click to revert Name and Description");
			undo.setVisible(false);
			undo.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// revert
					pName.revert();
					pDesc.revert(); 
					save.setVisible(false);
					undo.setVisible(false);
				}
			});

			delete = new Image(Resources.INSTANCE.delete());
			delete.setTitle("Delete Quote Bundle..");
			delete.setStyleName(Styles.DELETE);
			delete.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(Window.confirm("Completely delete " + bundle
							+ "?\nNote: Any and all contained quotes will be deleted.")) {

						// client side
						List<Quote> quotes = bundle.getQuotes();
						if(quotes != null) {
							for(Quote q : bundle.getQuotes()) {
								ClientModelCache.get().remove(q.getModelKey(), Poc.getPortal());
							}
						}
						ClientModelCache.get().remove(bundle.getModelKey(), Poc.getPortal());

						// server side
						ClientModelCache.get().deleteBundle(bundle.getId(), true);
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
						// we need to notify other views of the current quote bundle change
						// and we do it by firing a model change event
						fireEvent(new ModelChangeEvent(ModelChangeOp.UPDATED, bundle, null));
					}
				}
			});

			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(Styles.BUTTONS);
			buttons.add(save);
			buttons.add(undo);
			buttons.add(delete);
			buttons.add(current);
			buttons.add(close);

			header.add(pName);
			header.add(pDesc);
			header.insert(buttons, 0);

			pName.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					save.setVisible(true);
					undo.setVisible(true);
				}
			});
			
			pDesc.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					save.setVisible(true);
					undo.setVisible(true);
				}
			});
		}

		@Override
		public void setModel(QuoteBundle mQuoteBundle) {
			super.setModel(mQuoteBundle);
			String name = bundle.getName();
			String desc = bundle.getDescription();
			pName.setText(name == null ? "" : name);
			pDesc.setHTML(headerDescInnerHtmlSetter.convert(desc));
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

	@Override
	protected QuoteEditWidget addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
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
}
