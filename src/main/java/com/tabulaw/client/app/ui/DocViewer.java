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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.rpc.Payload;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.util.StringUtil;

/**
 * Displays a single document either statically (default) or in a rich text area
 * (edit mode).
 * <p>
 * Doc events are fired to signal when content is loaded/unloaded in the
 * containing doc frame element.
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

	public static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String RTF_MIME_TYPE = "text/rtf";

	public static enum ViewMode {
		EDIT,
		STATIC;
	}

	private static class DownloadDocCommand implements Command {

		private String mimeType;
		private String id;

		public DownloadDocCommand(String mimeType) {
			this.mimeType = mimeType;
		}

		public void setId(String id) {
			this.id = id;
		}

		@Override
		public void execute() {
			setLocation("docdownload?mimeType=" + mimeType + "&docId=" + id);
		}

		private final native void setLocation(String url) /*-{
			$wnd.location.href = url;
		}-*/;
	}

	static class DocViewHeader extends Composite {

		private final FlowPanel pnl = new FlowPanel();
		private final HTML html = new HTML();

		private DownloadDocCommand rtfDownloadCommand = new DownloadDocCommand(RTF_MIME_TYPE);
		private DownloadDocCommand docxDownloadCommand = new DownloadDocCommand(DOCX_MIME_TYPE);

		private final Image imgEdit = new Image(Resources.INSTANCE.edit());

		public DocViewHeader() {
			super();
			pnl.setStyleName("docHeader");

			imgEdit.setStyleName("imgEdit");
			imgEdit.setTitle("Edit document");

			MenuBar downloadMenuTop = new MenuBar();

			MenuBar downloadMenu = new MenuBar(true);

			downloadMenuTop.addItem("<img src='poc/images/word-16.gif'/><u>Download</u>", true, downloadMenu);
			downloadMenuTop.setStyleName("docHeaderMenuItem");

			MenuItem fireRtf = new MenuItem("rtf format", rtfDownloadCommand);
			MenuItem fireDocx = new MenuItem("docx format", docxDownloadCommand);

			downloadMenu.addItem(fireRtf);
			downloadMenu.addItem(fireDocx);

			html.setStyleName("docHeaderLabel");
			pnl.add(html);
			pnl.add(imgEdit);
			pnl.add(downloadMenuTop);

			initWidget(pnl);
		}

		public void insert(Widget w, int beforeIndex) {
			pnl.insert(w, 0);
		}
	}

	static class DocFrame extends ScrollPanel {

		public DocFrame() {
			super();
			setStyleName("docFrame");
			//getElement().setAttribute("frameBorder", "0"); // for IE
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
	 * The tag in which the doc is loaded.
	 */
	private final DocFrame frame;

	private DocEditWidget dew;

	private PushButton btnSave, btnCancel;

	private DocRef doc;

	/**
	 * flag indicating whether or not actual doc content exists in the doc frame
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

		frame = new DocFrame();
		pnl.add(frame);

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
					pnl.add(dew);
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
	 *         frame element.
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
	 */
	native void initDocFrame() /*-{
		//alert('TextSelectApi.init() - START');
		
		var tsapi = this;
		var mouseUpHandler = function(e){
			//alert('onMouseUp [frameId: ' + frameId + ']');

			var rng = $wnd.goog.dom.Range.createFromWindow($wnd);
			//alert('rng: '+ rng);
			if(rng == null) return;

			// make sure we have a legit text selection
			var text = rng.getText();
			if(!text || $wnd.stringTrim(text).length == 0) 
				return;
			if(rng.getStartNode().nodeType != 3 || rng.getEndNode().nodeType != 3)
				return;

			var mark;
			try {
				//if(framedoc !== rng.getDocument()) alert('range.document != framedoc!');
				//alert('framedoc: ' + framedoc + ', rng.getDocument(): ' + rng.getDocument());
				mark  = new $wnd.Mark(rng);
				tsapi.@com.tabulaw.client.app.ui.DocViewer::fireTextSelectEvent(Lcom/tabulaw/client/app/model/MarkOverlay;)(mark);
			}
			catch(e) {
				alert('Unable to select this portion of text\n(' + e + ')');
				//alert('Unable to select this portion of text');
			}
		};

		// capture user selections w/in the doc frame
		var frameId = tsapi.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(frameId);
		$wnd.goog.events.listen(frame, 'mouseup', mouseUpHandler);

		// fire doc loaded
		tsapi.@com.tabulaw.client.app.ui.DocViewer::fireDocLoaded()();

		//alert('TextSelectApi.init() - DONE');
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
			//if(frame == null) return;
			//alert('shutdown - frame: ' + frame);
			if(frame) $wnd.goog.events.unlisten(frame, 'mouseup');

			// fire doc unloaded
			this.@com.tabulaw.client.app.ui.DocViewer::fireDocUnloaded()();
		} catch(e) {
			alert('doc shutdown error: ' + e);
		}
	}-*/;

	/**
	 * @return the DOM body element ref of the contained document.
	 */
	public native JavaScriptObject getDocBody() /*-{
		//var frameId = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
		//var frame = $wnd.goog.dom.$(frameId);
		//var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		//return fbody;
		return $wnd.body;
	}-*/;

	public DocRef getModel() {
		return doc;
	}

	/**
	 * Sets the document model data.
	 * <p>
	 * NOTE: <code>null</code> model is supported.
	 * @param doc
	 * @param docContent optional
	 */
	public void setModel(DocRef doc, DocContent docContent) {

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
			initDocFrame();
		}

		// set html content directly or via url?
		String htmlContent = docContent == null ? null : docContent.getHtmlContent();
		if(!StringUtil.isEmpty(htmlContent)) {
			// html content
			//frame.setUrl("");
			setDocHtml(htmlContent);
			Log.debug("DocViewer html content set directly");
		}
		else {
			throw new IllegalStateException("No doc content specified");
			// remote url
			//String furl = doc == null ? "" : "doc?id=" + doc.getId();
			//frame.setUrl(furl);
			//Log.debug("DocViewer fetching content: " + furl);
		}

		if(doc != null) {
			header.rtfDownloadCommand.setId(doc.getId());
			header.docxDownloadCommand.setId(doc.getId());
		}
	}
	
	public String getDocHtml() {
		return frame.getElement().getInnerHTML();
	}

	public void setDocHtml(String html) {
		this.frame.getElement().setInnerHTML(html);
	}

//	public native String getDocHtml() /*-{
//		var fid = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
//		var frame = $wnd.goog.dom.$(fid);
//		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
//		return fbody.innerHTML;
//	}-*/;

//	public native void setDocHtml(String html) /*-{
//		var fid = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
//		var frame = $wnd.goog.dom.$(fid);
//		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
//		//$wnd.alert('html: ' + html);
//		fbody.innerHTML = html;
//	}-*/;

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ViewMode> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public DocEditWidget getDocEditWidget() {
		return dew;
	}

	public Widget[] getNavColWidgets() {
		return isEditMode() ? new Widget[] {
			btnSave, btnCancel } : null;
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
	 * @return The id assigned to the doc frame element or <code>null</code> if the
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
		RootPanel.get().removeStyleName("doc-static");
		RootPanel.get().addStyleName("doc-edit");

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
		RootPanel.get().addStyleName("doc-static");
		RootPanel.get().removeStyleName("doc-edit");

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
