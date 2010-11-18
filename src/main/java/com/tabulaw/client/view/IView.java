/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk May 10, 2008
 */
package com.tabulaw.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.ui.view.ViewToolbar;

/**
 * IView - Runtime view definition defining a view's life-cycle.
 * @author jpk
 * @param <I> the view initializer type
 */
public interface IView<I extends IViewInitializer> {

	/**
	 * Styles - (view.css)
	 * @author jpk
	 */
	static final class Styles {

		/**
		 * Style applied to IView implementing widgets.
		 */
		static final String VIEW = "view";
	}
	
	/**
	 * @return The view class of this view.
	 */
	ViewClass getViewClass();

	/**
	 * @return The short view name.
	 */
	String getShortViewName();

	/**
	 * @return The long view name.
	 */
	String getLongViewName();

	/**
	 * @return The Widget used in the UI that represents this view.
	 */
	Widget getViewWidget();
	
	/**
	 * @return The underlying DOM element id of this view (not the view container).
	 * @throws IllegalStateException When the view isn't initialized. 
	 */
	String getElementId() throws IllegalStateException;

	/**
	 * Initializes the view enabling it to be uniquely identifiable at runtime.
	 * @param initializer The view key provider responsible for providing the view
	 *        the ability to provide a {@link ViewKey}.
	 */
	void initialize(I initializer);

	/**
	 * Hook to enable tailoring of the view at runtime with the employed view
	 * toolbar and the widgetthat contains the view entirely. This method is
	 * invoked after {@link #initialize(IViewInitializer)}.
	 * @param viewContainerRef the widget containing the view in its entirety
	 * @param toolbar The view toolbar is additionally provided enabling
	 *        additional {@link Widget}s to be added to it
	 */
	void apply(Widget viewContainerRef, ViewToolbar toolbar);

	/**
	 * Sets or refreshes the contents of the view. This method is invoked after
	 * {@link #apply(Widget, ViewToolbar)}.
	 */
	void refresh();

	/**
	 * Life-cycle provision for view implementations to perform clean-up before
	 * this view looses reference-ability. This could mean, for example, to issue
	 * an RPC cache clean up type command.
	 */
	void onDestroy();
}