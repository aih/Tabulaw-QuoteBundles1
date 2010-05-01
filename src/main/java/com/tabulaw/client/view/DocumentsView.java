/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.mvc.view.ViewClass;
import com.tabulaw.client.mvc.view.ViewOptions;
import com.tabulaw.client.ui.DocSuggestWidget;
import com.tabulaw.client.ui.DocUploadDialog;
import com.tabulaw.client.ui.DocumentsListingWidget;


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
	
	class DocUploadButton extends PushButton implements ClickHandler {

		private DocUploadButton() {
			super("Upload");
			addClickHandler(this);
		}

		@Override
		public void onClick(ClickEvent event) {
			if(docUploadDialog == null) docUploadDialog = new DocUploadDialog();
			docUploadDialog.center();
		}
	}
	
	private final DocSuggestWidget docSuggest = new DocSuggestWidget();
	
	private final DocumentsListingWidget docListing = new DocumentsListingWidget();
	
	// private HandlerRegistration hrModelChangeSuggest;
	
	private DocUploadDialog docUploadDialog;
	
	private final DocUploadButton btnDocUpload = new DocUploadButton();
	
	/**
	 * Constructor
	 */
	public DocumentsView() {
		super();
	}

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
		return new Widget[] { btnDocUpload };
	}

	@Override
	protected void doInitialization(StaticViewInitializer initializer) {
		addWidget(docSuggest);
		addWidget(docListing);
		docListing.makeModelChangeAware();
	}

	@Override
	protected final void doRefresh() {
		docListing.loadData();
	}

	@Override
	protected final void doDestroy() {
		if(docListing.getOperator() != null) docListing.getOperator().clear();
		docListing.unmakeModelChangeAware();
	}
}
