/**
 * The Logic Lab
 * @author jpk
 * @since Mar 23, 2009
 */
package com.tabulaw.client.view;

import com.google.gwt.user.client.Command;

/**
 * IViewRequest - Definition for a view request that is handled by the mvc
 * controllers.
 * @author jpk
 */
public interface IViewRequest extends IViewKeyProvider {

	/**
	 * @return <code>true</code> if history should be updated with a view token,
	 *         <code>false</code> if history is NOT to be updated.
	 *         <p>
	 *         Default returns <code>true</code>. There are few cases when this
	 *         needs to be <code>false</code>.
	 */
	boolean addHistory();

	/**
	 * Optional command to execution upon completion of the view request.
	 * @return command
	 */
	Command onCompleteCommand();
}