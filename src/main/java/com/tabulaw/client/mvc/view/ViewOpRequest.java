/**
 * The Logic Lab
 * @author jpk
 * Apr 5, 2008
 */
package com.tabulaw.client.mvc.view;

import com.google.gwt.user.client.Command;

/**
 * ViewOpRequest
 * @author jpk
 */
public abstract class ViewOpRequest extends AbstractViewRequest {

	private final ViewKey viewKey;

	/**
	 * Constructor
	 * @param viewKey
	 * @param onCompleteCommand optional command to execute upon completion of
	 *        request
	 */
	public ViewOpRequest(ViewKey viewKey, Command onCompleteCommand) {
		super(onCompleteCommand);
		this.viewKey = viewKey;
	}

	/**
	 * Constructor
	 * @param viewKey
	 */
	public ViewOpRequest(ViewKey viewKey) {
		this(viewKey, null);
	}

	@Override
	public ViewKey getViewKey() {
		return viewKey;
	}

}
