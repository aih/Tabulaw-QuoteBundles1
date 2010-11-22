/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Jan 13, 2008
 */
package com.tabulaw.client.view;

import com.google.gwt.user.client.Command;

/**
 * AbstractViewRequest - common base class for all {@link IViewRequest} types.
 * @author jpk
 */
public abstract class AbstractViewRequest extends AbstractViewKeyProvider implements IViewRequest {

	private final Command onCompleteCommand;

	/**
	 * Constructor
	 * @param onCompleteCommand optional command that gets executed upon
	 *        completion of this request.
	 */
	protected AbstractViewRequest(Command onCompleteCommand) {
		super();
		this.onCompleteCommand = onCompleteCommand;
	}

	/**
	 * @return <code>true</code> if history should be updated with a view token,
	 *         <code>false</code> if history is NOT to be updated.
	 *         <p>
	 *         Default returns <code>true</code>. Concrete impls may override.
	 */
	public boolean addHistory() {
		return true;
	}

	@Override
	public final Command onCompleteCommand() {
		return onCompleteCommand;
	}

	@Override
	public final String toString() {
		String s = "";
		final ViewKey viewKey = getViewKey();
		if(viewKey != null) {
			s += viewKey.toString();
		}
		return s;
	}
}
