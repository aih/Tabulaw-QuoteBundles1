package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.model.IHasModel;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.util.ObjectUtil;

/**
 * Widget that displays a quote bundle.
 * @param <B> this bundle widget type as a generic arg
 * @param <Q> the {@link AbstractQuoteWidget} type
 * @param <H> the quote bundle {@link EditableBundleHeader} widget type.
 * @author jpk
 */
public abstract class AbstractBundleWidget<B extends AbstractBundleWidget<B, Q, H>, Q extends AbstractQuoteWidget<B>, H extends EditableBundleHeader> 
extends AbstractModelChangeAwareWidget implements IHasModel<QuoteBundle> {

	/**
	 * Supports drag drop targeting.
	 * @author jpk
	 */
	static class QuotesPanel extends FlowPanel {

		public QuotesPanel() {
			super();
			setStyleName("quotes");
			// setSpacing(4);
		}
	}

	protected H header;

	protected QuotesPanel quotePanel = new QuotesPanel();

	protected QuoteBundle bundle;

	protected final FlowPanel panel = new FlowPanel();

	private PickupDragController dragController;

	/**
	 * Constructor
	 * @param headerWidget The header widget
	 */
	protected AbstractBundleWidget(H headerWidget) {
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
	public void setModel(QuoteBundle bundle) {
		header.setModel(bundle);
		clearQuotesFromUi();
		this.bundle = bundle;
		if(bundle != null) {
			List<Quote> mQuotes = bundle.getQuotes();
			if(mQuotes != null) {
				for(Quote mQuote : mQuotes) {
					addQuote(mQuote, false, false);
				}
			}
		}
	}

	/**
	 * @return Array of existing {@link AbstractQuoteWidget} currently in this
	 *         widget.
	 */
	@SuppressWarnings("unchecked")
	public final List<Q> getQuoteWidgets() {
		int siz = quotePanel.getWidgetCount();
		ArrayList<Q> list = new ArrayList<Q>(siz);
		for(int i = 0; i < siz; i++) {
			Widget w = quotePanel.getWidget(i);
			if(w instanceof AbstractQuoteWidget) {
				list.add((Q) quotePanel.getWidget(i));
			}
		}
		return list;
	}

	/**
	 * Adds a quote.
	 * @param mQuote quote model to add
	 * @param persist save both the bundle and quote model data (firing model
	 *        change events)?
	 * @param addToModel Add the given quote model to our internal model
	 *        instance?
	 * @return the added quote widget
	 */
	public Q addQuote(final Quote mQuote, boolean persist, final boolean addToModel) {
		if(mQuote == null) throw new NullPointerException();

		// add to the ui
		final Q qw = getNewQuoteWidget(mQuote);
		addQuoteWidget(qw, true, addToModel);
		if(persist) {
			// server side persist
			ServerPersistApi.get().addQuoteToBundle(bundle.getId(), mQuote, new AsyncCallback<Quote>() {
				
				@Override
				public void onSuccess(Quote newQuote) {
					newQuote.setMark(mQuote.getMark());
					qw.setModel(newQuote);
					if (addToModel) {
						int index = bundle.getQuotes().indexOf(mQuote);
						bundle.removeQuote(index);
						bundle.insertQuote(newQuote, index);
					}
					// add the quote updating the bundle quote refs too
					ClientModelCache.get().persist(bundle, AbstractBundleWidget.this);
					ClientModelCache.get().persist(newQuote, AbstractBundleWidget.this);
				}
				
				@Override
				public void onFailure(Throwable paramThrowable) {
					// do nothing
				}
			});
		} 
		return qw;
	}

	/**
	 * Removes the given quote ref from the underlying quote bundle model removing
	 * the corres. quote widget from the ui as well.
	 * @param mQuote the quote to remove
	 * @param removeFromModel 
	 * @param persist update the datastore (which will trigger a model change
	 *        event)?
	 * @return the widget of the removed quote or <code>null</code> if not present
	 */
	public Q removeQuote(final Quote mQuote, boolean removeFromModel, boolean persist) {
		if(mQuote == null) throw new NullPointerException();

		Q qw = getQuoteWidget(mQuote.getId());
		if(qw == null)
			//throw new IllegalArgumentException("No quote widget contained having quote with id: " + mQuote.getId());
			return null;

		removeQuoteWidget(qw, true, removeFromModel);

		if(persist) {
			// propagate removal
			ClientModelCache.get().persist(bundle, AbstractBundleWidget.this);

			// add removed quote to un-assigned quotes bundle and propagate
			// ONLY if we are not the orphaned quote container!
			QuoteBundle ocq = ClientModelCache.get().getOrphanedQuoteBundle();
			if(!bundle.equals(ocq)) {
				ocq.addQuote(mQuote);
				ClientModelCache.get().persist(ocq, AbstractBundleWidget.this);
				
				// server side persist (move to un-assigned bundle)
				ServerPersistApi.get().moveQuote(mQuote.getId(), bundle.getId(), ocq.getId());
			}
			else {
				ClientModelCache.get().remove(mQuote.getModelKey(), AbstractBundleWidget.this);
				
				// delete the quote
				ServerPersistApi.get().deleteQuote(mQuote.getId());
			}
		}

		return qw;
	}

	/**
	 * Adds a quote widget to this panel adding its model quote to this widget's
	 * model bundle.
	 * @param quoteWidget
	 * @param addToUi add the quote widget to the UI?
	 * @param addToModel add the quote widget's model quote to this bundle widgets model?
	 */
	private void addQuoteWidget(Q quoteWidget, boolean addToUi, boolean addToModel) {
		if(quoteWidget == null) throw new NullPointerException();
		if(addToModel) {
			if(bundle.hasQuote(quoteWidget.getModel())) throw new IllegalArgumentException("Quote already contained.");
			bundle.insertQuote(quoteWidget.getModel(), 0);
		}
		if(addToUi) {
			quotePanel.insert(quoteWidget, 0); // add to head
			makeQuoteDraggable(quoteWidget, true);
		}
	}

	/**
	 * Removes a quote widget from this panel removing its model quote from this
	 * widget's model bundle.
	 * @param quoteWidget
	 * @param removeFromUi remove the quote from the UI?
	 * @param removeFromModel remove the model quote from this widgets bundle model?
	 */
	private void removeQuoteWidget(Q quoteWidget, boolean removeFromUi, boolean removeFromModel) {
		if(quoteWidget == null) throw new NullPointerException();
		if(removeFromModel) {
			if(!bundle.removeQuote(quoteWidget.getModel())) throw new IllegalArgumentException("Quote not contained.");
		}
		if(removeFromUi) {
			if(!quotePanel.remove(quoteWidget)) throw new IllegalArgumentException("Quote widget not found.");
			makeQuoteDraggable(quoteWidget, false);
		}
	}

	private void makeQuoteDraggable(Q qw, boolean draggable) {
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
		List<Q> list = getQuoteWidgets();
		for(Q qw : list) {
			makeQuoteDraggable(qw, draggable);
		}
	}

	/**
	 * Remove all quotes from the UI with no model changes.
	 */
	public final void clearQuotesFromUi() {
		List<Q> list = getQuoteWidgets();
		for(Q qw : list) {
			quotePanel.remove(qw);
			makeQuoteDraggable(qw, false);
		}
	}

	/**
	 * Finds the quote widget containing the quote identified by the given quote
	 * id.
	 * @param quoteId
	 * @return the index or <code>-1</code> if not found
	 */
	@SuppressWarnings("unchecked")
	public final Q getQuoteWidget(String quoteId) {
		int siz = quotePanel.getWidgetCount();
		for(int i = 0; i < siz; i++) {
			Q qw = (Q) quotePanel.getWidget(i);
			Quote m = qw.getModel();
			if(m.getId().equals(quoteId)) return qw;
		}
		return null;
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

	/**
	 * Compares the given quote bundle model against the one held currently adding
	 * quotes that don't exist and removing those that do but not in the one
	 * given. No model change event is fired.
	 * @param bundleToSyncTo
	 */
	public final void sync(QuoteBundle bundleToSyncTo) {
		header.setModel(bundleToSyncTo);

		// wrap in new lists to avoid concurrent mod exception!
		List<Quote> existingQuotes = new ArrayList<Quote>(bundle.getQuotes());
		List<Quote> changedQuotes = new ArrayList<Quote>(bundleToSyncTo.getQuotes());

		// IMPT: remove quotes first so highlighting works against a *clean* dom!
		for(Quote mexisting : existingQuotes) {
			if(!changedQuotes.contains(mexisting)) {
				// quote to remove
				removeQuote(mexisting, true, false);
			}
		}

		for(Quote mchanged : changedQuotes) {
			if(!existingQuotes.contains(mchanged)) {
				// quote to add
				addQuote(mchanged, false, true);
			}
		}
	}
}