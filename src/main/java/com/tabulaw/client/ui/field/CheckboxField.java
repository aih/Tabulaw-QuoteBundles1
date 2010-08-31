/**
 * The Logic Lab
 * @author jpk Nov 5, 2007
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.user.client.ui.CheckBox;
import com.tabulaw.client.convert.ToBooleanConverter;
import com.tabulaw.client.convert.ToStringConverter;

/**
 * CheckboxField
 * @author jpk
 */
public final class CheckboxField extends AbstractField<Boolean> {

	/**
	 * Impl
	 * @author jpk
	 */
	private static final class Impl extends CheckBox implements IEditable<Boolean> {

		/**
		 * Constructor
		 * @param name
		 * @param label
		 */
		public Impl(String name, String label) {
			super(label);
			setName(name);
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

	private final Impl cb;
	
	private final String checkedReadOnlyText, uncheckedReadOnlyText;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param checkedReadOnlyText
	 * @param uncheckedReadOnlyText
	 */
	CheckboxField(String name, String propName, String labelText, String helpText, String checkedReadOnlyText, String uncheckedReadOnlyText) {
		super(name, propName, null, helpText);
		setConverter(ToBooleanConverter.DEFAULT);
		cb = new Impl(name, labelText);
		cb.addFocusHandler(this);
		cb.addBlurHandler(this);
		cb.addValueChangeHandler(this);
		this.checkedReadOnlyText = checkedReadOnlyText;
		this.uncheckedReadOnlyText = uncheckedReadOnlyText;
	}

	@Override
	public IEditable<Boolean> getEditable() {
		return cb;
	}

	public boolean isChecked() {
		return cb.getValue() == Boolean.TRUE;
	}

	public void setChecked(boolean checked) {
		cb.setValue(checked ? Boolean.TRUE : Boolean.FALSE);
	}

	@Override
	public void setEnabled(boolean enabled) {
		cb.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override 
	public String doGetText() {
		return ToStringConverter.INSTANCE.convert(getValue());
	}

	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLabelText(String labelText) {
		// TODO Auto-generated method stub
		super.setLabelText(labelText);
	}

	@Override
	protected void setReadOnlyContent() {
		getReadOnlyWidget().setText(isChecked()? checkedReadOnlyText : uncheckedReadOnlyText);
	}
}
