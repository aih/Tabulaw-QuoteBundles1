/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tll.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tll.client.model.IHasModel;
import com.tll.common.model.Model;

/**
 * Displays a single document allowng for quotes to be generated from user text
 * selections.
 * @author jpk
 */
public class DocumentViewer extends Composite implements IHasModel {

	public static class Styles {

		/**
		 * Style for the parent vertical panel
		 */
		public static final String DOC_VIEW = "docView";

		/**
		 * The document header (always visible)
		 */
		public static final String DOC_HEADER = "docHeader";

		/**
		 * The scrollable area containing the document body.
		 */
		public static final String DOC_PORTAL = "docPortal";

		/**
		 * Identifies the html tag containing the actual document data.
		 */
		public static final String DOC_CONTAINER = "docContainer";

	} // Styles

	/**
	 * docView
	 */
	private final VerticalPanel vp = new VerticalPanel();

	/**
	 * docHeader
	 */
	private final HTML header = new HTML();

	/**
	 * docHeader
	 */
	private final SimplePanel sp = new SimplePanel();

	/**
	 * The iframe tag in which the doc is loaded.
	 */
	private final Frame frame;

	private Model mDocument;

	/**
	 * Constructor
	 */
	public DocumentViewer() {
		super();

		header.setStyleName(Styles.DOC_HEADER);

		vp.setStyleName(Styles.DOC_VIEW);
		vp.add(header);

		sp.addStyleName(Styles.DOC_PORTAL);
		vp.add(sp);

		frame = new Frame();
		// frame.getElement().setId(Styles.DOC_CONTAINER_ID);
		frame.setStyleName(Styles.DOC_CONTAINER);
		frame.getElement().setAttribute("frameBorder", "0"); // for IE
		sp.add(frame);

		initWidget(vp);
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
		String html = mDocument.asString("case.parties");
		header.setHTML("<p>" + html + "</p>");

		// body
		String url = mDocument.asString("case.url");
		Log.debug("Setting document content in iframe for url: " + url);
		frame.getElement().setId(getFrameId());
		frame.setUrl(url);
	}
}
