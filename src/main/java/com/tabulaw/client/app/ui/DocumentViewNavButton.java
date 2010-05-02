/**
 * The Logic Lab
 * @author jpk
 * @since Feb 26, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.client.app.ui.view.DocumentViewInitializer;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.common.model.ModelKey;

/**
 * Routes to an open document when clicked.
 * @author jpk
 */
public class DocumentViewNavButton extends AbstractNavButton {

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
	public DocumentViewNavButton(ModelKey docKey) {
		super(docKey.getName(), Styles.DOC);
		this.documentKey = docKey;
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return new DocumentViewInitializer(documentKey);
	}

	public ModelKey getDocKey() {
		return documentKey;
	}
}
