/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.mvc.view.IView;
import com.tabulaw.client.mvc.view.IViewInitializer;

/**
 * Poc specific view definition.
 * @param <I> view initializer type
 * @author jpk
 */
public interface IPocView<I extends IViewInitializer> extends IView<I>, IModelChangeHandler {

	/**
	 * @return Ordered list of non-navigational widgets to display in the nav col
	 *         panel. <code>null</code> indicates no widgets will be displayed.
	 */
	Widget[] getNavColWidgets();
}
