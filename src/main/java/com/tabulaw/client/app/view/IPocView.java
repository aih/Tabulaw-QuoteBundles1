/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.app.view;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.view.IView;
import com.tabulaw.client.view.IViewInitializer;

/**
 * Poc specific view definition.
 * @param <I> view initializer type
 * @author jpk
 */
public interface IPocView<I extends IViewInitializer> extends IView<I> {

	/**
	 * @return Ordered list of non-navigational widgets to display in the nav col
	 *         panel. <code>null</code> indicates no widgets will be displayed.
	 */
	Widget[] getNavColWidgets();
}