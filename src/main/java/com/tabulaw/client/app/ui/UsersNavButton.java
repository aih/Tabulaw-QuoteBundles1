/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.client.app.ui.view.UsersView;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.IViewInitializerProvider;
import com.tabulaw.client.mvc.view.StaticViewInitializer;

/**
 * @author jpk
 */
	public class UsersNavButton extends AbstractButton implements IViewInitializerProvider {

	static final IViewInitializer vi = new StaticViewInitializer(UsersView.klas);

	public UsersNavButton() {
		super("Users", null);
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return vi;
	}

}
