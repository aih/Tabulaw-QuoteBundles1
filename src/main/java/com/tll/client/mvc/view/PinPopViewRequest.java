/**
 * The Logic Lab
 * @author jpk Jan 26, 2008
 */
package com.tll.client.mvc.view;


/**
 * PinPopViewRequest - Request to either pin or pop a view.
 * @author jpk
 */
public final class PinPopViewRequest extends ViewOpRequest {

	private final boolean pop;

	/**
	 * Constructor
	 * @param viewKey May be <code>null</code>
	 * @param pop <code>true</code> when requesting to pop the current view,
	 *        <code>false<code> when requesting to pin a popped view.
	 */
	public PinPopViewRequest(ViewKey viewKey, boolean pop) {
		super(viewKey);
		this.pop = pop;
	}

	public boolean isPop() {
		return pop;
	}

	@Override
	public boolean addHistory() {
		return !pop;
	}
}
