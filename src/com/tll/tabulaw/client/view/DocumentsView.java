/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.client.mvc.view.ViewClass;
import com.tll.tabulaw.client.ui.DocumentsListingWidget;


/**
 * View showing the existing documents.
 * @author jpk
 */
public class DocumentsView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "Documents";
		}

		@Override
		public DocumentsView newView() {
			return new DocumentsView();
		}
	}
	
	private final DocumentsListingWidget docListing = new DocumentsListingWidget();
	
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
		addWidget(docListing);
	}

	@Override
	protected final void doRefresh() {
		docListing.refresh();
	}

	@Override
	protected final void doDestroy() {
		docListing.clear();
	}

	@Override
	protected void handleModelChange(ModelChangeEvent event) {
		docListing.onModelChangeEvent(event);
	}
}
