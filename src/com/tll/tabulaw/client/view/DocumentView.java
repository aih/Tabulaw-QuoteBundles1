/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.client.view;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.UnloadViewRequest;
import com.tll.client.mvc.view.ViewClass;
import com.tll.client.mvc.view.ViewOptions;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.tabulaw.client.model.PocModelStore;
import com.tll.tabulaw.client.ui.DocumentHighlightWidget;
import com.tll.tabulaw.client.ui.ModelChangeDispatcher;

/**
 * Displays a single document allowing quote/bundle editing.
 * @author jpk
 */
public class DocumentView extends AbstractPocView<DocumentViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		/**
		 * Document views stay in the DOM so the highlighting is not lost when the
		 * doc data is re-fetched since we are using an iframe and since we are not
		 * currently auto-saving the doc.
		 */
		private static final ViewOptions POC_DOC_VIEW_OPTIONS = new ViewOptions(false, false, false, false, false, true);

		@Override
		public String getName() {
			return "docview";
		}

		@Override
		public DocumentView newView() {
			return new DocumentView();
		}

		@Override
		public ViewOptions getViewOptions() {
			return POC_DOC_VIEW_OPTIONS;
		}
	}

	private final DocumentHighlightWidget docWidget = new DocumentHighlightWidget();

	private ModelKey docKey;

	/**
	 * Constructor
	 */
	public DocumentView() {
		super();
		addWidget(docWidget);
	}

	@Override
	public String getLongViewName() {
		return "Document View";
	}

	@Override
	public ViewClass getViewClass() {
		return klas;
	}

	@Override
	public Widget[] getNavColWidgets() {
		return null;
	}

	/**
	 * @return The key of the document being viewed for this view instance.
	 */
	public ModelKey getDocKey() {
		return docKey;
	}

	@Override
	protected void doInitialization(DocumentViewInitializer initializer) {
		docKey = initializer.getDocumentKey();
		if(docKey == null) throw new IllegalArgumentException();
	}

	@Override
	protected void doRefresh() {
		// fetch the mDoc
		assert docKey != null;
		Model mDocument = PocModelStore.get().get(docKey);
		docWidget.setDocument(mDocument);

		// set the doc key name to the document's title
		docKey.setName(mDocument.asString("title"));
	}

	@Override
	protected void handleModelChange(ModelChangeEvent event) {
		if(event.getChangeOp() == ModelChangeOp.DELETED && event.getModelKey().equals(docKey)) {
			// gotta unload bro
			Log.debug("Unloading doc view: " + this);
			ViewManager.get().dispatch(new UnloadViewRequest(getViewKey(), true, true));
			return;
		}
		docWidget.onModelChangeEvent(event);
	}
	
	private HandlerRegistration hrModelChange;

	@Override
	protected void onLoad() {
		super.onLoad();
		hrModelChange = addHandler(ModelChangeDispatcher.get(), ModelChangeEvent.TYPE);
	}

	@Override
	protected void onUnload() {
		hrModelChange.removeHandler();
		super.onUnload();
	}

}
