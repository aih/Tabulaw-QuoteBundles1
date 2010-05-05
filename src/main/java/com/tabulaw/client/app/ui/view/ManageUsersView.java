/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.ui.AbstractNavButton;
import com.tabulaw.client.app.ui.ManageUsersWidget;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.mvc.view.ViewClass;

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
	
	static class NewUserButton extends AbstractNavButton {
		
		public NewUserButton() {
			super("New User", Styles.PLUS);
			setClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// TODO
				}
			});
		}

		@Override
		protected String getTitleText(String buttonText) {
			return "Create a new User...";
		}

		@Override
		protected IViewInitializer getViewInitializer() {
			return null;
		}
	}
	
	private final Widget[] navColWidgets;
	
	private final ManageUsersWidget widget;
	
	/**
	 * Constructor
	 */
	public ManageUsersView() {
		super();
		widget = new ManageUsersWidget();
		
		navColWidgets = new Widget[] {
			new NewUserButton(),
		};
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
	public Widget[] getNavColWidgets() {
		return navColWidgets; 
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
