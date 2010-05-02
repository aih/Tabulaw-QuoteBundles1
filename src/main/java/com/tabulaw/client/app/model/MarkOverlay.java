package com.tabulaw.client.app.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

/**
 * Overlay for Mark in mark.js
 * <p>This augments quote model data.
 * @author jpk
 */
public class MarkOverlay extends JavaScriptObject {
	
	/**
	 * De-serializes the given token re-setting the state  
	 * @param body the dom body ref (required to create the corres. range)
	 * @param stoken the serialized string (return value of serialize())
	 * @return de-serialized mark instance
	 */
	public static final native MarkOverlay deserialize(JavaScriptObject body, String stoken) /*-{ 
		return new $wnd.Mark(body, stoken);
	}-*/;

	/**
	 * @return The entire mark state serialized into a string.
	 */
	public final native String serialize() /*-{ return this.serialize(); }-*/;

	/**
	 * @return The text of the user selection.
	 */
	public final native String getText() /*-{ return this.getText(); }-*/;
	
	/**
	 * @return The start node.
	 */
	public final native Element getStartNode() /*-{
		return this.getStartNode();
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