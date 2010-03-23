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
import com.tll.tabulaw.client.ui.DocSearchWidget;

/**
 * Document search view.
 * @author jpk
 */
public class DocumentSearchView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "docSearch";
		}

		@Override
		public DocumentSearchView newView() {
			return new DocumentSearchView();
		}
	}
	
	private final DocSearchWidget docSearchWidget;

	/**
	 * Constructor
	 */
	public DocumentSearchView() {
		super();
		docSearchWidget = new DocSearchWidget();
	}

	@Override
	public String getLongViewName() {
		return "Document Search";
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
		addWidget(docSearchWidget);
	}

	@Override
	protected final void doRefresh() {
	}

	@Override
	protected final void doDestroy() {
		// no-op
	}

	@Override
	protected void handleModelChange(ModelChangeEvent event) {
		// no-op
	}
}
