/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Nov 5, 2007
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.user.client.ui.RadioButton;
import com.tabulaw.client.convert.ToBooleanConverter;
import com.tabulaw.client.convert.ToStringConverter;

/**
 * @author jpk
 */
public final class RadioField extends AbstractField<Boolean> {

	/**
	 * Impl
	 * @author jpk
	 */
	private static final class Impl extends RadioButton implements IEditable<Boolean> {

		/**
		 * Constructor
		 * @param name
		 * @param label
		 */
		public Impl(String name, String label) {
			super(name, label);
			setStyleName(Styles.CBRB);
		}

		@Override
		public void setValue(Boolean value) {
			super.setValue(value == null ? Boolean.FALSE : value);
		}

		@Override
		public void setValue(Boolean value, boolean fireEvents) {
			super.setValue(value == null ? Boolean.FALSE : value, fireEvents);
		}
	}

	private final Impl rb;

	/**
	 * Constructor
	 * @param fieldName the unique field name
	 * @param radioName the radio form input tag name attribute
	 * @param propName
	 * @param labelText
	 * @param helpText
	 */
	RadioField(String fieldName, String radioName, String propName, String labelText, String helpText) {
		super(fieldName, propName, labelText, helpText);
		setConverter(ToBooleanConverter.DEFAULT);
		rb = new Impl(radioName, labelText);
		rb.addFocusHandler(this);
		rb.addBlurHandler(this);
		rb.addValueChangeHandler(this);
	}

	@Override
	public IEditable<Boolean> getEditable() {
		return rb;
	}

	public boolean isChecked() {
		return rb.getValue() == Boolean.TRUE;
	}

	public void setChecked(boolean checked) {
		rb.setValue(checked ? Boolean.TRUE : Boolean.FALSE);
	}

	@Override
	public void setEnabled(boolean enabled) {
		rb.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override 
	public String doGetText() {
		return ToStringConverter.INSTANCE.convert(getValue());
	}

	public void setText(String text) {
		rb.setText(text);
	}

}
