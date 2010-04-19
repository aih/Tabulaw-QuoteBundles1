/**
 * The Logic Lab
 * @author jpk
 * Apr 5, 2008
 */
package com.tabulaw.client.mvc.view;


/**
 * ViewOpRequest
 * @author jpk
 */
public abstract class ViewOpRequest extends AbstractViewRequest {

	private final ViewKey viewKey;

	/**
	 * Constructor
	 * @param viewKey
	 */
	public ViewOpRequest(ViewKey viewKey) {
		super();
		this.viewKey = viewKey;
	}

	@Override
	public ViewKey getViewKey() {
		return viewKey;
	}

}
