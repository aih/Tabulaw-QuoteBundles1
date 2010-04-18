/**
 * The Logic Lab
 * @author jpk
 * May 24, 2008
 */
package com.tll.client.ui.field;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * IFieldComposer - Composes fields onto a given "canvas" ( {@link Panel} )
 * dictating their layout.
 * @author jpk
 */
public interface IFieldComposer {

	/**
	 * Sets the {@link Panel} onto which the fields are drawn. When called,
	 * implementations should clear out any previously set draw state clearing any
	 * internally managed {@link Panel}s.
	 * @param canvas The {@link Panel} onto which the fields are drawn
	 */
	void setCanvas(Panel canvas);

	/**
	 * Adds a field to the canvas. The field label is extracted from the given
	 * field and if non-<code>null</code>, is added as well.
	 * @param field The field to add
	 */
	void addField(IFieldWidget<?> field);

	/**
	 * Generic add routine.
	 * @param fldLbl the field label
	 * @param w the associated widget
	 */
	void add(FieldLabel fldLbl, Widget w);

	/**
	 * Adds a label widget having <code>Styles.FIELD_TITLE</code> applied to it.
	 * @param text the label text
	 */
	void addFieldTitle(String text);

	/**
	 * Adds a non-field Widget.
	 * @param w The non-field Widget to add
	 */
	void addWidget(Widget w);

	/**
	 * Adds a field label and Widget. If the label text is <code>null</code>, no
	 * label is added. If the Widget is an {@link IFieldWidget},
	 * {@link #addField(IFieldWidget)} should be called instead.
	 * @param label The label text
	 * @param w The non-IField and non-FieldPanel Widget to add
	 */
	void addWidget(String label, Widget w);
}
