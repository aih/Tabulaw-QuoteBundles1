/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.tabulaw.client.app.view.ManageClausesView;
import com.tabulaw.client.view.IViewInitializer;
import com.tabulaw.client.view.StaticViewInitializer;

/**
 * @author jpk
 */
public class ManageClausesNavButton extends AbstractNavButton {

	static final IViewInitializer vi = new StaticViewInitializer(ManageClausesView.klas);

	public ManageClausesNavButton() {
		super("Manage Clauses", null, null);
		setTitle("Manage Clause Bundles and constituent Clauses");
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return vi;
	}
}
