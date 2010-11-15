package com.tabulaw.client.app.search;

import com.google.gwt.event.shared.GwtEvent;

public class SearchEvent extends GwtEvent<ISearchHandler> {
	public static final Type<ISearchHandler> TYPE = new Type<ISearchHandler>();
	
	private String criteria;
	
	public SearchEvent (String criteria) {
		this.criteria = criteria;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	@Override
	protected void dispatch(ISearchHandler handler) {
		handler.onSearchEvent(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ISearchHandler> getAssociatedType() {
		return TYPE;
	}
	
	

}
