/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.view;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.ui.DocAndBundleWidget;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.view.UnloadViewRequest;
import com.tabulaw.client.view.ViewClass;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.client.view.ViewOptions;
import com.tabulaw.model.DocKey;

/**
 * Displays a single document allowing quote/bundle editing.
 * @author jpk
 */
public class DocView extends AbstractPocView<DocViewInitializer> implements IModelChangeHandler {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		/**
		 * Document views stay in the DOM so the highlighting is not lost when the
		 * doc data is re-fetched since we are using an iframe and since we are not
		 * currently auto-saving the doc.
		 */
		private static final ViewOptions VIEW_OPTIONS = new ViewOptions(false, false, false, false, false, true);

		@Override
		public String getName() {
			return "doc";
		}

		@Override
		public DocView newView() {
			return new DocView();
		}

		@Override
		public ViewOptions getViewOptions() {
			return VIEW_OPTIONS;
		}
	}

	private final DocAndBundleWidget docWidget = new DocAndBundleWidget();

	private DocKey docKey;

	private HandlerRegistration hrModelChange;

	/**
	 * Constructor
	 */
	public DocView() {
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
		return docWidget.getNavColWidgets();
	}

	/**
	 * @return The key of the document being viewed for this view instance.
	 */
	public DocKey getDocKey() {
		return docKey;
	}

	@Override
	protected void doInitialization(DocViewInitializer initializer) {
		docKey = initializer.getDocumentKey();
		if(docKey == null) throw new IllegalArgumentException();
		docWidget.makeModelChangeAware();

		assert hrModelChange == null;
		hrModelChange = Poc.getPortal().addModelChangeHandler(this);
	}

	@Override
	protected void doDestroy() {
		super.doDestroy();

		hrModelChange.removeHandler();
		hrModelChange = null;

		docWidget.unmakeModelChangeAware();
	}

	@Override
	protected void doRefresh() {
		// fetch the mDoc
		assert docKey != null;
		docWidget.setDocKey(docKey);

		// TODO determine why setting the name is necessary
		// set the doc key name to the document's name (title)
		//docKey.setName(mDocument.getName());
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		if(event.getChangeOp() == ModelChangeOp.DELETED && event.getModelKey().equals(docKey)) {
			// gotta unload bro
			Log.debug("Unloading doc view: " + this);
			ViewManager.get().dispatch(new UnloadViewRequest(getViewKey(), true, true));
			return;
		}
	}
}
