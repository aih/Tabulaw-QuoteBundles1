/**
 * The Logic Lab
 * @author jpk
 * Jan 4, 2009
 */
package com.tll.client.ui.field;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * IFieldRenderer - Renders fields onto a {@link Panel}.
 * @param <W> The widget type on which to render
 * @author jpk
 */
public interface IFieldRenderer<W extends Widget> {

	/**
	 * Renders fields onto the given {@link Widget}.
	 * @param widget The widget onto which the fields are rendered
	 * @param fg The field group containing the fields to render
	 */
	void render(W widget, FieldGroup fg);
}
