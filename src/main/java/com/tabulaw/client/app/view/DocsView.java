/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.view;

import com.tabulaw.client.app.ui.DocsWidget;
import com.tabulaw.client.view.StaticViewInitializer;
import com.tabulaw.client.view.ViewClass;
import com.tabulaw.client.view.ViewOptions;


/**
 * View showing the existing documents.
 * @author jpk
 */
public class DocsView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		/**
		 * Keep this view in the dom (for now) so se don't loose the form.
		 */
		private static final ViewOptions VIEW_OPTIONS = new ViewOptions(false, false, false, false, false, true);
		
		@Override
		public String getName() {
			return "documents";
		}

		@Override
		public DocsView newView() {
			return new DocsView();
		}

		@Override
		public ViewOptions getViewOptions() {
			return VIEW_OPTIONS;
		}
	}
	
	private final DocsWidget docsWidget = new DocsWidget();
	
	/**
	 * Constructor
	 */
	public DocsView() {
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
	protected void doInitialization(StaticViewInitializer initializer) {
		addWidget(docsWidget);
		docsWidget.makeModelChangeAware();
	}

	@Override
	protected final void doRefresh() {
		docsWidget.refresh();
	}

	@Override
	protected final void doDestroy() {
		docsWidget.clearState();
		docsWidget.unmakeModelChangeAware();
	}
}
