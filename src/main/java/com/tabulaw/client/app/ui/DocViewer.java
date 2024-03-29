/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.GoogleScholarDocFetcher;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.ui.AbstractInfoDialog;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.msg.Msgs;
import com.tabulaw.common.data.rpc.Payload;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.ModelKey;
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
	public static final String DOC_MIME_TYPE = "application/msword";
	public static final String RTF_MIME_TYPE = "text/rtf";

	public static enum ViewMode {
		EDIT,
		STATIC;
	}

	public static enum LinkType {
		INTERNAL_LINK,
		GOOGLE_SCHOLAR,
		EXTERNAL_LINK;

		private static String GOOGLE_SCHOLAR_PATTERN = "^http://scholar\\.google\\.com/scholar_case\\?case=[0-9]+\\&+.+"; //[0-9]
		private static String LOCAL_PATTERN = Window.Location.getProtocol() + "//"+Window.Location.getHost();
		
		
		public static LinkType parse(String href) {
			if (href.matches(GOOGLE_SCHOLAR_PATTERN)){
				return LinkType.GOOGLE_SCHOLAR; 
			}

			if (href.startsWith(LOCAL_PATTERN)){
				return LinkType.INTERNAL_LINK; 
			}
			
			return EXTERNAL_LINK; 
		}
	}

	private class DownloadDocCommand implements Command {

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

	private final Command emailDocumentCommand = new Command() {

		@Override
		public void execute() {
			emailDocument();
		}
	};

	private class DocMenu extends MenuBar {

		private DownloadDocCommand rtfDownloadCommand = new DownloadDocCommand(RTF_MIME_TYPE);
		private DownloadDocCommand docxDownloadCommand = new DownloadDocCommand(DOCX_MIME_TYPE);
		private DownloadDocCommand docDownloadCommand = new DownloadDocCommand(DOC_MIME_TYPE);
		private MenuItem editDoc;
		private MenuItem viewDoc;

		private Command editCommand = new Command() {

			@Override
			public void execute() {
				createDew();
			}

		};
		private Command viewCommand = new Command() {

			@Override
			public void execute() {
				tryToCloseEditor();
			}

		};

		public DocMenu() {
			super();

			MenuBar fileMenu = new MenuBar(true);

			// downloadMenuTop.addItem("<img src='poc/images/word-16.gif'/><u>Download</u>",
			// true, downloadMenu);
			this.addItem("File", true, fileMenu);
			this.addItem("Documents", true, DocumentsMenuBar.getDocumentsMenuBar());

			MenuBar downloadMenu = new MenuBar(true);
			editDoc = new MenuItem("Edit", editCommand);
			viewDoc = new MenuItem("View", viewCommand);
			viewDoc.setVisible(false);
			fileMenu.addItem(editDoc);
			fileMenu.addItem(viewDoc);

			fileMenu.addItem("Download", true, downloadMenu);
			//fileMenu.addItem("Email", true, emailDocumentCommand);

			MenuItem fireRtf = new MenuItem("rtf format", rtfDownloadCommand);
			MenuItem fireDocx = new MenuItem("docx format", docxDownloadCommand);
			MenuItem fireDoc = new MenuItem("doc format", docDownloadCommand);
			// fireDocx.addStyleName("docHeaderMenuItem"); some troubles here

			downloadMenu.addItem(fireRtf);
			downloadMenu.addItem(fireDocx);
			downloadMenu.addItem(fireDoc);
		}

		public void setVisibleEditItem(boolean visible) {
			editDoc.setVisible(visible);
		}

		public void setId(String id) {
			rtfDownloadCommand.setId(id);
			docxDownloadCommand.setId(id);
			docDownloadCommand.setId(id);
		}

		public void toogleEditItemVisibility() {
			boolean isViewVisible = viewDoc.isVisible();
			boolean isEditVisible = editDoc.isVisible();
			viewDoc.setVisible(!isViewVisible);
			editDoc.setVisible(!isEditVisible);

		}

	}

	static class DocFrame extends ScrollPanel {

		public DocFrame() {
			super();
			setStyleName("docFrame");
			// getElement().setAttribute("frameBorder", "0"); // for IE
		}
	}

	private ClickHandler docClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			menu.removeFromParent();
			headerPnl.insert(menu, 0);
			menu.selectItem(null);
		}
	};

	private static class LinkWarningDialog extends AbstractInfoDialog {
		private Button okButton = new Button("Open in new window", this);
		private Button cancelButton = new Button("Cancel", this);
		
		String href;
		
		public LinkWarningDialog(String href) {
			super("External link detected");
			this.href = href;
			HTML link = new HTML(href);
			link.addStyleName("external-link");
			addContents(link);
			addButton(okButton);
			addButton(cancelButton);
			setWidth("330px");
		}
		public void onClick(ClickEvent clkEvt) {
			hide();
			if(clkEvt.getSource() == okButton) {
				Window.open(href, null, null);
			}
		}
	}	

	private class SaveWarningDialog extends AbstractInfoDialog {

		private Button cancel, save, doNotSave;

		public SaveWarningDialog(String docTitle) {
			super(null);
			Label questionLabel = new Label("Do you want to save changes you made in the document:", true);

			Label docTitleLabel = new Label("'" + docTitle + "' ?", true);
			docTitleLabel.setStyleName("saveWarningDialog-title");

			questionLabel.setStyleName("saveWarningDialog-question");
			Label warningLabel = new Label("Your changes will be lost if you don't save them.", true);
			warningLabel.setStyleName("saveWarningDialog-warning");

			doNotSave = new Button("Don't save", this);
			cancel = new Button("Cancel", this);
			save = new Button("Save...", this);

			addButton(doNotSave);
			addButton(cancel);
			addButton(save);

			addContents(questionLabel);
			addContents(docTitleLabel);
			addContents(warningLabel);
			this.setWidth("330px");

		}

		public void onClick(ClickEvent clkEvt) {
			hide();
			if(clkEvt.getSource() == save) {
				updateDocContent(getAsyncCallback());
			}
			if(clkEvt.getSource() == doNotSave) {
				staticMode();
			}
		}

		private AsyncCallback<Payload> getAsyncCallback() {
			AsyncCallback<Payload> asyncCallback = new AsyncCallback<Payload>() {

				@Override
				public void onSuccess(Payload result) {
					Notifier.get().showFor(result, null);
					staticMode();

				}

				@Override
				public void onFailure(Throwable caught) {
					Notifier.get().showFor(caught);
				}

			};
			return asyncCallback;
		}
	}

	/**
	 * docView
	 */
	private final DockLayoutPanel pnl = new DockLayoutPanel(Unit.PX);
	/**
	 * docHeader
	 */
	private final DocMenu menu = new DocMenu();

	private final FlowPanel headerPnl = new FlowPanel();
	private final FlowPanel docPnl = new FlowPanel();

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
		headerPnl.add(menu);
		pnl.addNorth(headerPnl, 50);

		frame = new DocFrame();
		frame.addStyleName("documentContainer");
		docPnl.add(frame);
		pnl.add(docPnl);

		initWidget(pnl);

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
		var root = tsapi.@com.tabulaw.client.app.ui.DocViewer::getDocBody()();
		try {
		//if(framedoc !== rng.getDocument()) alert('range.document != framedoc!');
		//alert('framedoc: ' + framedoc + ', rng.getDocument(): ' + rng.getDocument());
		mark  = new $wnd.Mark(root, rng);
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
	public JavaScriptObject getDocBody() {
		return frame.getElement();
	}

	// /*-{
	// var frameId = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
	// var frame = $wnd.goog.dom.$(frameId);
	// var fbody = frame.contentDocument? frame.contentDocument.body :
	// frame.contentWindow.document.body;
	// return fbody;
	// return $wnd.body;
	// }-*/;

	public DocRef getModel() {
		return doc;
	}

	/**
	 * Sets the document model data.
	 * <p>
	 * NOTE: <code>null</code> model is supported.
	 * @param doc
	 * @param docContent
	 */
	public void setModel(DocRef doc, DocContent docContent) {

		if(this.doc != null) {
			shutdownDocFrame(this.doc.getId());
		}

		this.doc = doc;

		// disallow doc editing for case type docs
		menu.setVisibleEditItem(doc != null && doc.getReference() == null);

		frame.getElement().setId(getFrameId());

		if(doc != null) {
			initDocFrame();
			menu.setId(doc.getId());
		}

		// set html content directly or via url?
		String htmlContent = docContent == null ? null : docContent.getHtmlContent();
		if(!StringUtil.isEmpty(htmlContent)) {
			// html content
			// frame.setUrl("");
			setDocHtml(htmlContent);
			addHrefHandlers();
			Log.debug("DocViewer html content set directly");
		}
		else {
			throw new IllegalStateException("No doc content specified");
		}
	}

	private void addHrefHandlers() {
		NodeList<com.google.gwt.dom.client.Element> anchors = frame.getElement().getElementsByTagName("a");
		for (int i = 0; i < anchors.getLength(); i++) {
			Element elementToListen = (Element) anchors.getItem(i);
			DOM.setEventListener(elementToListen, new EventListener() {
				@Override
				public void onBrowserEvent(Event event) {
					// TODO Auto-generated method stub
					EventTarget target = event.getEventTarget();
					if (AnchorElement.is(target)) {
						AnchorElement anchor = target.cast();
						LinkType linkType = LinkType.parse(anchor.getHref());
						switch (linkType) {
						case INTERNAL_LINK:
							break;
						case EXTERNAL_LINK:
							event.preventDefault();
							LinkWarningDialog dialog = new LinkWarningDialog(anchor.getHref());
							dialog.center();
							break;
						case GOOGLE_SCHOLAR:
							event.preventDefault();
							showInProgressMessage("Google Scholar" , "Loading document");
							Command callback = new Command() {
								
								@Override
								public void execute() {
									clearInProgressMessage();									
								}
							};
							
							GoogleScholarDocFetcher.fetchGoogleScholarDoc(anchor.getHref(), DocViewer.this, frame, callback );
							break;
						}
					}
				}
			});
			DOM.sinkEvents(elementToListen, Event.ONCLICK);

		}

	}

	public String getDocHtml() {
		return frame.getElement().getInnerHTML();
	}

	public void setDocHtml(String html) {
		this.frame.getElement().setInnerHTML(html);
	}

	// public native String getDocHtml() /*-{
	// var fid = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
	// var frame = $wnd.goog.dom.$(fid);
	// var fbody = frame.contentDocument? frame.contentDocument.body :
	// frame.contentWindow.document.body;
	// return fbody.innerHTML;
	// }-*/;

	// public native void setDocHtml(String html) /*-{
	// var fid = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
	// var frame = $wnd.goog.dom.$(fid);
	// var fbody = frame.contentDocument? frame.contentDocument.body :
	// frame.contentWindow.document.body;
	// //$wnd.alert('html: ' + html);
	// fbody.innerHTML = html;
	// }-*/;

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

	public void updateDocContent() {
		AsyncCallback<Payload> asyncCallback = new AsyncCallback<Payload>() {

			@Override
			public void onSuccess(Payload result) {
				Notifier.get().showFor(result, null);

			}

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}
		};
		updateDocContent(asyncCallback);
	}

	public void updateDocContent(AsyncCallback<Payload> asyncCallback) {
		// update screen
		String docHtml = dew.getHTML();
		setDocHtml(docHtml);
		// update the cache
		try {
			ClientModelCache.get().remove(new ModelKey(EntityType.DOC_CONTENT.name(), doc.getId()), null);
		}
		catch(EntityNotFoundException enf) {
			// do nothing
		}
		// persist to server
		Poc.getUserDataService().updateDocContent(doc.getId(), docHtml, asyncCallback);

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
	 * @return The id assigned to the doc frame element or <code>null</code> if
	 *         the document model data has not been set.
	 */
	private String getFrameId() {
		return doc == null ? null : "docframe_" + doc.getId();
	}

	private boolean isEditMode() {
		return dew != null && dew.isVisible();
	}

	private boolean isContentChanged() {
		String docHtml = getDocHtml(); // html from div
		return !docHtml.equals(dew.getHTML());
	}

	private void createDew() {
		if(dew == null) {

			// wire up save/cancel buttons
			assert btnSave == null;
			assert btnCancel == null;
			btnSave = new PushButton("Save", new ClickHandler() {

				@Override
				public void onClick(ClickEvent clkEvt) {
					if(doc == null) return;
					// save the doc
					updateDocContent();
				}
			});
			btnSave.setTitle("Save Document");
			btnCancel = new PushButton("View", new ClickHandler() {

				@Override
				public void onClick(ClickEvent clkEvt) {
					tryToCloseEditor();
				}
			});
			btnCancel.setTitle("Revert Document");

			dew = new DocEditWidget();
			dew.setVisible(false);
			docPnl.add(dew);
			dew.addClickHandler(docClickHandler);
		}

		if(!dew.isVisible()) {
			editMode();
		}

	}

	private void tryToCloseEditor() {
		if(isContentChanged()) {
			SaveWarningDialog dialogbox = new SaveWarningDialog(doc.getTitle());
			dialogbox.center();
		}
		else {
			staticMode();
		}
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

		headerPnl.add(dew.getEditToolBar());
		menu.toogleEditItemVisibility();
		Poc.getNavCol().addWidget(btnSave);
		Poc.getNavCol().addWidget(btnCancel);

		autosave.scheduleRepeating(5 * 60 * 1000);

		ValueChangeEvent.fire(this, ViewMode.EDIT);
	}

	/**
	 * Sets the mode to static.
	 */
	private void staticMode() {
		autosave.cancel();
		pnl.addStyleDependentName("static");
		pnl.removeStyleDependentName("edit");

		// so qb doc col can know which doc mode we are in
		RootPanel.get().addStyleName("doc-static");
		RootPanel.get().removeStyleName("doc-edit");
		frame.setVisible(true);
		if(dew != null) {
			dew.setVisible(false);
			menu.toogleEditItemVisibility();
			dew.getEditToolBar().removeFromParent();
			Poc.getNavCol().removeWidget(btnSave);
			Poc.getNavCol().removeWidget(btnCancel);
			ValueChangeEvent.fire(this, ViewMode.STATIC);
		}
	}

	private final Timer autosave = new Timer() {

		@Override
		public void run() {
			if(doc != null) {
				Notifier.get().info("Document autosave");
				updateDocContent();
			}
		}
	};

	private Dialog inProgressMessage = null;

	private void emailDocument() {
		if(doc != null) {
			emailDocument(doc);
		}
	}

	void emailDocument(final DocRef doc) {
		if(inProgressMessage == null) {
			showInProgressMessage("Email Document" , "Document is sending via email");
			String id = doc.getId();
			String url = "/services/docments/" + id + "/send_by_email?sessionToken=" + Cookies.getCookie("JSESSIONID");
			final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, url);
			rb.setCallback(new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					clearInProgressMessage();
					try {
						if("OK".equalsIgnoreCase(response.getStatusText())) {
							Document xml = XMLParser.parse(response.getText());
							String value = xml.getDocumentElement().getNodeName();
							if("success".equalsIgnoreCase(value)) {
								Notifier.get().info("Document has been sent via email.");
							}
							else {
								onEmailDocumentError();
							}
						}
						else {
							onEmailDocumentError();
						}
					}
					catch(Exception e) {
						onEmailDocumentError();
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					onEmailDocumentError();
				}
			});
			try {
				rb.send();
			}
			catch(RequestException e) {
				onEmailDocumentError();
			}
		}
	}
	private void showInProgressMessage(String header, String msg) {
		if(inProgressMessage == null) {
			inProgressMessage = new Dialog(null, true);
			inProgressMessage.setText(header);
			inProgressMessage.add(new HTML(msg + "<img src=\"images/ajax-loader.gif\">"));
			inProgressMessage.setGlassEnabled(true);
			inProgressMessage.center();
		}
		
	}
	private void clearInProgressMessage() {
		if(inProgressMessage != null) {
			inProgressMessage.hide();
			inProgressMessage = null;
		}
	}

	private void onEmailDocumentError() {
		clearInProgressMessage();
		Notifier.get().error("Unable to send document via email.");
	}
}
