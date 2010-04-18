/**
 * The Logic Lab
 * @author jpk Jan 13, 2008
 */
package com.tll.client.mvc.view;

/**
 * AbstractViewRequest - common base class for all {@link IViewRequest} types.
 * @author jpk
 */
public abstract class AbstractViewRequest extends AbstractViewKeyProvider implements IViewRequest {

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
	public final String toString() {
		String s = "";
		final ViewKey viewKey = getViewKey();
		if(viewKey != null) {
			s += viewKey.toString();
		}
		return s;
	}
}
