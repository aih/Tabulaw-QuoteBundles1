/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.tabulaw.client.app.ui.view.ManageUsersView;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.StaticViewInitializer;

/**
 * @author jpk
 */
public class UsersNavButton extends AbstractNavButton {

	static final IViewInitializer vi = new StaticViewInitializer(ManageUsersView.klas);

	public UsersNavButton() {
		super("Users", null);
	}

	@Override
	protected IViewInitializer getViewInitializer() {
		return vi;
	}

}
