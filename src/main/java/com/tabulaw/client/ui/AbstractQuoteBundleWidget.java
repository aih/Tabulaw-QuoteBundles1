package com.tabulaw.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tabulaw.client.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Widget that displays a quote bundle.
 * @param <Q> the {@link AbstractQuoteWidget} type
 * @param <H> the quote bundle {@link Header} widget type.
 * @author jpk
 */
public abstract class AbstractQuoteBundleWidget<Q extends AbstractQuoteWidget, H extends AbstractQuoteBundleWidget.Header> extends VerticalPanel {

	static class Styles {

		/**
		 * Top-most quote bundle widget style.
		 */
		public static final String WQBUNDLE = "wqbundle";

		public static final String HEADER = "qbheader";

		public static final String HEADER_TEXT = "headertext";

		public static final String ECHO = "echo";

		public static final String NAME = "name";

		public static final String DESC = "desc";

		public static final String QUOTES = "quotes";
	} // Styles

	/**
	 * Extensible quote bundle header widget.
	 * @author jpk
	 */
	protected static class Header extends AbstractModelChangeAwareWidget {

		protected final FlowPanel header = new FlowPanel();

		protected final HTML htmlHeader;

		protected QuoteBundle mQuoteBundle;

		/**
		 * Constructor
		 */
		public Header() {
			initWidget(header);

			htmlHeader = new HTML();
			htmlHeader.setStyleName(Styles.HEADER_TEXT);

			header.setStyleName(Styles.HEADER);
			header.add(htmlHeader);
		}

		/**
		 * Sets the quote bundle model updating the UI.
		 * @param mQuoteBundle the quote bundle model data
		 */
		public void setModel(QuoteBundle mQuoteBundle) {
			String name = mQuoteBundle.getName();
			String desc = mQuoteBundle.getDescription();
			String h1 = "<p class=\"" + Styles.ECHO + "\">Quote Bundle</p>";
			String h2 = "<p class=\"" + Styles.NAME + "\">" + (name == null ? "" : name) + "</p>";
			String h3 =
					"<p class=\"" + Styles.DESC + "\"><span class=\"" + Styles.ECHO + "\">DESCRIPTION: </span>"
							+ (desc == null ? "" : desc) + "</p>";
			htmlHeader.setHTML(h1 + h2 + h3);
			this.mQuoteBundle = mQuoteBundle;
		}

		/**
		 * @return The draggable {@link HTML} widget.
		 */
		public final HTML getHtmlHeader() {
			return htmlHeader;
		}

	} // Header

	/**
	 * Supports drag drop targeting.
	 * @author jpk
	 */
	protected static class QuotesPanel extends VerticalPanelWithSpacer {

		public QuotesPanel() {
			super();
			setStyleName(AbstractQuoteBundleWidget.Styles.QUOTES);
			setSpacing(4);
		}
	} // QuotesPanel

	protected H header;

	protected QuotesPanel quotePanel = new QuotesPanel();

	protected QuoteBundle mQuoteBundle;

	private PickupDragController dragController;

	/**
	 * Constructor
	 * @param headerWidget The header widget
	 */
	protected AbstractQuoteBundleWidget(H headerWidget) {
		super();
		this.header = headerWidget;
		setStyleName(Styles.WQBUNDLE);
		add(header);
		add(quotePanel);
	}

	/**
	 * Set the drag controller.
	 * @param dragController
	 */
	protected final void setDragController(PickupDragController dragController) {
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
		return mQuoteBundle;
	}

	/**
	 * Sets the quote bundle model clearing the ui then adding based on the
	 * provided model data.
	 * @param mQuoteBundle
	 */
	public final void setModel(QuoteBundle mQuoteBundle) {
		header.setModel(mQuoteBundle);
		clearQuotesFromUi();
		if(mQuoteBundle != null) {
			List<Quote> mQuotes = mQuoteBundle.getQuotes();
			if(mQuotes != null) {
				for(Quote mQuote : mQuotes) {
					addQuote(mQuote, false, false);
				}
			}
		}
		this.mQuoteBundle = mQuoteBundle;
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
	 * Adds a quote the underlying model as well as adding a new quote widget to
	 * this widget.
	 * @param mQuote
	 * @param persist update the datastore (which will trigger a model change
	 *        event)?
	 * @return The added quote widget
	 */
	public Q addQuote(Quote mQuote, boolean persist) {
		return addQuote(mQuote, persist, true);
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
	protected Q addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
		// add the quote ref to the quote bundle
		if(addToThisBundleModel && mQuoteBundle != null) mQuoteBundle.insertQuote(mQuote, 0);
		if(persist) {
			// add the quote updating the bundle quote refs too
			ClientModelCache.get().persist(mQuote, this);
			ClientModelCache.get().persist(mQuoteBundle, this);
			// server side persist
			ClientModelCache.get().addQuoteToBundle(mQuoteBundle.getId(), mQuote);
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
			if(mQuoteBundle.removeQuote(mQuote)) {
				if(persist) {
					// delete the quote updating the bundle quote refs too
					if(deleteQuote) {
						ClientModelCache.get().remove(EntityType.QUOTE, mQuote.getId(), AbstractQuoteBundleWidget.this);
					}
					ClientModelCache.get().persist(mQuoteBundle, AbstractQuoteBundleWidget.this);
					
					// server side persist
					ClientModelCache.get().removeQuoteFromBundle(mQuoteBundle.getId(), mQuote.getId(), deleteQuote);
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

	private HandlerRegistration mcr;

	@Override
	protected void onLoad() {
		super.onLoad();
		mcr = addHandler(ModelChangeDispatcher.get(), ModelChangeEvent.TYPE);
	}

	@Override
	protected void onUnload() {
		mcr.removeHandler();
		super.onUnload();
	}

	/**
	 * Compares the given quote bundle model against the one held currently adding
	 * quotes that don't exist and removing those that do but not in the one
	 * given. No model change event is fired.
	 * @param mQuoteBundleToSyncTo
	 */
	protected final void sync(QuoteBundle mQuoteBundleToSyncTo) {
		// wrap in new lists to avoid concurrent mod exception!
		List<Quote> existingQuotes = new ArrayList<Quote>(mQuoteBundle.getQuotes());
		List<Quote> changedQuotes = new ArrayList<Quote>(mQuoteBundleToSyncTo.getQuotes());
		
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
				addQuote(mchanged, false);
			}
		}
	}
}