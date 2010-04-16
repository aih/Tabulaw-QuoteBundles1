/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.ui.nav;

import com.tabulaw.client.view.DocumentsView;
import com.tll.client.mvc.view.IViewInitializer;
import com.tll.client.mvc.view.StaticViewInitializer;

/**
 * Shows the Document listing view when clicked.
 * @author jpk
 */
public class DocumentsNavButton extends AbstractNavButton {

	static class Styles {

		public static final String DOCS = "docs";
	}

	static final IViewInitializer vi = new StaticViewInitializer(DocumentsView.klas);

	public DocumentsNavButton() {
		super("Documents", Styles.DOCS);
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return vi;
	}

}
