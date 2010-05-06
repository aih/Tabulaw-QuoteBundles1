/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.client.app.ui.view.DocsView;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.IViewInitializerProvider;
import com.tabulaw.client.mvc.view.StaticViewInitializer;

/**
 * Shows the Document listing view when clicked.
 * @author jpk
 */
public class DocsNavButton extends AbstractButton implements IViewInitializerProvider {

	static class Styles {

		public static final String DOCS = "docs";
	}

	static final IViewInitializer vi = new StaticViewInitializer(DocsView.klas);

	public DocsNavButton() {
		super("Documents", Styles.DOCS);
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return vi;
	}
}
