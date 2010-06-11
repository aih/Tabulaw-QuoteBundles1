/**
 * The Logic Lab
 * @author jpk
 * @since Feb 20, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.app.ui.DocViewer.ViewMode;
import com.tabulaw.client.app.ui.QuoteEvent.QuoteType;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.DocEvent;
import com.tabulaw.client.ui.IDocHandler;
import com.tabulaw.client.ui.LoggingDragHandler;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Displays a document on the left and quote bundle on the right separated by a
 * split panel.
 * <p>
 * This widget auto-creates quotes under the displayed quote bundle from user
 * made text selections for supported doc types.
 * <p>
 * Text selections/quotes are serialized and persisted and therefore any
 * existing quotes in the bundle are re-displayed upon widget load.
 * @author jpk
 */
public class DocAndBundleWidget extends AbstractModelChangeAwareWidget implements IDocHandler, ValueChangeHandler<ViewMode>, IQuoteHandler {

	class DocQuoteDragHandler extends LoggingDragHandler {

		public DocQuoteDragHandler() {
			super("Doc Quote");
		}

		@Override
		public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			super.onPreviewDragEnd(event);

			// insert the quote text at the cursor location of the doc being edited
			try {
				QuoteDocWidget qw = (QuoteDocWidget) event.getContext().draggable;
				String quoteText = qw.getModel().getQuote();
				wDocViewer.getDocEditWidget().getFormatter().insertHTML(quoteText);
			}
			catch(Throwable t) {
				// for robustification purposes in web mode
			}

			throw new VetoDragException();
		}
	} // DocQuoteDragHandler

	private final DocViewer wDocViewer = new DocViewer();

	private HandlerRegistration hrViewMode, hrDoc;

	private final BundleDocWidget wDocQuoteBundle;

	private final HorizontalSplitPanel hsp = new HorizontalSplitPanel();

	/**
	 * Facilitate drag/drop ops.
	 */
	private final AbsolutePanel boundaryPanel;

	private final PickupDragController quoteController;

	private final DocQuoteDragHandler quoteHandler;

	private String crntQbId;

	/**
	 * Constructor
	 */
	public DocAndBundleWidget() {
		super();

		boundaryPanel = new AbsolutePanel();

		quoteController = new PickupDragController(boundaryPanel, false);
		quoteController.setBehaviorMultipleSelection(false);
		quoteController.setBehaviorDragStartSensitivity(2);
		quoteHandler = new DocQuoteDragHandler();
		quoteController.addDragHandler(quoteHandler);

		SimpleDropController quoteDropController = new SimpleDropController(wDocViewer);
		quoteController.registerDropController(quoteDropController);

		wDocQuoteBundle = new BundleDocWidget(this);

		hsp.add(wDocViewer);
		hsp.add(wDocQuoteBundle);
		boundaryPanel.add(hsp);

		initWidget(boundaryPanel);
	}

	public Widget[] getNavColWidgets() {
		return wDocViewer.getNavColWidgets();
	}

	/**
	 * Highlights or un-highlights the given quote contingent on the quote's
	 * referenced document matching that being viewed.
	 * <p>
	 * NOTE: the doc content is presumed loaded
	 * @param highlight highlight or un-highlight?
	 */
	private void highlightQuote(Quote q, boolean highlight) {
		DocRef doc = wDocViewer.getModel();
		assert doc != null;
		DocRef qdoc = q.getDocument();
		MarkOverlay mark = (MarkOverlay) q.getMark();
		if(doc.equals(qdoc)) {
			if(highlight) {
				// highlight
				if(mark == null) {
					String stoken = q.getSerializedMark();
					if(stoken != null) {
						mark = MarkOverlay.deserialize(wDocViewer.getDocBody(), stoken);
						q.setMark(mark); // cache
					}
				}
				if(mark != null) {
					try {
						mark.highlight();
					}
					catch(Throwable t) {
						Log.error("Unable to re-highlight quote: " + t.getMessage());
					}
				}
			}
			else {
				// un-highlight
				if(mark != null) {
					mark.unhighlight();
				}
			}
		}
	}

	/**
	 * Highlights or un-highlights the qoutes in the current quote bundle bound to
	 * the doc under view.
	 * <p>
	 * NOTE: the doc content is presumed loaded
	 * @param highlight highlight or un-highlight?
	 */
	private void highlightQuotes(boolean highlight) {
		DocRef doc = wDocViewer.getModel();
		assert doc != null;
		// now is when we can safely highlight
		QuoteBundle qb = wDocQuoteBundle.getModel();
		List<Quote> quotes = qb.getQuotes();
		for(Quote q : quotes) {
			highlightQuote(q, highlight);
		}
	}

	@Override
	public void onDocEvent(DocEvent event) {
		switch(event.getDocEventType()) {
			case TEXT_SELECT: {
				final MarkOverlay mark = event.getMark();

				// only add quote if a valid hightlight is possible
				try {
					mark.highlight();
				}
				catch(Throwable t) {
					Log.error("Unable to highlight quote: " + t.getMessage());
					return;
				}

				// create the quote
				String serializedMark = mark.serialize();
				Quote quote = EntityFactory.get().buildQuote(mark.getText(), wDocViewer.getModel(), serializedMark);
				quote.setMark(mark);
				// eagerly set id since EntityBase.equals() depends on it
				quote.setId(ClientModelCache.get().getNextId(EntityType.QUOTE.name()));
				wDocQuoteBundle.addQuote(quote, true, true);
				break;
			}
			case DOC_CONTENT_LOADED:
				highlightQuotes(true);
				break;
			case DOC_CONTENT_UNLOADED:
				// TODO anything? (we don't really need to un-highlight quotes)
				break;
		}
	}

	/**
	 * Fetches the current quote bundle updating the state of both document
	 * highlighting and the quote bundle display <em>only</em> if it is different
	 * than what is current.
	 * @return <code>true</code> if the current quote bundle was changed
	 */
	@SuppressWarnings("unchecked")
	private boolean maybeSetCurrentQuoteBundle() {
		QuoteBundle crntQb = ClientModelCache.get().getCurrentQuoteBundle();
		if(crntQb == null) {

			// don't auto-create if there are existing eligible bundles
			// TODO establish a priority scheme by which eligible bundles are sorted
			QuoteBundle oqb = ClientModelCache.get().getOrphanedQuoteBundle();
			List<QuoteBundle> qbs = (List<QuoteBundle>) ClientModelCache.get().getAll(EntityType.QUOTE_BUNDLE);
			for(QuoteBundle qb : qbs) {
				if(!qb.equals(oqb)) {
					// set this as the current qb
					crntQb = qb;
					Log.debug("Current buneld set to: " + crntQb);
					ClientModelCache.get().getUserState().setCurrentQuoteBundleId(qb.getId());
					// notify app of current qb change via update model change
					Poc.fireModelChangeEvent(new ModelChangeEvent(this, ModelChangeOp.UPDATED, crntQb, null));
					break;
				}
			}

			if(crntQb == null) {
				// auto-create a new quote bundle
				DocRef mDoc = wDocViewer.getModel();
				if(mDoc == null) return false;
				Log.debug("Auto-creating quote bundle for doc: " + mDoc);
				String qbName = mDoc.getTitle();
				String qbDesc = "Quote Bundle for " + qbName;
				crntQb = EntityFactory.get().buildBundle(qbName, qbDesc);
				crntQb.setId(ClientModelCache.get().getNextId(EntityType.QUOTE_BUNDLE.name()));

				// client-side persist
				ClientModelCache.get().getUserState().setCurrentQuoteBundleId(crntQb.getId());
				ClientModelCache.get().persist(crntQb, this);

				// server-side persist
				ServerPersistApi.get().addBundle(crntQb);
			}

		}

		if(crntQbId == null || !crntQbId.equals(crntQb.getId())) {
			if(crntQbId != null) {
				// un-hightlight quotes
				highlightQuotes(false);

				wDocQuoteBundle.clearQuotesFromUi();
				wDocQuoteBundle.clearQuoteEventBindings();
			}
			wDocQuoteBundle.setModel(crntQb);
			crntQbId = crntQb.getId();
			return true;
		}

		return false;
	}

	@Override
	public void onQuoteEvent(QuoteEvent event) {
		if(event.getQtype() == QuoteType.CURRENT_PASTE) {
			DocEditWidget dew = wDocViewer.getDocEditWidget();
			if(dew != null) {
				QuoteDocWidget qw = (QuoteDocWidget) event.getSource();
				Quote q = qw.getModel();
				DocRef docRef = q.getDocument();
				String htmlQuote = "\"" + q.getQuote() + "\"";

				// append citation in italics if a case doc
				if(docRef.isCaseDoc()) {
					htmlQuote += "<i> - " + docRef.getCitation() + "</i>";
				}
				dew.getFormatter().insertHTML(htmlQuote);
			}
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		boolean qbChanged = maybeSetCurrentQuoteBundle();
		ModelChangeOp op = event.getChangeOp();
		IEntity m = event.getModel();
		EntityType et = EntityType.fromString(m.getEntityType());
		if(et == EntityType.QUOTE_BUNDLE && m.getId().equals(crntQbId)) {

			// sync
			if(op == ModelChangeOp.UPDATED && !qbChanged) wDocQuoteBundle.sync((QuoteBundle) m);

			// re-apply quote highlights
			if(wDocViewer.isDocContentLoaded()) {
				highlightQuotes(true);
			}
		}
		else if(et == EntityType.QUOTE && op == ModelChangeOp.DELETED) {
			wDocQuoteBundle.removeQuote((Quote) m, true, false);
		}
	}

	public void setDocument(DocRef mDoc) {
		// update doc viewer with doc
		wDocViewer.setModel(mDoc);

		// grab the current quote bundle
		maybeSetCurrentQuoteBundle();
	}

	@Override
	public void onValueChange(ValueChangeEvent<ViewMode> event) {
		wDocQuoteBundle.setDragController(quoteController);
		wDocQuoteBundle.makeQuotesDraggable(event.getValue() == ViewMode.EDIT);
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		// move the splitter over to the right
		// we want to see as much of the doc as possible
		hsp.setSplitPosition("80%");

		assert hrViewMode == null;
		hrViewMode = wDocViewer.addValueChangeHandler(this);

		assert hrDoc == null;
		hrDoc = wDocViewer.addDocHandler(this);
	}

	@Override
	protected void onUnload() {
		assert hrDoc != null;
		hrDoc.removeHandler();
		hrDoc = null;

		assert hrViewMode != null;
		hrViewMode.removeHandler();
		hrViewMode = null;

		super.onUnload();
	}
}
