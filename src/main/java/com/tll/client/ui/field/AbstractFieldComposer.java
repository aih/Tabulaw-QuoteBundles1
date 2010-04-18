/**
 * The Logic Lab
 * @author jpk
 * @since May 11, 2009
 */
package com.tll.client.ui.field;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.tll.client.ui.field.IFieldWidget.Styles;


/**
 * AbstractFieldComposer
 * @author jpk
 */
public abstract class AbstractFieldComposer implements IFieldComposer {

	protected Panel canvas;

	@Override
	public void setCanvas(Panel canvas) {
		this.canvas = canvas;
	}

	@Override
	public void addFieldTitle(String text) {
		final Label l = new Label(text);
		l.setStyleName(Styles.FIELD_TITLE);
		add(null, l);
	}

}
