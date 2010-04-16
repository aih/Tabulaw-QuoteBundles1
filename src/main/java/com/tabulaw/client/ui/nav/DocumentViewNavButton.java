/**
 * The Logic Lab
 * @author jpk
 * @since Feb 26, 2010
 */
package com.tabulaw.client.ui.nav;

import com.tabulaw.client.view.DocumentViewInitializer;
import com.tll.client.mvc.view.IViewInitializer;
import com.tll.common.model.ModelKey;

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

	private ModelKey documentKey;

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
