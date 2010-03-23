/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.tll.tabulaw.client.Resources;
import com.tll.tabulaw.common.data.rpc.DocSearchPayload;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;

/**
 * Search as you type doc search widget.
 * @author jpk
 */
public class DocSuggestWidget extends Composite implements HasSelectionHandlers<Suggestion> {

	public static class Styles {
		public static final String IMAGE = "image";
		public static final String ENTRY = "entry";
		public static final String TITLE = "title";
		public static final String SUMMARY = "summary";
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
			DocSearchRequest dsr =  new DocSearchRequest(DocDataProvider.GOOGLE_SCHOLAR, rquery, 0, 4);
			DocSearchWidget.svc.search(dsr, new AsyncCallback<DocSearchPayload>() {
				
				@Override
				public void onSuccess(DocSearchPayload result) {
					if(!result.getStatus().hasErrors()) {
						query = aquery;
						Response sresponse = new Response(result.getResults());
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

	private final DocSearchSuggestBox docSuggest;
	
	private final FlowPanel pnl = new FlowPanel();
	
	/**
	 * Constructor
	 */
	public DocSuggestWidget() {
		super();
		searchImage = new Image(Resources.INSTANCE.searchImage());
		searchImage.setStyleName(Styles.IMAGE);
		docSuggest = new DocSearchSuggestBox();
		
		pnl.add(searchImage);
		pnl.add(docSuggest);
		initWidget(pnl);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
		return docSuggest.addSelectionHandler(handler);
	}

}
