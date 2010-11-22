/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.UsersWidget;
import com.tabulaw.client.ui.ImageButton;
import com.tabulaw.client.ui.view.ViewToolbar;
import com.tabulaw.client.view.StaticViewInitializer;
import com.tabulaw.client.view.ViewClass;

/**
 * @author jpk
 */
public class UsersView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "users";
		}

		@Override
		public UsersView newView() {
			return new UsersView();
		}
	}
	
	static class NewUserButton extends ImageButton {

		public NewUserButton() {
			super(Resources.INSTANCE.plus(), "New User");
			setTitle("Create a user...");
		}
	}

	private final NewUserButton newUserButton;

	private final UsersWidget widget;
	
	/**
	 * Constructor
	 */
	public UsersView() {
		super();
		widget = new UsersWidget();
		
		newUserButton = new NewUserButton();
		newUserButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				widget.newUserMode();
			}
		});

	}

	@Override
	protected void decorateToolbar(ViewToolbar toolbar) {
		super.decorateToolbar(toolbar);
		toolbar.add(newUserButton);
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
