package com.tll.tabulaw.client.model;

import com.google.gwt.core.client.JavaScriptObject;

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