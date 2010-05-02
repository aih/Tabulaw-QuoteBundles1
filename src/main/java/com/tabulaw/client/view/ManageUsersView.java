/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.view;

import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.mvc.view.ViewClass;
import com.tabulaw.client.ui.ManageUsersWidget;

/**
 * @author jpk
 */
public class ManageUsersView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "manageUsers";
		}

		@Override
		public ManageUsersView newView() {
			return new ManageUsersView();
		}
	}
	
	private final ManageUsersWidget widget;
	
	/**
	 * Constructor
	 */
	public ManageUsersView() {
		super();
		widget = new ManageUsersWidget();
	}

	@Override
	public String getLongViewName() {
		return "Users";
	}

	@Override
	public ViewClass getViewClass() {
		return klas;
	}

	@Override
	protected void doInitialization(StaticViewInitializer initializer) {
		super.doInitialization(initializer);
		addWidget(widget);
	}

	@Override
	protected final void doDestroy() {
		// no-op
	}
	
	@Override
	protected void doRefresh() {
		super.doRefresh();
		widget.refresh();
	}

}
