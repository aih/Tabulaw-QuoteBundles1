/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.client.ui.nav;

import com.tll.client.mvc.view.IViewInitializer;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.tabulaw.client.view.DocumentsView;

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
