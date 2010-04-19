/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.ui.nav;

import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.view.QuoteBundlesView;


/**
 * Shows the Quote Bundles view when clicked.
 * @author jpk
 */
public class QuoteBundlesNavButton extends AbstractNavButton {

	static class Styles {

		public static final String BUNDLES = "bundles";
	}

	static final IViewInitializer vi = new StaticViewInitializer(QuoteBundlesView.klas);
	
	public QuoteBundlesNavButton() {
		super("Quote Bundles", Styles.BUNDLES);
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return vi;
	}
}
