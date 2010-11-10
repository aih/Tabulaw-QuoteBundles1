package com.tabulaw.client.app.search;

import com.google.gwt.event.shared.EventHandler;

public interface ISearchHandler extends EventHandler {
	void onSearchEvent(SearchEvent event);
}
