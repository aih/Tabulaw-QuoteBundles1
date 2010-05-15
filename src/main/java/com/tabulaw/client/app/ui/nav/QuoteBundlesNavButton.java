/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.view.QuoteBundlesView;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.StaticViewInitializer;


/**
 * Shows the Quote Bundles view when clicked.
 * @author jpk
 */
public class QuoteBundlesNavButton extends AbstractNavButton {

	static final IViewInitializer vi = new StaticViewInitializer(QuoteBundlesView.klas);
	
	public QuoteBundlesNavButton() {
		super("Quote Bundles", "bundles", Resources.INSTANCE.quoteBundleIcon());
		setTitle("View Quote Bundles");
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return vi;
	}
}
