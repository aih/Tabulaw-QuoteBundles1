/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.ui.AbstractButton;
import com.tabulaw.client.app.ui.UsersWidget;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.mvc.view.ViewClass;

/**
 * @author jpk
 */
public class UsersView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "manageUsers";
		}

		@Override
		public UsersView newView() {
			return new UsersView();
		}
	}
	
	class NewUserButton extends AbstractButton {
		
		public NewUserButton() {
			super("New User", "plus");
			setClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					widget.newUserMode();
				}
			});
		}

		@Override
		protected String getTitleText(String buttonText) {
			return "Create a new User...";
		}
	}
	
	private final Widget[] navColWidgets;
	
	private final UsersWidget widget;
	
	/**
	 * Constructor
	 */
	public UsersView() {
		super();
		widget = new UsersWidget();
		
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
