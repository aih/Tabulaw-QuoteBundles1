/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.ui.AbstractButton;
import com.tabulaw.client.app.ui.DocUploadDialog;
import com.tabulaw.client.app.ui.DocsWidget;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.mvc.view.ViewClass;
import com.tabulaw.client.mvc.view.ViewOptions;


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
	
	class DocUploadButton extends AbstractButton {

		private DocUploadButton() {
			super("Upload", null);
			setClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(docUploadDialog == null) docUploadDialog = new DocUploadDialog();
					docUploadDialog.center();
				}
			});
		}

		@Override
		protected String getTitleText(String buttonText) {
			return "Upload one or more documents...";
		}
	}
	
	private final DocsWidget docsWidget = new DocsWidget();
	
	private DocUploadDialog docUploadDialog;
	
	private final DocUploadButton btnDocUpload = new DocUploadButton();
	
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
	public Widget[] getNavColWidgets() {
		return new Widget[] { btnDocUpload };
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
