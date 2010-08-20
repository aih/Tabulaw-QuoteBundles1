/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.tabulaw.client.app.view.UsersView;
import com.tabulaw.client.view.IViewInitializer;
import com.tabulaw.client.view.StaticViewInitializer;

/**
 * @author jpk
 */
	public class UsersNavButton extends AbstractNavButton {

	static final IViewInitializer vi = new StaticViewInitializer(UsersView.klas);

	public UsersNavButton() {
		super("Users", null, null);
		setTitle("View Users");
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return vi;
	}
}
