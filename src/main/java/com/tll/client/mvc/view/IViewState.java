/**
 * The Logic Lab
 * @author jpk
 * Mar 19, 2008
 */
package com.tll.client.mvc.view;

/**
 * IViewState
 * @author jpk
 */
public interface IViewState {

	/**
	 * @return <code>true</code> if minimized.
	 */
	boolean isMinimized();

	/**
	 * @return <code>true</code> if popped.
	 */
	boolean isPopped();
}