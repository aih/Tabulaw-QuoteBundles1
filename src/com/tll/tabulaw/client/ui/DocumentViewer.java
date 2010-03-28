/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tll.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.IHasModel;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.Poc;

/**
 * Displays a single document either statically (default) or in a rich text area
 * (edit mode).
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "edit" when the document view is
 * set to edit mode.
 * <p>
 * Fires a {@link ValueChangeEvent} of value: "static" when the document view is
 * set to read-only mode.
 * @author jpk
 */
public class DocumentViewer extends Composite implements IHasModel, DoubleClickHandler, HasValueChangeHandlers<DocumentViewer.ViewMode> {

	public static enum ViewMode {
		EDIT,
		STATIC;
	}

	static class DocViewHeader extends Composite {

		private final FlowPanel pnl = new FlowPanel();
		private final DblClickHTML html = new DblClickHTML();

		public DocViewHeader() {
			super();
			pnl.setStyleName(Styles.DOC_HEADER);
			html.setStyleName(Styles.DOC_HEADER_LABEL);
			pnl.add(html);
			initWidget(pnl);
		}

		public void insert(Widget w, int beforeIndex) {
			pnl.insert(w, 0);
		}
	}

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
		 * The scrollable area containing the document body.
		 */
		public static final String DOC_PORTAL = "docPortal";

		/**
		 * Identifies the html tag containing the actual document data.
		 */
		public static final String DOC_CONTAINER = "docContainer";

		/**
		 * Indicates doc view is in edit mode.
		 */
		public static final String EDIT = "edit";
	} // Styles

	/**
	 * docView
	 */
	private final FlowPanel pnl = new FlowPanel();

	/**
	 * docHeader
	 */
	private final DocViewHeader header = new DocViewHeader();

	/**
	 * portal
	 */
	private final FlowPanel portal = new FlowPanel();

	/**
	 * The iframe tag in which the doc is loaded.
	 */
	private final Frame frame;

	private DocEditWidget dew;

	private PushButton btnSave, btnCancel;

	private Model mDocument;

	/**
	 * Constructor
	 */
	public DocumentViewer() {
		super();

		header.html.addDoubleClickHandler(this);

		pnl.setStyleName(Styles.DOC_VIEW);
		pnl.add(header);

		portal.addStyleName(Styles.DOC_PORTAL);
		pnl.add(portal);

		frame = new Frame();
		// frame.getElement().setId(Styles.DOC_CONTAINER_ID);
		frame.setStyleName(Styles.DOC_CONTAINER);
		frame.getElement().setAttribute("frameBorder", "0"); // for IE
		portal.add(frame);

		initWidget(pnl);
	}

	public Model getModel() {
		return mDocument;
	}

	/**
	 * @return The id assigned to the iframe element or <code>null</code> if the
	 *         document model data has not been set.
	 */
	public String getFrameId() {
		return mDocument == null ? null : "docframe_" + Integer.toString(mDocument.getKey().hashCode());
	}

	/**
	 * Sets the document model data.
	 * @param mDocument
	 */
	public void setModel(Model mDocument) {
		if(mDocument == null) throw new NullPointerException();
		this.mDocument = mDocument;

		// header
		String html = mDocument.asString("title");
		header.html.setHTML("<p>" + html + "</p>");
		header.html.setTitle("Double click to edit");

		// doc content
		frame.getElement().setId(getFrameId());
		String hash = mDocument.asString("hash");
		Log.debug("Setting document content in iframe for doc: " + hash);
		frame.setUrl("doc?id=" + hash);
	}

	public native String getDocHtml() /*-{
		var fid = this.@com.tll.tabulaw.client.ui.DocumentViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(fid);
		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		return fbody.innerHTML;
	}-*/;

	public native void setDocHtml(String html) /*-{
		var fid = this.@com.tll.tabulaw.client.ui.DocumentViewer::getFrameId()();
		var frame = $wnd.goog.dom.$(fid);
		var fbody = frame.contentDocument? frame.contentDocument.body : frame.contentWindow.document.body;
		//$wnd.alert('html: ' + html);
		fbody.innerHTML = html;
	}-*/;

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ViewMode> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		if(dew == null) {

			// wire up save/cancel buttons
			assert btnSave == null;
			assert btnCancel == null;
			btnSave = new PushButton("Save", new ClickHandler() {

				@Override
				public void onClick(ClickEvent clkEvt) {
					// save the doc
					setDocHtml(dew.getHTML());
					staticMode();
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
			portal.add(dew);
		}

		if(!dew.isVisible()) {
			editMode();
		}
	}

	public DocEditWidget getDocEditWidget() {
		return dew;
	}

	/**
	 * Sets the mode to edit.
	 */
	private void editMode() {
		pnl.addStyleName(Styles.EDIT);
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
		frame.setVisible(true);
		dew.setVisible(false);
		pnl.removeStyleName(Styles.EDIT);

		dew.getEditBar().removeFromParent();
		header.html.setTitle("Double click to edit");

		Poc.getNavCol().removeWidget(btnSave);
		Poc.getNavCol().removeWidget(btnCancel);

		ValueChangeEvent.fire(this, ViewMode.STATIC);
	}
}
