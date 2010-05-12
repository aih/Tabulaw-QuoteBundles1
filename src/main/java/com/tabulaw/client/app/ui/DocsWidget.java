/**
 * The Logic Lab
 * @author jpk
 * @since May 4, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.rpc.DocHashPayload;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;

/**
 * Single widget containing:
 * <ul>
 * <li>User's documents (doc listing)
 * <li>Document Search
 * </ul>
 * @author jpk
 */
public class DocsWidget extends AbstractModelChangeAwareWidget {

	private static class Styles {

		public static final String DOC_LISTING_HEADER = "docListingHeader";
		public static final String DOC_LISTING_HEADER_TITLE = "title";
		public static final String DOC_LISTING_HEADER_SUMMARY = "summary";
		public static final String DP_SEARCH = "searchDp";
	}

	static class DocUploadButton extends AbstractButton {

		private DocUploadDialog docUploadDialog;

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

	static class NewDocButton extends AbstractButton {

		private NewDocButton() {
			super("New", null);
			setClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// TODO use dialog to get these doc properties
					String docTitle = "New Document";
					Date docDate = new Date();
					final DocRef newDoc = EntityFactory.get().buildDoc(docTitle, null, docDate);
					
					// get docs from server
					new RpcCommand<DocHashPayload>() {

						@Override
						protected void doExecute() {
							setSource(NewDocButton.this);
							Poc.getDocService().createDoc(newDoc, this);
						}

						@Override
						protected void handleSuccess(DocHashPayload result) {
							super.handleSuccess(result);
							Notifier.get().showFor(result);
							if(!result.hasErrors()) {
								// persist the new doc and propagate through app
								newDoc.setHash(result.getDocHash());
								ClientModelCache.get().persist(newDoc, NewDocButton.this);

								DeferredCommand.addCommand(new Command() {

									@Override
									public void execute() {
										// show the doc (letting the model change event finish
										// first)
										final DocViewInitializer dvi = new DocViewInitializer(newDoc.getModelKey());
										ViewManager.get().dispatch(new ShowViewRequest(dvi));
									}
								});
							}
						}
					}.execute();
				}
			});
		}

		@Override
		protected String getTitleText(String buttonText) {
			return "Create new document";
		}
	}

	private final FlowPanel panel = new FlowPanel();

	private final DisclosurePanel dpSearch;
	private final DocSearchWidget docSearch = new DocSearchWidget();

	private final FlowPanel docListingHeader = new FlowPanel();
	private final DocListingWidget docListing = new DocListingWidget();

	private final FlowPanel btnPanel = new FlowPanel();
	private final DocUploadButton btnDocUpload = new DocUploadButton();
	private final NewDocButton btnNewDoc = new NewDocButton();

	/**
	 * Constructor
	 */
	public DocsWidget() {
		super();

		dpSearch = new DisclosurePanel("Search");
		dpSearch.setAnimationEnabled(true);
		dpSearch.addStyleName(Styles.DP_SEARCH);
		dpSearch.setContent(docSearch);
		dpSearch.setOpen(true); // initially open
		dpSearch.addOpenHandler(new OpenHandler<DisclosurePanel>() {

			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				// docListing.setVisible(false);
			}
		});
		dpSearch.addCloseHandler(new CloseHandler<DisclosurePanel>() {

			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				// docListing.setVisible(true);
			}
		});

		panel.add(dpSearch);

		docListingHeader.setStyleName(Styles.DOC_LISTING_HEADER);
		Label lblDocuments = new Label("Documents");
		lblDocuments.setStyleName(Styles.DOC_LISTING_HEADER_TITLE);
		Label lblSummary = new Label("Includes your written documents and reading history.");
		lblSummary.setStyleName(Styles.DOC_LISTING_HEADER_SUMMARY);
		docListingHeader.add(lblDocuments);
		docListingHeader.add(lblSummary);

		btnPanel.setStyleName("btnRow");
		btnPanel.add(btnDocUpload);
		btnPanel.add(btnNewDoc);
		docListingHeader.add(btnPanel);

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
