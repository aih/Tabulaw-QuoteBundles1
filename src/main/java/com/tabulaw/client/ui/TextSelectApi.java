/**
 * The Logic Lab
 * @author jpk
 * @since Feb 20, 2010
 */
package com.tabulaw.client.ui;

import com.tabulaw.client.app.model.MarkOverlay;

/**
 * API for use in a native JavaScript context in order to realize document text
 * select features and persistence.
 * @author jpk
 */
public class TextSelectApi {

	private final ITextSelectHandler textSelectHandler;

	/**
	 * Constructor
	 * @param textSelectHandler required
	 */
	public TextSelectApi(ITextSelectHandler textSelectHandler) {
		this.textSelectHandler = textSelectHandler;
	}

	/**
	 * Call this to turn "on" the capturing of user selected text in a document
	 * under view.
	 * @param docId id of the doc
	 */
	public native void init(String docId) /*-{
		var frameId = 'docframe_' + docId;
		var frame = $wnd.goog.dom.$(frameId);
		//alert('init - frame: '+frame);
		var twindow = frame.contentWindow;
		//alert('init - twindow: '+twindow);

		var tsapi = this;

		$wnd['onDocFrameLoaded_'+docId] = function(iframedoc) {
			//alert('onDocFrameLoaded! iframedoc:' + iframedoc);
			//alert('iframedoc.body:' + iframedoc.body);

			// fire frame loaded callback fn
			tsapi.@com.tabulaw.client.ui.TextSelectApi::fireDocFrameLoaded(Ljava/lang/String;)(frameId);

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
					tsapi.@com.tabulaw.client.ui.TextSelectApi::fireTextSelectEvent(Lcom/tabulaw/client/app/model/MarkOverlay;)(mark);
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
	 * @param docId the doc id
	 */
	public native void shutdown(String docId) /*-{
		//alert('shutdown - frameId: ' + frameId);
		try {
			var frameId = 'docframe_' + docId;
			var frame = $wnd.goog.dom.$(frameId);
			if(frame == null) return;
			//alert('shutdown - frame: ' + frame);
			var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
			//alert('shutdown - fbody: ' + fbody);
			if(fbody) $wnd.goog.events.unlisten(fbody, 'mouseup');

			$wnd['onDocFrameLoaded_'+docId] = null; 			
		} catch(e) {
			alert('text select api shutdown error: ' + e);
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
	public void fireTextSelectEvent(MarkOverlay range) {
		textSelectHandler.onTextSelect(new TextSelectEvent(range));
	}

	/**
	 * Bridge function (JavaScript -> GWT) signaling the doc was just loaded into
	 * the containing iframe tag.
	 * @param frameId id of the iframe into which the doc was just loaded
	 */
	public void fireDocFrameLoaded(String frameId) {
		textSelectHandler.onDocFrameLoaded(frameId);
	}
}
