package com.tabulaw.client.app.ui.event;

import com.google.gwt.event.shared.GwtEvent;

public class IframeClickEvent extends GwtEvent<IframeClickedHandler> {
	private static final Type<IframeClickedHandler> TYPE = new Type<IframeClickedHandler>();
	 
	 public IframeClickEvent() {
	 }
	 
	 public static Type<IframeClickedHandler> getType() {
	  return TYPE;
	 }
	 
	 
	 @Override
	 public com.google.gwt.event.shared.GwtEvent.Type<IframeClickedHandler> getAssociatedType() {
	  return TYPE;
	 }

	@Override
	protected void dispatch(IframeClickedHandler handler) {
		  handler.onIframeClicked(this);
		
	}
}
