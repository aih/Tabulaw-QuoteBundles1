/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.ui.nav;

import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.view.ManageUsersView;

/**
 * @author jpk
 */
public class UsersNavButton extends AbstractNavButton {

	static final IViewInitializer vi = new StaticViewInitializer(ManageUsersView.klas);

	public UsersNavButton() {
		super("Usres", null);
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return vi;
	}

}
