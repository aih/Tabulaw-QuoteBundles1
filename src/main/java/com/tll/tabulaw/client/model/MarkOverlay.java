package com.tll.tabulaw.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay for Mark in mark.js
 * <p>This augments quote model data.
 * @author jpk
 */
public class MarkOverlay extends JavaScriptObject {

	/**
	 * @return The text of the user selection.
	 */
	public final native String getText() /*-{ return this.toString(); }-*/;

	/**
	 * Sets the window object to use.
	 * @param wnd the window ref
	 */
	public final native void setWindow(JavaScriptObject wnd) /*-{ 
		this.setWindow(wnd); 
	}-*/;
	
	/**
	 * Highlights the text selection.
	 */
	public final native void highlight() /*-{ 
		this.highlight(); 
	}-*/;
	
	/**
	 * Removes the highlight around the text selection.
	 */
	public final native void unhighlight() /*-{
		this.unhighlight();
	}-*/;
	
	protected MarkOverlay() {
	}
}