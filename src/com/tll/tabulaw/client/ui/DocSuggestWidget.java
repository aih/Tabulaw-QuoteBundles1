/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tll.tabulaw.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.tll.client.data.rpc.IRpcHandler;
import com.tll.client.data.rpc.RpcCommand;
import com.tll.client.data.rpc.RpcEvent;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.ui.RpcUiHandler;
import com.tll.client.ui.msg.Msgs;
import com.tll.common.model.Model;
import com.tll.common.msg.Msg;
import com.tll.tabulaw.client.Poc;
import com.tll.tabulaw.client.Resources;
import com.tll.tabulaw.client.model.PocModelCache;
import com.tll.tabulaw.client.view.DocumentViewInitializer;
import com.tll.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tll.tabulaw.common.data.rpc.DocFetchPayload;
import com.tll.tabulaw.common.data.rpc.DocSearchPayload;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tll.tabulaw.common.model.PocEntityType;
import com.tll.tabulaw.common.model.PocModelFactory;

/**
 * Search as you type doc search widget.
 * @author jpk
 */
public class DocSuggestWidget extends AbstractModelChangeAwareWidget implements IRpcHandler {

	public static class Styles {

		public static final String DOC_SUGGEST = "docSuggest";
		public static final String IMAGE = "image";
		public static final String ENTRY = "entry";
		public static final String TITLE = "title";
		public static final String SUMMARY = "summary";
	}

	static class DocSuggestion implements Suggestion {

		final CaseDocSearchResult doc;
		final String displayString;

		public DocSuggestion(CaseDocSearchResult doc) {
			super();
			this.doc = doc;
			displayString =
					"<div class=\"entry\"><div class=\"title\">" + doc.getTitleHtml() + "</div><div class=\"summary\">"
							+ doc.getSummary() + "</div></div>";
		}

		@Override
		public String getDisplayString() {
			return displayString;
		}

		@Override
		public String getReplacementString() {
			return doc.getTitle();
		}

	}

	class DocSearchSuggestOracle extends SuggestOracle {

		private String query = "";

		@Override
		public boolean isDisplayStringHTML() {
			return true;
		}

		@Override
		public void requestSuggestions(final Request request, final Callback callback) {
			String rquery = request.getQuery();
			if(rquery == null || rquery.length() < 1) return;
			rquery = rquery.trim();
			if(rquery.equals(query)) return;
			final String aquery = rquery;
			DocSearchRequest dsr = new DocSearchRequest(DocDataProvider.GOOGLE_SCHOLAR, rquery, 0, 4);
			Poc.getDocService().search(dsr, new AsyncCallback<DocSearchPayload>() {

				@Override
				public void onSuccess(DocSearchPayload result) {
					if(!result.getStatus().hasErrors()) {
						query = aquery;
						List<CaseDocSearchResult> searchResults = result.getResults();
						ArrayList<DocSuggestion> suggestions = new ArrayList<DocSuggestion>(searchResults.size());
						for(final CaseDocSearchResult doc : searchResults) {
							suggestions.add(new DocSuggestion(doc));
						}
						Response sresponse = new Response(suggestions);
						callback.onSuggestionsReady(request, sresponse);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Unable to get doc search results: " + caught.getMessage());
				}
			});
		}
	} // DocSearchSuggestOracle

	class DocSearchSuggestBox extends SuggestBox {

		public DocSearchSuggestBox() {
			super(new DocSearchSuggestOracle());
		}
	}

	private final Image searchImage;

	private final DocSearchSuggestBox docSuggestBox;

	private final FlowPanel pnl = new FlowPanel();

	private final RpcUiHandler uiHandler;

	/**
	 * Constructor
	 */
	public DocSuggestWidget() {
		super();
		searchImage = new Image(Resources.INSTANCE.searchImage());
		searchImage.setStyleName(Styles.IMAGE);
		docSuggestBox = new DocSearchSuggestBox();

		pnl.setStyleName(Styles.DOC_SUGGEST);
		pnl.add(searchImage);
		pnl.add(docSuggestBox);
		initWidget(pnl);

		addHandler(this, RpcEvent.TYPE);

		// add default selection handler
		docSuggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				// fetch the doc
				final CaseDocSearchResult caseDoc = ((DocSuggestion) event.getSelectedItem()).doc;
				final String docRemoteUrl = caseDoc.getUrl();
				Log.debug("Checking for client cache of docRemoteUrl: " + docRemoteUrl);
				Model mDoc = PocModelCache.get().getCaseDocByRemoteUrl(docRemoteUrl);
				if(mDoc == null) {
					Log.debug("Fetching remote doc: " + docRemoteUrl);

					new RpcCommand<DocFetchPayload>() {

						@Override
						protected void doExecute() {
							this.source = DocSuggestWidget.this;
							Poc.getDocService().fetch(docRemoteUrl, this);
						}

						@Override
						protected void handleFailure(Throwable caught) {
							super.handleFailure(caught);
							Log.error("Unable to fetch remote document", caught);
						}

						@Override
						protected void handleSuccess(DocFetchPayload result) {
							super.handleSuccess(result);
							if(result.hasErrors()) {
								Msgs.post(result.getStatus().getMsgs(Msg.MsgAttr.EXCEPTION.flag), docSuggestBox);
								return;
							}
							final Model mNewDoc =
									PocModelFactory.get().buildCaseDoc(caseDoc.getTitle(), result.getLocalUrl(), new Date(), null,
											caseDoc.getCitation(), caseDoc.getUrl(), null);
							mNewDoc.setId(PocModelCache.get().getNextId(PocEntityType.DOCUMENT));

							// persist the new doc and propagate through app
							PocModelCache.get().persist(mNewDoc, DocSuggestWidget.this);

							DeferredCommand.addCommand(new Command() {

								@Override
								public void execute() {
									// show the doc (letting the model change event finish first)
									final DocumentViewInitializer dvi = new DocumentViewInitializer(mNewDoc.getKey());
									ViewManager.get().dispatch(new ShowViewRequest(dvi));
								}
							});
						}

					}.execute();
				}
				else {
					final DocumentViewInitializer dvi = new DocumentViewInitializer(mDoc.getKey());
					DeferredCommand.addCommand(new Command() {

						@Override
						public void execute() {
							ViewManager.get().dispatch(new ShowViewRequest(dvi));
						}
					});
				}
			}
		});

		uiHandler = new RpcUiHandler(docSuggestBox);
	}

	@Override
	public void onRpcEvent(RpcEvent event) {
		uiHandler.onRpcEvent(event);
	}

}
