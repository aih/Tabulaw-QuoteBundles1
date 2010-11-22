/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 24, 2009
 */
package com.tabulaw.client.view;

import com.google.gwt.user.client.Command;

/**
 * ShowViewRequest
 * @author jpk
 */
public final class ShowViewRequest extends AbstractViewRequest {

	/**
	 * The view initializer responsible for providing the {@link ViewKey}.
	 */
	private final IViewInitializer init;

	/**
	 * Constructor - Use for dynamic views that will have default view options.
	 * @param init
	 * @param onCompleteCommand optional command to get executed upon completion
	 *        of showing the view
	 */
	public ShowViewRequest(IViewInitializer init, Command onCompleteCommand) {
		super(onCompleteCommand);
		this.init = init;
	}

	/**
	 * Constructor - Use for dynamic views that will have default view options.
	 * @param init
	 */
	public ShowViewRequest(IViewInitializer init) {
		this(init, null);
	}

	/**
	 * Constructor - Use for static views that will have default view options.
	 * @param viewClass
	 */
	public ShowViewRequest(ViewClass viewClass) {
		this(new StaticViewInitializer(viewClass));
	}

	@Override
	public final boolean addHistory() {
		return true;
	}

	/**
	 * @return The view initializer.
	 */
	public IViewInitializer getViewInitializer() {
		return init;
	}

	@Override
	public ViewKey getViewKey() {
		return init.getViewKey();
	}
}
