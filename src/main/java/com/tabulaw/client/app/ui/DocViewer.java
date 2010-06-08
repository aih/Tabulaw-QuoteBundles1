/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.ui.DocEvent;
import com.tabulaw.client.ui.IDocHandler;
import com.tabulaw.client.ui.IHasDocHandlers;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.util.StringUtil;

/**
 * Displays a single document either statically (default) or in a rich text area
 * (edit mode).
 * <p>
 * Doc events are fired to signal when content is loaded/unloaded in the
 * containing iframe element.
 * <p>
 * Text select events are fired upon user text selections when in static mode.
 * <p>
 * Fires {@link DocEvent}s.
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "edit" when the document view is
 * set to edit mode.
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "static" when the document view is
 * set to read-only mode.
 * @author jpk
 */
public class DocViewer extends Composite implements IHasDocHandlers, HasValueChangeHandlers<DocViewer.ViewMode> {

	public static enum ViewMode {
		EDIT,
		STATIC;
	}

	static class DocViewHeader extends Composite {

		private final FlowPanel pnl = new FlowPanel();
		private final HTML html = new HTML();

		private final Image imgEdit = new Image(Resources.INSTANCE.edit());
		//private final Image imgExport = new Image(Resources.INSTANCE.permalink());
		private final Anchor aDwnldAsWordDoc;

		public DocViewHeader() {
			super();
			pnl.setStyleName("docHeader");

			imgEdit.setStyleName("imgEdit");
			imgEdit.setTitle("Edit document");

			aDwnldAsWordDoc = new Anchor("Download as MS Word doc", false);
			aDwnldAsWordDoc.setTarget("_blank");
			//imgExport.setStyleName("imgExport");
			//imgExport.setTitle("Export to MS Word");

			html.setStyleName("docHeaderLabel");
			pnl.add(html);
			pnl.add(imgEdit);
			//pnl.add(imgExport);
			pnl.add(aDwnldAsWordDoc);

			initWidget(pnl);
		}

		public void insert(Widget w, int beforeIndex) {
			pnl.insert(w, 0);
		}
	}

	static class DocFrame extends Frame {

		public DocFrame() {
			super();
			setStyleName("docFrame");
			getElement().setAttribute("frameBorder", "0"); // for IE
		}
	}

	/**
	 * docView
	 */
	private final FlowPanel pnl = new FlowPanel();

	/**
	 * docHeader
	 */
	private final DocViewHeader header = new DocViewHeader();

	/**
	 * container
	 */
	private final FlowPanel container = new FlowPanel();

	/**
	 * The iframe tag in which the doc is loaded.
	 */
	private final DocFrame frame;

	private DocEditWidget dew;

	private PushButton btnSave, btnCancel;

	private DocRef doc;

	/**
	 * flag indicating whether or not actual doc content exists in the iframe
	 * element.
	 */
	boolean docContentLoaded = false;

	/**
	 * Constructor
	 */
	public DocViewer() {
		super();

		pnl.setStylePrimaryName("docView");
		pnl.add(header);

		container.addStyleName("docContainer");
		pnl.add(container);

		frame = new DocFrame();
		container.add(frame);

		initWidget(pnl);

		header.imgEdit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(dew == null) {

					// wire up save/cancel buttons
					assert btnSave == null;
					assert btnCancel == null;
					btnSave = new PushButton("Save", new ClickHandler() {

						@Override
						public void onClick(ClickEvent clkEvt) {
							if(doc == null) return;

							// save the doc
							String docHtml = dew.getHTML();
							setDocHtml(docHtml);
							staticMode();

							// persist to server
							Poc.getUserDataService().updateDocContent(doc.getId(), docHtml, new AsyncCallback<Payload>() {

								@Override
								public void onSuccess(Payload result) {
									Notifier.get().showFor(result, null);
								}

								@Override
								public void onFailure(Throwable caught) {
									Notifier.get().showFor(caught);
								}
							});
						}
					});
					btnSave.setTitle("Save Document");
					btnCancel = new PushButton("Cancel", new ClickHandler() {

						@Override
						public void onClick(ClickEvent clkEvt) {
							staticMode();
						}
					});
					btnCancel.setTitle("Revert Document");

					dew = new DocEditWidget();
					dew.setVisible(false);
					container.add(dew);
				}

				if(!dew.isVisible()) {
					editMode();
				}
			}
		});

		// set initial styling..
		staticMode();
	}

	/**
	 * @return <code>true</code> if html content exists within the containing doc
	 *         iframe element.
	 */
	public boolean isDocContentLoaded() {
		return docContentLoaded;
	}

	@Override
	public HandlerRegistration addDocHandler(IDocHandler handler) {
		return addHandler(handler, DocEvent.TYPE);
	}

	void fireTextSelectEvent(MarkOverlay range) {
		fireEvent(DocEvent.createTextSelectEvent(range));
	}

	void fireDocLoaded() {
		docContentLoaded = true;
		fireEvent(DocEvent.createDocLoadEvent(true));
	}

	void fireDocUnloaded() {
		fireEvent(DocEvent.createDocLoadEvent(false));
		docContentLoaded = false;
	}

	/**
	 * Call this to turn "on" the capturing of user selected text in a document
	 * under view.
	 * @param docId id of the doc
	 */
	native void initDocFrame(String docId) /*-{
		//alert('initDocFrame - docId: '+docId);

		var onLoadFnName = 'onDocFrameLoaded_'+docId;
		if($wnd[onLoadFnName]) return; // already init'd!

		var frameId = 'docframe_' + docId;
		var frame = $wnd.goog.dom.$(frameId);
		//alert('init - frame: '+frame);
		var twindow = frame.contentWindow;
		//alert('init - twindow: '+twindow);

		var tsapi = this;

		$wnd[onLoadFnName] = function(iframedoc) {
			//alert('onDocFrameLoaded! iframedoc:' + iframedoc);
			//alert('iframedoc.body:' + iframedoc.body);

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
					//if(iframedoc !== rng.getDocument()) alert('range.document != iframedoc!');
					//alert('iframedoc: ' + iframedoc + ', rng.getDocument(): ' + rng.getDocument());
					mark  = new $wnd.Mark(rng);
					tsapi.@com.tabulaw.client.app.ui.DocViewer::fireTextSelectEvent(Lcom/tabulaw/client/app/model/MarkOverlay;)(mark);
				}
				catch(e) {
					alert('Unable to select this portion of text\n(' + e + ')');
					//alert('Unable to select this portion of text');
				}
			};

			// capture user selections w/in the iframe content
			$wnd.goog.events.listen(iframedoc.body, 'mouseup', mouseUpHandler);

			// fire doc loaded
			tsapi.@com.tabulaw.client.app.ui.DocViewer::fireDocLoaded()();

			//alert('TextSelectApi.init() - DONE');
		}
	}-*/;

	/**
	 * Call this to turn "off" the capturing of user text selections in a document
	 * under view.
	 * @param docId the doc id
	 */
	native void shutdownDocFrame(String docId) /*-{
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

			// fire doc unloaded
			this.@com.tabulaw.client.app.ui.DocViewer::fireDocUnloaded()();
		} catch(e) {
			alert('doc shutdown error: ' + e);
		}
	}-*/;

	/**
	 * @return the DOM iframe body ref of the contained document.
	 */
	public native JavaScriptObject getDocBody() /*-{
		var frameId = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(frameId);
		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		return fbody;
	}-*/;

	public DocRef getModel() {
		return doc;
	}

	/**
	 * Sets the document model data.
	 * <p>
	 * NOTE: <code>null</code> model is supported.
	 * @param doc
	 */
	public void setModel(DocRef doc) {

		if(this.doc != null) {
			shutdownDocFrame(this.doc.getId());
		}

		this.doc = doc;

		// header
		String html = doc == null ? "" : doc.getTitle();
		header.html.setHTML("<p>" + html + "</p>");

		// disallow doc editing for case type docs
		header.imgEdit.setVisible(doc != null && doc.getCaseRef() == null);

		frame.getElement().setId(getFrameId());

		if(doc != null) {
			initDocFrame(doc.getId());
		}

		// set html content directly or via url?
		String htmlContent = doc == null ? null : doc.getHtmlContent();
		if(!StringUtil.isEmpty(htmlContent)) {
			// html content
			frame.setUrl("");
			setDocHtml(htmlContent);
			Log.debug("DocViewer iframe html content set directly");
		}
		else {
			// remote url
			String furl = doc == null ? "" : "doc?id=" + doc.getId();
			frame.setUrl(furl);
			Log.debug("DocViewer iframe url set to: " + furl);
		}
		
		// update export to ms word doc href
		String href = doc == null ? "#" : "docdownload?mimeType=application/msword&docId=" + doc.getId();
		header.aDwnldAsWordDoc.setHref(href);
	}

	public native String getDocHtml() /*-{
		var fid = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(fid);
		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		return fbody.innerHTML;
	}-*/;

	public native void setDocHtml(String html) /*-{
		var fid = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(fid);
		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		//$wnd.alert('html: ' + html);
		fbody.innerHTML = html;
	}-*/;

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ViewMode> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public DocEditWidget getDocEditWidget() {
		return dew;
	}

	public Widget[] getNavColWidgets() {
		return isEditMode() ? new Widget[] {
			btnSave, btnCancel
		} : null;
	}

	@Override
	protected void onUnload() {
		super.onUnload();

		String docId = doc == null ? null : doc.getId();
		if(docId != null) {
			shutdownDocFrame(docId);
			// fire doc frame unload event
			fireEvent(DocEvent.createDocLoadEvent(false));
		}
	}

	/**
	 * @return The id assigned to the iframe element or <code>null</code> if the
	 *         document model data has not been set.
	 */
	private String getFrameId() {
		return doc == null ? null : "docframe_" + doc.getId();
	}

	private boolean isEditMode() {
		return dew != null && dew.isVisible();
	}

	/**
	 * Sets the mode to edit.
	 */
	private void editMode() {
		pnl.removeStyleDependentName("static");
		pnl.addStyleDependentName("edit");

		// so qb doc col can know which doc mode we are in
		RootPanel.get().removeStyleName("docview-static");
		RootPanel.get().addStyleName("docview-edit");

		dew.setHTML(getDocHtml());
		frame.setVisible(false);
		dew.setVisible(true);

		header.html.setTitle("Editing");
		header.insert(dew.getEditBar(), 0);

		Poc.getNavCol().addWidget(btnSave);
		Poc.getNavCol().addWidget(btnCancel);

		ValueChangeEvent.fire(this, ViewMode.EDIT);
	}

	/**
	 * Sets the mode to static.
	 */
	private void staticMode() {
		pnl.addStyleDependentName("static");
		pnl.removeStyleDependentName("edit");

		// so qb doc col can know which doc mode we are in
		RootPanel.get().addStyleName("docview-static");
		RootPanel.get().removeStyleName("docview-edit");

		frame.setVisible(true);
		if(dew != null) {
			dew.setVisible(false);
			dew.getEditBar().removeFromParent();
			Poc.getNavCol().removeWidget(btnSave);
			Poc.getNavCol().removeWidget(btnCancel);
			ValueChangeEvent.fire(this, ViewMode.STATIC);
		}
	}

}
