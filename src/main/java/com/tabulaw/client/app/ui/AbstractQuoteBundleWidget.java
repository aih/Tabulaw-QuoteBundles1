package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.QuoteBundleEditWidget.Styles;
import com.tabulaw.client.convert.IConverter;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.edit.EditableTextWidget;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Widget that displays a quote bundle.
 * @param <Q> the {@link AbstractQuoteWidget} type
 * @param <H> the quote bundle {@link Header} widget type.
 * @author jpk
 */
public abstract class AbstractQuoteBundleWidget<Q extends AbstractQuoteWidget, H extends AbstractQuoteBundleWidget.Header> 
extends AbstractModelChangeAwareWidget {

	static final IConverter<String, String> headerDescTextExtractor = new IConverter<String, String>() {
		
		@Override
		public String convert(String in) throws IllegalArgumentException {
			int index = in.indexOf("</span>");
			if(index == -1) index = in.indexOf("</SPAN>");
			return in.substring(index + 7);
		}
	};

	static final IConverter<String, String> headerDescInnerHtmlSetter = new IConverter<String, String>() {
		
		@Override
		public String convert(String in) throws IllegalArgumentException {
			return "<span class=\"" + "echo" + "\">DESCRIPTION: </span>" + (in == null ? "" : in);
		}
	};

	/**
	 * Extensible quote bundle header widget.
	 * @author jpk
	 */
	protected static class Header extends Composite {

		protected final FlowPanel header = new FlowPanel();

		protected final Label lblQb;
		
		protected final EditableTextWidget pName, pDesc;
		
		protected final FlowPanel buttons = new FlowPanel(); 

		protected QuoteBundle bundle;

		/**
		 * Constructor
		 */
		public Header() {
			lblQb = new Label("Quote Bundle");
			lblQb.setStyleName("echo");

			buttons.setStyleName(Styles.BUTTONS);
			header.insert(buttons, 0);
			
			header.setStyleName("qbheader");
			header.add(lblQb);
			
			initWidget(header);

			pName = new EditableTextWidget();
			pName.addStyleName("name");
			pName.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					bundle.setName(event.getValue());
					// save the quote bundle
					ClientModelCache.get().persist(bundle, Header.this);
					// server side
					ClientModelCache.get().updateBundleProps(bundle);
				}
			});
			header.add(pName);

			pDesc = new EditableTextWidget(headerDescTextExtractor, headerDescInnerHtmlSetter);
			pDesc.addStyleName("desc");
			pDesc.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					bundle.setDescription(event.getValue());
					// save the quote bundle
					ClientModelCache.get().persist(bundle, Header.this);
					// server side
					ClientModelCache.get().updateBundleProps(bundle);
				}
			});
			header.add(pDesc);
			
			/*
			save = new Image(Resources.INSTANCE.save());
			save.setStyleName(Styles.SAVE);
			save.setTitle("Save Name and Description");
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
			undo.setTitle("Revert Name and Description");
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
			*/
		}

		/**
		 * Sets the quote bundle model updating the UI.
		 * @param bundle the quote bundle model data
		 */
		public void setModel(QuoteBundle bundle) {
			String name = bundle.getName();
			String desc = bundle.getDescription();
			pName.setText(name == null ? "" : name);
			pDesc.setHTML(headerDescInnerHtmlSetter.convert(desc));
			this.bundle = bundle;
		}

		public final Widget getDraggable() {
			return lblQb;
		}

	} // Header

	/**
	 * Supports drag drop targeting.
	 * @author jpk
	 */
	protected static class QuotesPanel extends FlowPanel {

		public QuotesPanel() {
			super();
			setStyleName("quotes");
			//setSpacing(4);
		}
	} // QuotesPanel

	protected H header;

	protected QuotesPanel quotePanel = new QuotesPanel();

	protected QuoteBundle bundle;
	
	private final FlowPanel panel = new FlowPanel();

	private PickupDragController dragController;

	/**
	 * Constructor
	 * @param headerWidget The header widget
	 */
	protected AbstractQuoteBundleWidget(H headerWidget) {
		super();
		this.header = headerWidget;
		panel.setStyleName("qbundle");
		panel.add(header);
		panel.add(quotePanel);
		initWidget(panel);
	}

	/**
	 * Set the drag controller.
	 * @param dragController
	 */
	public final void setDragController(PickupDragController dragController) {
		if(this.dragController == dragController) return;

		// already set
		if(this.dragController != null) throw new IllegalStateException();

		this.dragController = dragController;
	}

	/**
	 * Responsible for creating a new quote widget.
	 * @param mQuote the quote model data
	 * @return A newly created <Q>type.
	 */
	protected abstract Q getNewQuoteWidget(Quote mQuote);

	public final QuoteBundle getModel() {
		return bundle;
	}

	/**
	 * Sets the quote bundle model clearing the ui then adding based on the
	 * provided model data.
	 * @param bundle
	 */
	public final void setModel(QuoteBundle bundle) {
		header.setModel(bundle);
		clearQuotesFromUi();
		if(bundle != null) {
			List<Quote> mQuotes = bundle.getQuotes();
			if(mQuotes != null) {
				for(Quote mQuote : mQuotes) {
					addQuote(mQuote, false, false);
				}
			}
		}
		this.bundle = bundle;
	}

	/**
	 * @return Array of existing {@link AbstractQuoteWidget} currently in this
	 *         widget.
	 */
	public final AbstractQuoteWidget[] getQuoteWidgets() {
		int siz = quotePanel.getWidgetCount();
		AbstractQuoteWidget[] arr = new AbstractQuoteWidget[siz];
		for(int i = 0; i < siz; i++) {
			arr[i] = (AbstractQuoteWidget) quotePanel.getWidget(i);
		}
		return arr;
	}

	/**
	 * Adds a quote.
	 * @param mQuote quote model to add
	 * @param persist save both the bundle and quote model data (firing model
	 *        change events)?
	 * @param addToThisBundleModel Add the given quote model to our internal model
	 *        instance?
	 * @return the added quote widget
	 */
	public Q addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
		// add the quote ref to the quote bundle
		if(addToThisBundleModel && bundle != null) bundle.insertQuote(mQuote, 0);
		if(persist) {
			// add the quote updating the bundle quote refs too
			ClientModelCache.get().persist(mQuote, this);
			ClientModelCache.get().persist(bundle, this);
			// server side persist
			ClientModelCache.get().addQuoteToBundle(bundle.getId(), mQuote);
		}
		// add to the ui
		Q qw = getNewQuoteWidget(mQuote);
		quotePanel.add(qw);
		if(qw != null) makeQuoteDraggable(qw, true);
		return qw;
	}

	/**
	 * Removes the given quote ref from the underlying quote bundle model removing
	 * the corres. quote widget from the ui as well.
	 * @param mQuote the quote to remove
	 * @param persist update the datastore (which will trigger a model change
	 *        event)?
	 * @param deleteQuote Delete the quote in the datastore?
	 * @return the widget of the removed quote
	 */
	@SuppressWarnings("unchecked")
	public Q removeQuote(final Quote mQuote, boolean persist, final boolean deleteQuote) {
		int index = getQuoteWidgetIndex(mQuote.getId());
		if(index != -1) {
			if(bundle.removeQuote(mQuote)) {
				if(persist) {
					// delete the quote updating the bundle quote refs too
					if(deleteQuote) {
						ClientModelCache.get().remove(mQuote.getModelKey(), AbstractQuoteBundleWidget.this);
					}
					ClientModelCache.get().persist(bundle, AbstractQuoteBundleWidget.this);

					// server side persist
					ClientModelCache.get().removeQuoteFromBundle(bundle.getId(), mQuote.getId(), deleteQuote);
				}
				Q qw = (Q) quotePanel.getWidget(index);
				quotePanel.remove(index);
				makeQuoteDraggable(qw, false);
				return qw;
			}
		}
		throw new IllegalStateException();
	}

	private void makeQuoteDraggable(AbstractQuoteWidget qw, boolean draggable) {
		if(dragController == null) return;
		if(draggable) {
			if(!qw.draggable) {
				dragController.makeDraggable(qw, qw.getDragHandle());
				qw.draggable = true;
			}
		}
		else {
			if(qw.draggable) {
				dragController.makeNotDraggable(qw);
				qw.draggable = false;
			}
		}
	}

	/**
	 * Iterates through all the quote widgets making them either draggable or not
	 * draggable.
	 * @param draggable make draggable (or not)?
	 */
	public void makeQuotesDraggable(boolean draggable) {
		if(dragController == null) throw new IllegalStateException();
		AbstractQuoteWidget[] arr = getQuoteWidgets();
		for(AbstractQuoteWidget qw : arr) {
			makeQuoteDraggable(qw, draggable);
		}
	}

	/**
	 * Remove all quotes from the UI with no model changes.
	 */
	public final void clearQuotesFromUi() {
		AbstractQuoteWidget[] arr = getQuoteWidgets();
		for(AbstractQuoteWidget qw : arr) {
			removeQuote(qw.getModel(), false, false);
		}
	}

	/**
	 * Resolves the index at which the quote widget referencing the model having
	 * the given key resides.
	 * @param quoteId
	 * @return the index or <code>-1</code> if not found
	 */
	protected final int getQuoteWidgetIndex(String quoteId) {
		int siz = quotePanel.getWidgetCount();
		for(int i = 0; i < siz; i++) {
			AbstractQuoteWidget qw = (AbstractQuoteWidget) quotePanel.getWidget(i);
			Quote m = qw.getModel();
			if(m.getId().equals(quoteId)) return i;
		}
		return -1;
	}
	
	/**
	 * Compares the given quote bundle model against the one held currently adding
	 * quotes that don't exist and removing those that do but not in the one
	 * given. No model change event is fired.
	 * @param bundleToSyncTo
	 */
	public final void sync(QuoteBundle bundleToSyncTo) {
		// wrap in new lists to avoid concurrent mod exception!
		List<Quote> existingQuotes = new ArrayList<Quote>(bundle.getQuotes());
		List<Quote> changedQuotes = new ArrayList<Quote>(bundleToSyncTo.getQuotes());

		// IMPT: remove quotes first so highlighting works against a *clean* dom!
		for(Quote mexisting : existingQuotes) {
			if(!changedQuotes.contains(mexisting)) {
				// quote to remove
				removeQuote(mexisting, false, false);
			}
		}

		for(Quote mchanged : changedQuotes) {
			if(!existingQuotes.contains(mchanged)) {
				// quote to add
				addQuote(mchanged, false, true);
			}
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		// no-op
	}
}