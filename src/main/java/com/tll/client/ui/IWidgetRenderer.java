/**
 * The Logic Lab
 * @author jpk
 * Feb 26, 2009
 */
package com.tll.client.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * IWidgetRenderer - Renders a collection of widgets.
 * @author jpk
 */
public interface IWidgetRenderer {

	/**
	 * Renders a collection of widgets returning a panel that contains them.
	 * @param widgetCollection widget collection to render
	 * @return newly created panel containing the widgets.
	 */
	Panel render(Collection<? extends Widget> widgetCollection);
}
