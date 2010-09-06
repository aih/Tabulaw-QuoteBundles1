package com.tabulaw.client.app.ui.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

public class IframeClickEvent extends GwtEvent<IframeClickedHandler> {
	private static final Type TYPE = new Type<IframeClickedHandler>();
	 
	 public IframeClickEvent() {
	 }
	 
	 public static Type getType() {
	  return TYPE;
	 }
	 
	 
	 @Override
	 public com.google.gwt.event.shared.GwtEvent.Type getAssociatedType() {
	  return TYPE;
	 }

	@Override
	protected void dispatch(IframeClickedHandler handler) {
		  handler.onIframeClicked(this);
		
	}
}
