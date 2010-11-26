package com.tabulaw.client.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Radek Olesiak
 * 
 *         This class is similar to Window.open() but here we can control close
 *         event and the method open() returns popup window object
 * 
 */
public class PopupWindow extends JavaScriptObject {

	protected PopupWindow() {
	}

	public final native void close()/*-{
		this.close();
	}-*/;

	public final native void focus()/*-{
		this.focus();
	}-*/;

	public final static native void setCloseHandler(
			PopupWindowCloseHandler handler)/*-{
		$wnd["onPopupWindowClose"] = function() {
			handler.@com.tabulaw.client.util.PopupWindowCloseHandler::onClose()();
		}
	}-*/;

	public static native PopupWindow open(String url, String name,
			String features) /*-{
		return $wnd.open(url, name, features);
	}-*/;
}
