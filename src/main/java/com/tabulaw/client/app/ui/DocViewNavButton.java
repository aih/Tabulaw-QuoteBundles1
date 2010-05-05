/**
 * The Logic Lab
 * @author jpk
 * @since Feb 26, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.common.model.ModelKey;

/**
 * Routes to an open document when clicked.
 * @author jpk
 */
public class DocViewNavButton extends AbstractNavButton {

	static class Styles {

		/**
		 * The secondary style used for document nav buttons.
		 */
		public static final String DOC = "doc";
	} // Styles

	private final ModelKey documentKey;

	/**
	 * Constructor
	 * @param docKey The document model key
	 */
	public DocViewNavButton(ModelKey docKey) {
		super(docKey.getName(), Styles.DOC);
		this.documentKey = docKey;
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return new DocViewInitializer(documentKey);
	}

	public ModelKey getDocKey() {
		return documentKey;
	}
}
