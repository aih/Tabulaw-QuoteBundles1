package com.tll.tabulaw.client.ui;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tll.client.model.IHasModel;
import com.tll.client.model.ModelChangeEvent;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.tabulaw.client.model.PocModelStore;

/**
 * Widget that displays a quote bundle.
 * @param <Q> the {@link AbstractQuoteWidget} type
 * @param <H> the quote bundle {@link Header} widget type.
 * @author jpk
 */
public abstract class AbstractQuoteBundleWidget<Q extends AbstractQuoteWidget, H extends AbstractQuoteBundleWidget.Header> extends VerticalPanel implements IHasModel {

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
	protected static class Header extends AbstractModelChangingWidget {

		protected final FlowPanel header = new FlowPanel();

		protected final HTML htmlHeader;

		protected Model mQuoteBundle;

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
		public void setModel(Model mQuoteBundle) {
			String name = mQuoteBundle.getName();
			String desc = mQuoteBundle.asString("description");
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

	protected Model mQuoteBundle;

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
	 * Responsible for creating a new quote widget.
	 * @param mQuote the quote model data
	 * @return A newly created <Q>type.
	 */
	protected abstract Q getNewQuoteWidget(Model mQuote);

	@Override
	public final Model getModel() {
		return mQuoteBundle;
	}

	/**
	 * Sets the quote bundle model clearing the ui then adding based on the
	 * provided model data.
	 * @param mQuoteBundle
	 */
	@Override
	public final void setModel(Model mQuoteBundle) {
		header.setModel(mQuoteBundle);
		clearQuotesFromUi();
		if(mQuoteBundle != null) {
			List<Model> mQuotes = mQuoteBundle.relatedMany("quotes").getModelList();
			if(mQuotes != null) {
				for(Model mQuote : mQuotes) {
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
	public Q addQuote(Model mQuote, boolean persist) {
		return addQuote(mQuote, persist, true);
	}

	/**
	 * Adds a quote.
	 * @param mQuote quote model to add
	 * @param persist save both the bundle and quote model data (firing model change events)?
	 * @param addToThisBundleModel Add the given quote model to our internal model instance?
	 * @return the added quote widget
	 */
	protected Q addQuote(Model mQuote, boolean persist, boolean addToThisBundleModel) {
		// add the quote ref to the quote bundle
		if(addToThisBundleModel && mQuoteBundle != null) mQuoteBundle.relatedMany("quotes").insert(mQuote, 0);
		if(persist) {
			// add the quote updating the bundle quote refs too
			PocModelStore.get().persist(mQuote, this);
			PocModelStore.get().persist(mQuoteBundle, this);
		}
		// add to the ui
		Q qw = getNewQuoteWidget(mQuote);
		quotePanel.add(qw);
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
	public Q removeQuote(Model mQuote, boolean persist, boolean deleteQuote) {
		int index = getQuoteWidgetIndex(mQuote.getKey());
		if(index != -1) {
			if(mQuoteBundle.relatedMany("quotes").remove(mQuote.getKey())) {
				if(persist) {
					// delete the quote updating the bundle quote refs too
					if(deleteQuote) PocModelStore.get().remove(mQuote.getKey(), this);
					PocModelStore.get().persist(mQuoteBundle, this);
				}
				Q qw = (Q) quotePanel.getWidget(index);
				quotePanel.remove(index);
				return qw;
			}
		}
		throw new IllegalStateException();
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
	 * @param quoteKey
	 * @return the index or <code>-1</code> if not found
	 */
	protected final int getQuoteWidgetIndex(ModelKey quoteKey) {
		int siz = quotePanel.getWidgetCount();
		for(int i = 0; i < siz; i++) {
			AbstractQuoteWidget qw = (AbstractQuoteWidget) quotePanel.getWidget(i);
			Model m = qw.getModel();
			if(m.getKey().equals(quoteKey)) return i;
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
	protected final void sync(Model mQuoteBundleToSyncTo) {
		List<Model> existingQuotes = mQuoteBundle.relatedMany("quotes").getModelList();
		List<Model> changedQuotes = mQuoteBundleToSyncTo.relatedMany("quotes").getModelList();
		// IMPT: remove quotes first so highlighting works against a *clean* dom!
		for(Model mexisting : existingQuotes) {
			if(Model.findInCollection(changedQuotes, mexisting.getKey()) == null) {
				// quote to remove
				removeQuote(mexisting, false, false);
			}
		}
		for(Model mchanged : changedQuotes) {
			if(Model.findInCollection(existingQuotes, mchanged.getKey()) == null) {
				// quote to add
				addQuote(mchanged, false);
			}
		}
	}
}