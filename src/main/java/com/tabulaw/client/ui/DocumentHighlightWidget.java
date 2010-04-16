/**
 * The Logic Lab
 * @author jpk
 * @since Feb 20, 2010
 */
package com.tabulaw.client.ui;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.Poc;
import com.tabulaw.client.model.MarkOverlay;
import com.tabulaw.client.model.PocModelCache;
import com.tabulaw.client.ui.DocumentViewer.ViewMode;
import com.tabulaw.common.model.PocModelFactory;
import com.tll.client.DOMExt;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IViewChangeHandler;
import com.tll.client.mvc.view.ViewChangeEvent;
import com.tll.common.data.ModelPayload;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.common.msg.Msg;
import com.tll.schema.PropertyType;

/**
 * Displays a document on the left and quote bundle on the right separated by a
 * split panel. This widget auto-creates quotes under the displayed quote bundle
 * from user made text selections.
 * <p>
 * Text selections/quotes are serialized and persisted and therefore any
 * existing quotes in the bundle are re-displayed upon widget load.
 * @author jpk
 */
public class DocumentHighlightWidget extends AbstractModelChangeAwareWidget implements ITextSelectHandler, IViewChangeHandler, ValueChangeHandler<ViewMode> {

	class DocQuoteDragHandler extends LoggingDragHandler {

		public DocQuoteDragHandler() {
			super("Doc Quote");
		}

		@Override
		public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			super.onPreviewDragEnd(event);

			// insert the quote text at the cursor location of the doc being edited
			QuoteDocWidget qw = (QuoteDocWidget) event.getContext().draggable;
			String quoteText = qw.getModel().asString("quote");
			wDocViewer.getDocEditWidget().getFormatter().insertHTML(quoteText);

			throw new VetoDragException();
		}
	} // DocQuoteDragHandler

	private final DocumentViewer wDocViewer = new DocumentViewer();

	private HandlerRegistration hrViewMode;

	private final QuoteBundleDocWidget wDocQuoteBundle;

	private final HorizontalSplitPanel hsp = new HorizontalSplitPanel();

	/**
	 * Facilitate drag/drop ops.
	 */
	private final AbsolutePanel boundaryPanel;

	private final PickupDragController quoteController;

	private final DocQuoteDragHandler quoteHandler;

	private ModelKey crntQbKey;

	/**
	 * Constructor
	 */
	public DocumentHighlightWidget() {
		super();

		boundaryPanel = new AbsolutePanel();

		quoteController = new PickupDragController(boundaryPanel, false);
		quoteController.setBehaviorMultipleSelection(false);
		quoteController.setBehaviorDragStartSensitivity(2);
		quoteHandler = new DocQuoteDragHandler();
		quoteController.addDragHandler(quoteHandler);

		SimpleDropController quoteDropController = new SimpleDropController(wDocViewer);
		quoteController.registerDropController(quoteDropController);

		wDocQuoteBundle = new QuoteBundleDocWidget();

		hsp.add(wDocViewer);
		hsp.add(wDocQuoteBundle);
		boundaryPanel.add(hsp);

		initWidget(boundaryPanel);
		// initWidget(hsp);
	}

	public Widget[] getNavColWidgets() {
		return wDocViewer.getNavColWidgets();
	}

	@Override
	public void onTextSelect(TextSelectEvent event) {
		final MarkOverlay mark = event.getMark();
		String serializedMark = mark.serialize();

		// create the quote
		Model quote = PocModelFactory.get().buildQuote(mark.getText(), wDocViewer.getModel(), serializedMark);
		// server-side persist
		Poc.getUserDataService().addQuoteToBundle(wDocQuoteBundle.getModel().getId(), quote,
				new AsyncCallback<ModelPayload>() {

					@Override
					public void onSuccess(ModelPayload result) {
						if(result.hasErrors()) {
							List<Msg> msgs = result.getStatus().getMsgs();
							Notifier.get().post(msgs);
						}
						else {
							Model persistedQuote = result.getModel();
							
							String serializedMark2 = persistedQuote.asString("serializedMark");
							MarkOverlay mark3 = MarkOverlay.deserialize(wDocViewer.getDocBody(), serializedMark2);
							mark3.highlight();
							
							// HACK: overwrite the persisted doc ref maintain model integrity
							// this is due to null caseRef properties coming back in the marshaled model
							// and it should be there and it trips up the client code as it uses this property 
							// to determine the doc type (i.e. caseref or contract (no case))
							persistedQuote.setProperty("document", wDocViewer.getModel(), PropertyType.RELATED_ONE);
							
							// cache, show and highlight
							persistedQuote.setProperty("mark", mark, PropertyType.OBJECT);
							wDocQuoteBundle.addQuote(persistedQuote, true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						String emsg = "Failed to persist Quote Bundle.";
						Log.error(emsg, caught);
						Notifier.get().error(emsg);
					}
				});
	}

	/**
	 * Fetches the current quote bundle updating the state of both document
	 * highlighting and the quote bundle display <em>only</em> if it is different
	 * than what is current.
	 * @return <code>true</code> if the current quote bundle was changed
	 */
	private boolean maybeSetCurrentQuoteBundle() {
		Model mQb = Poc.getCurrentQuoteBundle();
		if(mQb == null) {
			throw new IllegalStateException("No current qb set");
			/*
			assert crntQbKey == null;
			// auto-create a new quote bundle
			Model mDoc = wDocViewer.getModel();
			Log.debug("Auto-creating quote bundle for doc: " + mDoc);
			String qbName = mDoc.asString("title");
			String qbDesc = "Quote Bundle for " + qbName;
			mQb = PocModelFactory.get().buildQuoteBundle(qbName, qbDesc);
			mQb.setId(PocModelCache.get().getNextId(PocEntityType.QUOTE_BUNDLE));

			Poc.setCurrentQuoteBundle(mQb);
			// fire model change event
			PocModelCache.get().persist(mQb, this);
			*/
		}
		if(crntQbKey == null || !crntQbKey.equals(mQb.getKey())) {
			if(crntQbKey != null) {
				wDocQuoteBundle.clearQuotesFromUi();
			}
			if(Log.isDebugEnabled()) {
				String from = wDocQuoteBundle.getModel() == null ? "-empty-" : wDocQuoteBundle.getModel().descriptor();
				String to = mQb.descriptor();
				Log.debug("maybeSetCurrentQuoteBundle() - Re-setting model from: " + from + " to " + to);
			}
			String docId = wDocViewer.getModel().getId();
			assert docId != null;
			wDocQuoteBundle.init(docId, wDocViewer.getDocBody());
			wDocQuoteBundle.setModel(mQb);
			crntQbKey = mQb.getKey();
			return true;
		}
		return false;
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		if(!maybeSetCurrentQuoteBundle()) {
			ModelChangeOp op = event.getChangeOp();
			Model m = event.getModel();
			if(op == ModelChangeOp.UPDATED && m.getKey().equals(crntQbKey)) {
				wDocQuoteBundle.sync(m);
			}
		}
	}

	public void setDocument(Model mDoc) {
		String frameId = wDocViewer.getFrameId();
		if(frameId != null) {
			TextSelectApi.shutdown(frameId);
		}

		// update doc viewer with doc
		wDocViewer.setModel(mDoc);

		TextSelectApi.init(wDocViewer.getFrameId());
		
		if(Poc.getCurrentQuoteBundle() == null) {
			String userId = PocModelCache.get().getUser().getId();
			Log.debug("Auto-creating quote bundle for doc: " + mDoc);
			String qbName = mDoc.asString("title");
			String qbDesc = "Quote Bundle for " + qbName;
			final Model mQb = PocModelFactory.get().buildQuoteBundle(qbName, qbDesc);
			Poc.getUserDataService().addBundleForUser(userId, mQb, new AsyncCallback<ModelPayload>() {

				@Override
				public void onSuccess(ModelPayload result) {
					if(result.hasErrors()) {
						List<Msg> msgs = result.getStatus().getMsgs();
						Notifier.get().post(msgs);
					}
					else {
						Model persistedQb = result.getModel();
						Poc.setCurrentQuoteBundle(persistedQb);
						// fire model change event from the portal to ensure this view sees
						// the ensuing model change event
						// which will then trigger maybeSetCurrentQuoteBundle()..
						PocModelCache.get().persist(persistedQb, Poc.getPortal());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					String emsg = "Unable to persist auto-created Quote Bundle";
					Log.error(emsg, caught);
					Notifier.get().error(emsg);
				}
			});
		}
		else {
			// grab the current quote bundle
			maybeSetCurrentQuoteBundle();
		}
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

		hrViewMode = wDocViewer.addValueChangeHandler(this);

		ViewManager.get().addViewChangeHandler(this);
	}

	@Override
	protected void onUnload() {
		String frameId = wDocViewer.getFrameId();
		if(frameId != null) {
			TextSelectApi.shutdown(frameId);
		}

		ViewManager.get().removeViewChangeHandler(this);

		hrViewMode.removeHandler();

		super.onUnload();
	}

	@Override
	public void onViewChange(ViewChangeEvent event) {
		if(DOMExt.isCloaked(getElement())) {
			// register to receive text select events
			TextSelectApi.get().removeTextSelectHandler(this);
		}
		else {
			// register to receive text select events
			TextSelectApi.get().addTextSelectHandler(this);
		}
	}
}
