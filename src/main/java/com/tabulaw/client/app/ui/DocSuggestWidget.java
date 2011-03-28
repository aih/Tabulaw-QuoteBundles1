/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.GoogleScholarDocFetcher;
import com.tabulaw.client.ui.IRpcHandler;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcEvent;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;

/**
 * Search as you type doc search widget.
 * <p>
 * Fires {@link SelectionEvent} instances to indicate a full text search was
 * requested. The fired selection event holds the query token for the full text
 * search.
 * @author jpk
 */
public class DocSuggestWidget extends Composite implements IRpcHandler, HasSelectionHandlers<String> {

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
			String s = "<div class=\"entry\">";
			s += "<div class=\"title\">" + doc.getTitleHtml() + "</div>";
			s += "<div class=\"citation\">" + doc.getCitation() + "</div>";
			s += "<div class=\"summary\">" + doc.getSummary() + "</div>";
			s += "</div>";
			displayString = s;
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
			DocSearchRequest dsr = new DocSearchRequest("GOOGLE_SCHOLAR", rquery, 0, 4, false);
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

	static class SearchPlaceholder extends FocusPanel {

		private final FlowPanel flowPanel = new FlowPanel();

		/**
		 * Constructor
		 */
		public SearchPlaceholder() {
			super();
			setWidget(flowPanel);
			flowPanel.setStyleName("searchPlaceholder");
			flowPanel.add(new Image(Resources.INSTANCE.magnifyingGlass()));
			HTML html = new HTML("<p>e.g. New York Times Co. v. Sullivan</p>");
			html.setStyleName("text");
			flowPanel.add(html);
		}
	}

	private final DocSearchSuggestBox docSuggestBox;

	private final FlowPanel pnl = new FlowPanel();

	private final FlowPanel fpPoweredBy = new FlowPanel();

	private final RpcUiHandler uiHandler;

	private final SearchPlaceholder searchPlaceholder;

	/**
	 * Constructor
	 */
	public DocSuggestWidget() {
		super();
		docSuggestBox = new DocSearchSuggestBox();

		pnl.setStyleName("docSuggest");
		pnl.add(docSuggestBox);

		fpPoweredBy.setStyleName("poweredBy");
		Image imgGglScholarLogo = new Image(Resources.INSTANCE.googleScholarLogo());
		fpPoweredBy.add(imgGglScholarLogo);
		fpPoweredBy.add(new Label("Search powered by "));
		pnl.add(fpPoweredBy);

		searchPlaceholder = new SearchPlaceholder();
		searchPlaceholder.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// searchPlaceholder.setVisible(false);
				docSuggestBox.getTextBox().setFocus(true);
			}
		});
		docSuggestBox.getTextBox().addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				searchPlaceholder.setVisible(false);
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
					
					GoogleScholarDocFetcher.fetchGoogleScholarDoc(docRemoteUrl,DocSuggestWidget.this,docSuggestBox);

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
