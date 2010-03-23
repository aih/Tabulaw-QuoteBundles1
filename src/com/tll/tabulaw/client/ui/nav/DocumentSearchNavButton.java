/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tll.tabulaw.client.ui.nav;

import com.tll.client.mvc.view.IViewInitializer;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.tabulaw.client.view.DocumentSearchView;

/**
 * Shows the Document search view when clicked.
 * @author jpk
 */
public class DocumentSearchNavButton extends AbstractNavButton {

	static class Styles {

		public static final String DOC_SEARCH = "docSearch";
	}

	static final IViewInitializer vi = new StaticViewInitializer(DocumentSearchView.klas);

	public DocumentSearchNavButton() {
		super("Document Search", Styles.DOC_SEARCH);
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return vi;
	}
}
