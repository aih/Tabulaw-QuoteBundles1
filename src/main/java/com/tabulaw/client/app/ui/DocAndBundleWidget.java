/**
 * The Logic Lab
 * @author jpk
 * @since Feb 20, 2010
 */
package com.tabulaw.client.app.ui;

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
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.app.ui.DocViewer.ViewMode;
import com.tabulaw.client.app.ui.QuoteEvent.QuoteType;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.ITextSelectHandler;
import com.tabulaw.client.ui.LoggingDragHandler;
import com.tabulaw.client.ui.TextSelectApi;
import com.tabulaw.client.ui.TextSelectEvent;
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
public class DocAndBundleWidget extends AbstractModelChangeAwareWidget implements ITextSelectHandler, ValueChangeHandler<ViewMode>, IQuoteHandler {

	class DocQuoteDragHandler extends LoggingDragHandler {

		public DocQuoteDragHandler() {
			super("Doc Quote");
		}

		@Override
		public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			super.onPreviewDragEnd(event);

			// insert the quote text at the cursor location of the doc being edited
			QuoteDocWidget qw = (QuoteDocWidget) event.getContext().draggable;
			String quoteText = qw.getModel().getQuote();
			wDocViewer.getDocEditWidget().getFormatter().insertHTML(quoteText);

			throw new VetoDragException();
		}
	} // DocQuoteDragHandler

	private final DocViewer wDocViewer = new DocViewer();

	private HandlerRegistration hrViewMode;

	private final QuoteBundleDocWidget wDocQuoteBundle;

	private final HorizontalSplitPanel hsp = new HorizontalSplitPanel();

	private TextSelectApi tsapi;

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

		wDocQuoteBundle = new QuoteBundleDocWidget(this);

		hsp.add(wDocViewer);
		hsp.add(wDocQuoteBundle);
		boundaryPanel.add(hsp);

		initWidget(boundaryPanel);

		//tsapi = new TextSelectApi(this);
	}

	public Widget[] getNavColWidgets() {
		return wDocViewer.getNavColWidgets();
	}

	@Override
	public void onTextSelect(TextSelectEvent event) {
		final MarkOverlay mark = event.getMark();
		String serializedMark = mark.serialize();
		// create the quote
		Quote quote = EntityFactory.get().buildQuote(mark.getText(), wDocViewer.getModel(), serializedMark);
		// cache, show and highlight
		quote.setMark(mark);
		wDocQuoteBundle.addQuote(quote, true, true);
	}

	/**
	 * Fetches the current quote bundle updating the state of both document
	 * highlighting and the quote bundle display <em>only</em> if it is different
	 * than what is current.
	 * @return <code>true</code> if the current quote bundle was changed
	 */
	private boolean maybeSetCurrentQuoteBundle() {
		QuoteBundle crntQb = ClientModelCache.get().getCurrentQuoteBundle();
		if(crntQb == null) {

			// auto-create a new quote bundle
			DocRef mDoc = wDocViewer.getModel();
			Log.debug("Auto-creating quote bundle for doc: " + mDoc);
			String qbName = mDoc.getTitle();
			String qbDesc = "Quote Bundle for " + qbName;
			crntQb = EntityFactory.get().buildQuoteBundle(qbName, qbDesc);
			crntQb.setId(ClientModelCache.get().getNextId(EntityType.QUOTE_BUNDLE.name()));

			ClientModelCache.get().getUserState().setCurrentQuoteBundleId(crntQb.getId());

			// client-side persist
			ClientModelCache.get().persist(crntQb, this);

			// server-side persist
			ServerPersistApi.get().addBundle(crntQb);
		}

		if(crntQbId == null || !crntQbId.equals(crntQb.getId())) {
			if(crntQbId != null) {
				wDocQuoteBundle.clearQuotesFromUi();
				wDocQuoteBundle.clearQuoteEventBindings();
			}
			if(Log.isDebugEnabled()) {
				String from = wDocQuoteBundle.getModel() == null ? "-empty-" : wDocQuoteBundle.getModel().descriptor();
				String to = crntQb.descriptor();
				Log.debug("maybeSetCurrentQuoteBundle() - Re-setting model from: " + from + " to " + to);
			}
			String docId = wDocViewer.getModel().getId();
			assert docId != null;
			wDocQuoteBundle.init(docId, wDocViewer.getDocBody());
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
		if(!maybeSetCurrentQuoteBundle()) {
			ModelChangeOp op = event.getChangeOp();
			IEntity m = event.getModel();
			EntityType et = EntityType.fromString(m.getEntityType());
			if(op == ModelChangeOp.UPDATED && et == EntityType.QUOTE_BUNDLE && m.getId().equals(crntQbId)) {
				wDocQuoteBundle.sync((QuoteBundle) m);
			}
		}
	}

	public void setDocument(DocRef mDoc) {
		String frameId = wDocViewer.getFrameId();
		if(frameId != null && tsapi != null) {
			tsapi.shutdown(frameId);
		}

		// update doc viewer with doc
		wDocViewer.setModel(mDoc);

		// we allow all doc type to be highlightable
		//if(mDoc.isCaseDoc()) {
			if(tsapi == null) tsapi = new TextSelectApi(this);
			tsapi.init(wDocViewer.getFrameId());
		//}

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
	}

	@Override
	protected void onUnload() {
		assert hrViewMode != null;
		hrViewMode.removeHandler();
		hrViewMode = null;

		super.onUnload();
	}
}
