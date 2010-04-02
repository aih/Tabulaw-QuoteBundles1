/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.client.view;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.client.mvc.view.ViewClass;
import com.tll.client.mvc.view.ViewOptions;
import com.tll.tabulaw.client.ui.DocSuggestWidget;
import com.tll.tabulaw.client.ui.DocUploadWidget;
import com.tll.tabulaw.client.ui.DocumentsListingWidget;


/**
 * View showing the existing documents.
 * @author jpk
 */
public class DocumentsView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		/**
		 * Keep this view in the dom (for now) so se don't loose the form.
		 */
		private static final ViewOptions VIEW_OPTIONS = new ViewOptions(false, false, false, false, false, true);
		
		@Override
		public String getName() {
			return "Documents";
		}

		@Override
		public DocumentsView newView() {
			return new DocumentsView();
		}

		@Override
		public ViewOptions getViewOptions() {
			return VIEW_OPTIONS;
		}
	}
	
	private final DocSuggestWidget docSuggest = new DocSuggestWidget();
	
	private final DocUploadWidget docUpload = new DocUploadWidget();
	
	private final DocumentsListingWidget docListing = new DocumentsListingWidget();
	
	private HandlerRegistration hr;
	
	@Override
	public String getLongViewName() {
		return "Documents";
	}

	@Override
	public ViewClass getViewClass() {
		return klas;
	}

	@Override
	public Widget[] getNavColWidgets() {
		return null;
	}

	@Override
	protected void doInitialization(StaticViewInitializer initializer) {
		addWidget(docSuggest);
		addWidget(docUpload);
		addWidget(docListing);
		
		// this is necessary since the docListing widget won't see the model change
		// event fired
		// from doc suggest widget since it exists under the *same* view which model
		// change dispatcher doesn't support
		hr = docSuggest.addModelChangeHandler(docListing);
	}

	@Override
	protected final void doRefresh() {
		//docListing.getOperator().refresh();
		docListing.loadData();
	}

	@Override
	protected final void doDestroy() {
		//docListing.getOperator().clear();
		hr.removeHandler();
	}

	@Override
	protected void handleModelChange(ModelChangeEvent event) {
		docListing.onModelChangeEvent(event);
	}
}
