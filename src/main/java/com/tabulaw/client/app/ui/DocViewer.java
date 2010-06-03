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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.util.StringUtil;

/**
 * Displays a single document either statically (default) or in a rich text area
 * (edit mode).
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "loaded" when the document view is
 * set to edit mode.
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "edit" when the document view is
 * set to edit mode.
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "static" when the document view is
 * set to read-only mode.
 * @author jpk
 */
public class DocViewer extends Composite implements HasValueChangeHandlers<DocViewer.ViewMode> {

	public static class Styles {

		/**
		 * Style for the parent vertical panel
		 */
		public static final String DOC_VIEW = "docView";

		/**
		 * Row just above the document content
		 */
		public static final String DOC_HEADER = "docHeader";

		/**
		 * Displays the document title
		 */
		public static final String DOC_HEADER_LABEL = "docHeaderLabel";

		/**
		 * Parent container to the doc content.
		 */
		public static final String DOC_CONTAINER = "docContainer";

		/**
		 * Identifies the html frame tag containing the doc's static html content.
		 */
		public static final String DOC_FRAME = "docFrame";

		/**
		 * Secondary style name indicating doc view is in non-edit (static) mode.
		 */
		public static final String STATIC = "static";

		/**
		 * Secondary style name indicating doc view is in edit mode.
		 */
		public static final String EDIT = "edit";
	} // Styles

	public static enum ViewMode {
		EDIT,
		STATIC;
	}

	static class DocViewHeader extends Composite {

		private final FlowPanel pnl = new FlowPanel();
		private final HTML html = new HTML();

		private final Image imgEdit = new Image(Resources.INSTANCE.edit());
		private final Image imgExport = new Image(Resources.INSTANCE.permalink());

		public DocViewHeader() {
			super();
			pnl.setStyleName(Styles.DOC_HEADER);

			imgEdit.setStyleName("imgEdit");
			imgEdit.setTitle("Edit document");

			imgExport.setStyleName("imgExport");
			imgExport.setTitle("Export to MS Word");

			html.setStyleName(Styles.DOC_HEADER_LABEL);
			pnl.add(html);
			pnl.add(imgEdit);
			pnl.add(imgExport);

			initWidget(pnl);
		}

		public void insert(Widget w, int beforeIndex) {
			pnl.insert(w, 0);
		}
	}

	static class DocFrame extends Frame {

		public DocFrame() {
			super();
			setStyleName(Styles.DOC_FRAME);
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
	 * Constructor
	 */
	public DocViewer() {
		super();

		pnl.setStylePrimaryName(Styles.DOC_VIEW);
		pnl.add(header);

		container.addStyleName(Styles.DOC_CONTAINER);
		pnl.add(container);

		frame = new DocFrame();
		container.add(frame);

		initWidget(pnl);

		header.imgExport.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO impl
				Window.alert("TODO impl");
			}
		});

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

	public DocRef getModel() {
		return doc;
	}

	/**
	 * @return The id assigned to the iframe element or <code>null</code> if the
	 *         document model data has not been set.
	 */
	private String getFrameId() {
		return doc == null ? null : "docframe_" + doc.getId();
	}

	/**
	 * @return the DOM iframe body ref of the contained document.
	 */
	public native JavaScriptObject getDocBody() /*-{
		var frameId = this.@com.tabulaw.client.app.ui.DocViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(frameId);
		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		return fbody;
	}-*/;

	/**
	 * Sets the document model data.
	 * <p>
	 * NOTE: <code>null</code> model is supported.
	 * @param doc
	 */
	public void setModel(DocRef doc) {
		this.doc = doc;

		// header
		String html = doc == null ? "" : doc.getTitle();
		header.html.setHTML("<p>" + html + "</p>");

		// disallow doc editing for case type docs
		header.imgEdit.setVisible(doc != null && doc.getCaseRef() == null);

		frame.getElement().setId(getFrameId());
		
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

	private boolean isEditMode() {
		return dew != null && dew.isVisible();
	}

	/**
	 * Sets the mode to edit.
	 */
	private void editMode() {
		pnl.removeStyleDependentName(Styles.STATIC);
		pnl.addStyleDependentName(Styles.EDIT);

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
		pnl.addStyleDependentName(Styles.STATIC);
		pnl.removeStyleDependentName(Styles.EDIT);

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

	public Widget[] getNavColWidgets() {
		return isEditMode() ? new Widget[] {
			btnSave, btnCancel
		} : null;
	}
}
