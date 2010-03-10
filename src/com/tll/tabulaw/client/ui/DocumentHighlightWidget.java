/**
 * The Logic Lab
 * @author jpk
 * @since Feb 20, 2010
 */
package com.tll.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.tll.client.DOMExt;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IViewChangeHandler;
import com.tll.client.mvc.view.ViewChangeEvent;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.schema.PropertyType;
import com.tll.tabulaw.client.model.MarkOverlay;
import com.tll.tabulaw.client.model.PocModelStore;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * Displays a document on the left and quote bundle on the right separated by a
 * split panel. This widget auto-creates quotes under the displayed quote bundle
 * from user made text selections.
 * <p>
 * Text selections/quotes are serialized and persisted and therefore any
 * existing quotes in the bundle are re-displayed upon widget load.
 * @author jpk
 */
public class DocumentHighlightWidget extends AbstractModelChangingWidget implements ITextSelectHandler, IViewChangeHandler {

	private final DocumentViewer wDocViewer = new DocumentViewer();

	private final QuoteBundleDocWidget wDocQuoteBundle = new QuoteBundleDocWidget();

	private final HorizontalSplitPanel hsp = new HorizontalSplitPanel();

	private ModelKey crntQbKey;

	/**
	 * Constructor
	 */
	public DocumentHighlightWidget() {
		super();
		hsp.add(wDocViewer);
		hsp.add(wDocQuoteBundle);
		initWidget(hsp);
	}

	@Override
	public void onTextSelect(TextSelectEvent event) {
		MarkOverlay mark = event.getMark();

		// create the quote
		Model quote = PocModelStore.get().create(PocEntityType.QUOTE);
		quote.setString("quote", mark.getText());
		quote.relatedOne("document").setModel(wDocViewer.getModel());
		quote.setProperty("mark", mark, PropertyType.OBJECT);

		// persist show and highlight
		wDocQuoteBundle.addQuote(quote, true);
	}

	/**
	 * Fetches the current quote bundle updating the state of both document
	 * highlighting and the quote bundle display <em>only</em> if it is different
	 * than what is current.
	 * @return <code>true</code> if the current quote bundle was changed
	 */
	private boolean maybeSetCurrentQuoteBundle() {
		Model mCrntQuoteBundle = PocModelStore.get().getCurrentQuoteBundle();
		if(crntQbKey == null || !crntQbKey.equals(mCrntQuoteBundle.getKey())) {
			if(crntQbKey != null) {
				wDocQuoteBundle.clearQuotesFromUi();
			}
			if(Log.isDebugEnabled()) {
				String from = wDocQuoteBundle.getModel() == null ? "-empty-" : wDocQuoteBundle.getModel().descriptor();
				String to = mCrntQuoteBundle.descriptor();
				Log.debug("maybeSetCurrentQuoteBundle() - Re-setting model from: " + from + " to " + to);
			}
			wDocQuoteBundle.setModel(mCrntQuoteBundle);
			crntQbKey = mCrntQuoteBundle.getKey();
			return true;
		}
		return false;
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		if(!maybeSetCurrentQuoteBundle()) {
			Model m = event.getModel();
			if(event.getChangeOp() == ModelChangeOp.UPDATED && m.getKey().equals(crntQbKey)) {
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

		// grab the current quote bundle
		maybeSetCurrentQuoteBundle();

		TextSelectApi.init(wDocViewer.getFrameId());
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		// move the splitter over to the right
		// we want to see as much of the doc as possible
		hsp.setSplitPosition("600px");

		ViewManager.get().addViewChangeHandler(this);
	}

	@Override
	protected void onUnload() {
		String frameId = wDocViewer.getFrameId();
		if(frameId != null) {
			TextSelectApi.shutdown(frameId);
		}

		ViewManager.get().removeViewChangeHandler(this);

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
