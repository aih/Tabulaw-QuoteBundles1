/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.data.rpc.IRpcHandler;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.data.rpc.RpcEvent;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.client.ui.msg.Msgs;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocHashPayload;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.msg.Msg;

/**
 * Search as you type doc search widget.
 * <p>
 * Fires {@link SelectionEvent} instances to indicate a full text search was
 * requested. The fired selection event holds the query token for the full text
 * search.
 * @author jpk
 */
public class DocSuggestWidget extends Composite implements IRpcHandler, HasSelectionHandlers<String> {

	public static class Styles {

		public static final String DOC_SUGGEST = "docSuggest";
		public static final String ENTRY = "entry";
		public static final String TITLE = "title";
		public static final String SUMMARY = "summary";
		public static final String POWERED_BY = "poweredBy";
	}

	static class DocSuggestion implements Suggestion {

		final CaseDocSearchResult doc;
		final String displayString, queryToken;

		/**
		 * Constructor - doc search result entry
		 * @param doc
		 */
		public DocSuggestion(CaseDocSearchResult doc) {
			super();
			this.doc = doc;
			displayString =
					"<div class=\"entry\"><div class=\"title\">" + doc.getTitleHtml() + "</div><div class=\"summary\">"
							+ doc.getSummary() + "</div></div>";
			queryToken = null;
		}

		/**
		 * Constructor - Entry for doing a full text search token
		 * @param queryToken
		 */
		public DocSuggestion(String queryToken) {
			super();
			this.doc = null;
			this.queryToken = queryToken;
			this.displayString =
					"<div class=\"entry\"><span class=\"query\">" + queryToken
							+ "</span><span class=\"fts\">&nbsp;-&nbsp;full text search</span></div>";
		}

		public boolean isFullTextSearch() {
			return doc == null;
		}

		@Override
		public String getDisplayString() {
			return displayString;
		}

		@Override
		public String getReplacementString() {
			return doc == null ? queryToken : doc.getTitleHtml();
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
			DocSearchRequest dsr = new DocSearchRequest(DocDataProvider.GOOGLE_SCHOLAR, rquery, 0, 4, false);
			Poc.getDocService().search(dsr, new AsyncCallback<DocSearchPayload>() {

				@Override
				public void onSuccess(DocSearchPayload result) {
					if(result.hasErrors()) {
						Notifier.get().showFor(result);
					}
					else {
						query = aquery;
						List<CaseDocSearchResult> searchResults = result.getResults();
						ArrayList<DocSuggestion> suggestions = new ArrayList<DocSuggestion>(searchResults.size());
						// first entry - offer to perform a full text search
						suggestions.add(0, new DocSuggestion(query));
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

	private final DocSearchSuggestBox docSuggestBox;

	private final FlowPanel pnl = new FlowPanel();

	private final FlowPanel fpPoweredBy = new FlowPanel();

	private final RpcUiHandler uiHandler;

	private final HTML searchPlaceholder;

	/**
	 * Constructor
	 */
	public DocSuggestWidget() {
		super();
		docSuggestBox = new DocSearchSuggestBox();

		pnl.setStyleName(Styles.DOC_SUGGEST);
		pnl.add(docSuggestBox);

		fpPoweredBy.setStyleName(Styles.POWERED_BY);
		Image imgGglScholarLogo = new Image(Resources.INSTANCE.googleScholarLogo());
		fpPoweredBy.add(imgGglScholarLogo);
		fpPoweredBy.add(new Label("Search powered by "));
		pnl.add(fpPoweredBy);

		searchPlaceholder =
				new HTML("<img src=\"" + Resources.INSTANCE.magnifyingGlass().getURL()
						+ "\" /><p>e.g. 'New York Times Co. v. Sullivan</p>");
		searchPlaceholder.setStyleName("searchPlaceholder");
		searchPlaceholder.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				searchPlaceholder.setVisible(false);
				docSuggestBox.getTextBox().setFocus(true);
			}
		});
		docSuggestBox.getTextBox().addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				String text = docSuggestBox.getText();
				if(text.length() < 1) {
					searchPlaceholder.setVisible(true);
				}
			}
		});
		pnl.add(searchPlaceholder);

		initWidget(pnl);

		addHandler(this, RpcEvent.TYPE);

		// add default selection handler
		docSuggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				DocSuggestion docSuggest = (DocSuggestion) event.getSelectedItem();
				if(docSuggest.isFullTextSearch()) {
					SelectionEvent.fire(DocSuggestWidget.this, docSuggest.queryToken);
				}
				else {
					// fetch the doc
					final CaseDocSearchResult caseDoc = docSuggest.doc;
					final String docRemoteUrl = caseDoc.getUrl();
					Log.debug("Checking for client cache of docRemoteUrl: " + docRemoteUrl);
					DocRef mDoc = ClientModelCache.get().getCaseDocByRemoteUrl(docRemoteUrl);
					if(mDoc == null) {
						Log.debug("Fetching remote doc: " + docRemoteUrl);

						new RpcCommand<DocHashPayload>() {

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
							protected void handleSuccess(DocHashPayload result) {
								super.handleSuccess(result);
								if(result.hasErrors()) {
									Msgs.post(result.getStatus().getMsgs(Msg.MsgAttr.EXCEPTION.flag), docSuggestBox);
									return;
								}
								final DocRef mNewDoc =
										EntityFactory.get().buildCaseDoc(caseDoc.getTitle(), result.getDocHash(), new Date(), null,
												caseDoc.getCitation(), caseDoc.getUrl(), null);

								// persist the new doc and propagate through app
								ClientModelCache.get().persist(mNewDoc, DocSuggestWidget.this);

								DeferredCommand.addCommand(new Command() {

									@Override
									public void execute() {
										// show the doc (letting the model change event finish
										// first)
										final DocViewInitializer dvi = new DocViewInitializer(mNewDoc.getModelKey());
										ViewManager.get().dispatch(new ShowViewRequest(dvi));
									}
								});
							}

						}.execute();
					}
					else {
						final DocViewInitializer dvi = new DocViewInitializer(mDoc.getModelKey());
						DeferredCommand.addCommand(new Command() {

							@Override
							public void execute() {
								ViewManager.get().dispatch(new ShowViewRequest(dvi));
							}
						});
					}

					// clear out the suggest text
					docSuggestBox.setValue("", false);
				}
			}
		});

		uiHandler = new RpcUiHandler(docSuggestBox);
	}

	@Override
	public void onRpcEvent(RpcEvent event) {
		uiHandler.onRpcEvent(event);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
}
