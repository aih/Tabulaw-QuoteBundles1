/**
 * The Logic Lab
 * @author jpk
 * @since Feb 20, 2010
 */
package com.tabulaw.client.ui;

import java.util.ArrayList;

import com.tabulaw.client.app.model.MarkOverlay;

/**
 * API for use in a native JavaScript context in order to realize document text
 * select features and persistence.
 * @author jpk
 */
public class TextSelectApi implements IFiresTextSelectEvents {
	
	private static final TextSelectApi instance = new TextSelectApi();
	
	public static final TextSelectApi get() { 
		return instance; 
	}
	
	@SuppressWarnings("serial")
	static final class TextSelectHandlers extends ArrayList<ITextSelectHandler> {

		public void fireEvent(TextSelectEvent event) {
			for(final ITextSelectHandler handler : this) {
				handler.onTextSelect(event);
			}
		}
	} // TextSelectHandlers
	
	private final TextSelectHandlers tsHandlers = new TextSelectHandlers();
	
	/**
	 * Constructor
	 */
	private TextSelectApi() {
	}
	
	@Override
	public void addTextSelectHandler(ITextSelectHandler handler) {
		tsHandlers.add(handler);
	}
	
	@Override
	public void removeTextSelectHandler(ITextSelectHandler handler) {
		tsHandlers.remove(handler);
	}

	/**
	 * Call this to turn "on" the capturing of user selected text in a document
	 * under view.
	 * @param frameId the dom id of the target iframe.
	 */
	public static native void init(String frameId) /*-{
		var frame = $wnd.goog.dom.$(frameId);
		//alert('init - frame: '+frame);
		var twindow = frame.contentWindow;
		//alert('init - twindow: '+twindow);

		$wnd.onFrameLoaded = function(iframedoc) {
			//alert('onFrameLoaded! iframedoc:' + iframedoc);
			//alert('iframedoc.body:' + iframedoc.body);
			
			//////
			var mouseUpHandler = function(e){
				//alert('onMouseUp [frameId: ' + frameId + ']');
	
				var rng = $wnd.goog.dom.Range.createFromWindow(twindow);
				//alert('rng: '+ rng);
				if(rng == null) return;
	
				// make sure we have a legit text selection
				text = rng.getText();
				if(!text || $wnd.stringTrim(text).length == 0) 
					return;
				if(rng.getStartNode().nodeType != 3 || rng.getEndNode().nodeType != 3)
					return;

				var mark;
				try {
					if(iframedoc !== rng.getDocument()) alert('range.document != iframedoc!');
					//alert('iframedoc: ' + iframedoc + ', rng.getDocument(): ' + rng.getDocument());
					mark  = new $wnd.Mark(rng);
					@com.tabulaw.client.ui.TextSelectApi::fireTextSelectEvent(Lcom/tabulaw/client/app/model/MarkOverlay;)(mark);
				}
				catch(e) {
					alert('Unable to select this portion of text\n(' + e + ')');
					//alert('Unable to select this portion of text');
				}
			};
			//////
			
			// capture user selections w/in the iframe content
			$wnd.goog.events.listen(iframedoc.body, 'mouseup', mouseUpHandler);
			//alert('TextSelectApi.init() - DONE');
			//alert('init: ' + frameId);
		}
	}-*/;

	/**
	 * Call this to turn "off" the capturing of user text selections in a document
	 * under view.
	 * @param frameId the dom id of the target iframe.
	 */
	public static native void shutdown(String frameId) /*-{
		//alert('shutdown - frameId: ' + frameId);
		try {
			var frame = $wnd.goog.dom.$(frameId);
			//alert('shutdown - frame: ' + frame);
			var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
			//alert('shutdown - fbody: ' + fbody);
			if(fbody) $wnd.goog.events.unlisten(fbody, 'mouseup');
		} catch(e) {
			//alert('shutdown error: ' + e);
		}
	}-*/;

	/**
	 * Bridge function (JavaScript -> GWT) signaling a user text selection was
	 * made.
	 * <p>
	 * A {@link TextSelectEvent} is fired to all registered handlers.
	 * <p>
	 * This method is meant to be called from a native JavaScript context.
	 * @param range the dom range js overlay object
	 */
	public static void fireTextSelectEvent(MarkOverlay range) {
		instance.tsHandlers.fireEvent(new TextSelectEvent(range));
	}
}
