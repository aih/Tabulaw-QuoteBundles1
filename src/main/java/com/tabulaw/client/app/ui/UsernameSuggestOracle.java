package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.tabulaw.client.app.Poc;
import com.tabulaw.common.data.rpc.UserListPayload;
import com.tabulaw.model.User;

public class UsernameSuggestOracle extends SuggestOracle{
    /**
     * Class to hold a response from the server.
     */
    private static class ServerResponse {

        /**
         * Request made by the SuggestBox.
         */
        private final Request request;

        /**
         * The number of suggestions the server was asked for
         */
        private final int serverSuggestionsLimit;

        /**
         * Suggestions returned by the server in response to the request.
         */
        private final List<Suggestion> suggestions;

        /**
         * Create a new instance.
         *
         * @param request Request from the SuggestBox.
         * @param serverSuggestionsLimit The number of suggestions we asked the server for.
         * @param suggestions The suggestions returned by the server.
         */
        private ServerResponse(Request request, int serverSuggestionsLimit, List<Suggestion> suggestions) {
            this.request = request;
            this.serverSuggestionsLimit = serverSuggestionsLimit;
            this.suggestions = suggestions;
        }

        /**
         * Get the query string that was sent to the server.
         *
         * @return The query.
         */
        private String getQuery() {
            return request.getQuery();
        }

        /**
         * Does the response include all possible suggestions for the query.
         *
         * @return True or false.
         */
        private boolean isComplete() {
            return suggestions.size() <= serverSuggestionsLimit;
        }
        public List<Suggestion> getSuggestions() {
        	return suggestions;
        }

    }

    /**
     * Number of suggestions to request from the server.
     */
    private static final int numberOfServerSuggestions = 25; //to fit on screen  


    /**
     * Is there a request in progress
     */
    private boolean requestInProgress = false;

    /**
     * Is there a request in progress
     */
    private Set<User> exclusions;

    
    /**
     * The most recent request made by the client.
     */
    private Request mostRecentClientRequest = null;

    /**
     * The most recent response from the server.
     */
    private ServerResponse mostRecentServerResponse = null;

    public UsernameSuggestOracle (Set<User> exclusions) {
    	this.exclusions = exclusions;
    }
    
    /**
     * Called by the SuggestBox to get some suggestions.
     *
     * @param request The request.
     * @param callback The callback to call with the suggestions.
     */
    public void requestSuggestions(final Request request, final Callback callback) {
        // Record this request as the most recent one.
        mostRecentClientRequest = request;
        // If there is not currently a request in progress return some suggestions. If there is a request in progress
        // suggestions will be returned when it completes.
        if (!requestInProgress) {
            returnSuggestions(callback);
        }
    }

    /**
     * Return some suggestions to the SuggestBox. At this point we know that there is no call to the server currently in
     * progress and we try to satisfy the request from the most recent results from the server before we call the server.
     *
     * @param callback The callback.
     */
    private void returnSuggestions(Callback callback) {
        // For single character queries return an empty list.
        final String mostRecentQuery = mostRecentClientRequest.getQuery();
        if (mostRecentQuery.length() == 1) {
            callback.onSuggestionsReady(mostRecentClientRequest,
                    new Response(Collections.<Suggestion>emptyList()));
            return;
        }
        
		if (mostRecentServerResponse != null) {
			if (mostRecentQuery.equals(mostRecentServerResponse.getQuery())) {
				Response resp = new Response(mostRecentServerResponse.getSuggestions());

				callback.onSuggestionsReady(mostRecentClientRequest, resp);

			} else {
				makeRequest(mostRecentClientRequest, callback);
			}
		} else {
			makeRequest(mostRecentClientRequest, callback);
		}
    }

    /**
     * Send a request to the server.
     *
     * @param request The request.
     * @param callback The callback to call when the request returns.
     */
    private void makeRequest(final Request request, final Callback callback) {
        requestInProgress = true;
        Poc.getUserDataService().suggestUserName(request.getQuery(), numberOfServerSuggestions, new AsyncCallback<UserListPayload>() {
            public void onFailure(Throwable caught) {
                requestInProgress = false;
            }
            public void onSuccess(UserListPayload result) {
                requestInProgress = false;
                List<Suggestion> suggestionList = new ArrayList<Suggestion>(); 
                for (User user : result.getUsers()) {
                	if (exclusions!=null && !exclusions.contains(user)) {
	                	MultiWordSuggestOracle.MultiWordSuggestion suggestion = new MultiWordSuggestOracle.MultiWordSuggestion(user.getEmailAddress(),user.getEmailAddress());
	                	suggestionList.add(suggestion);
                	}
                }
                mostRecentServerResponse = new ServerResponse(request, numberOfServerSuggestions, suggestionList);
                UsernameSuggestOracle.this.returnSuggestions(callback);
            }
        });
    }
}