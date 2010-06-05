/**
 * The Logic Lab
 * @author jpk
 * @since May 4, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.ImageButton;

/**
 * Single widget containing:
 * <ul>
 * <li>User's documents (doc listing)
 * <li>Document Search
 * </ul>
 * @author jpk
 */
public class DocsWidget extends AbstractModelChangeAwareWidget {

	static class DocUploadButton extends ImageButton {

		private DocUploadDialog docUploadDialog;

		private DocUploadButton() {
			super(Resources.INSTANCE.uploadButton(), "Upload");
			setTitle("Upload one or more documents...");
			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(docUploadDialog == null) docUploadDialog = new DocUploadDialog();
					docUploadDialog.center();
				}
			});
		}
	}

	static class NewDocButton extends ImageButton {
		
		private DocCreateDialog dlg;

		private NewDocButton() {
			super(Resources.INSTANCE.plus(), "New Document");
			setTitle("Create a document...");
			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(dlg == null) {
						dlg = new DocCreateDialog();
						dlg.setGlassEnabled(true);
					}
					dlg.center();
				}
			});
		}
	}

	private final DocSearchWidget docSearch = new DocSearchWidget();

	private final FlowPanel docListingHeader = new FlowPanel();
	private final DocListingWidget docListing = new DocListingWidget();

	private final FlowPanel btnPanel = new FlowPanel();
	private final DocUploadButton btnDocUpload = new DocUploadButton();
	private final NewDocButton btnNewDoc = new NewDocButton();

	private final FlowPanel panel = new FlowPanel();

	/**
	 * Constructor
	 */
	public DocsWidget() {
		super();

		docListingHeader.setStyleName("docListingHeader");
		Label lblDocuments = new Label("Documents");
		lblDocuments.setStyleName("title");
		Label lblSummary = new Label("Includes your written documents and reading history.");
		lblSummary.setStyleName("summary");
		docListingHeader.add(lblDocuments);
		docListingHeader.add(lblSummary);

		btnPanel.setStyleName("btnRow");
		btnPanel.add(btnDocUpload);
		btnPanel.add(btnNewDoc);
		docListingHeader.add(btnPanel);

		panel.add(docSearch);
		panel.add(docListingHeader);
		panel.add(docListing);

		initWidget(panel);
	}

	/**
	 * Resets the state to that of initial load.
	 */
	public void refresh() {
		docSearch.reset();
		docListing.refresh();
	}

	/**
	 * Clears out all data but retains the structure.
	 */
	public void clearState() {
		if(docListing.getOperator() != null) docListing.getOperator().clear();
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		docListing.onModelChangeEvent(event);
	}
}
