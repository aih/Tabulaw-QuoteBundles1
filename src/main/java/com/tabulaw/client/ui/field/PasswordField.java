/**
 * The Logic Lab
 * @author jpk
 * Nov 5, 2007
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.tabulaw.client.convert.ToStringConverter;
import com.tabulaw.client.validate.StringLengthValidator;

/**
 * PasswordField
 * @author jpk
 */
public final class PasswordField extends AbstractField<String> implements IHasMaxLength {

	/**
	 * Impl
	 * @author jpk
	 */
	static final class Impl extends PasswordTextBox implements IEditable<String> {

		/**
		 * Constructor
		 */
		public Impl() {
			super();
			addStyleName(Styles.TBOX);
		}

	}

	private final Impl tb;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param lblText
	 * @param helpText
	 * @param visibleLength
	 */
	PasswordField(String name, String propName, String lblText, String helpText, int visibleLength) {
		super(name, propName, lblText, helpText);
		tb = new Impl();
		setVisibleLen(visibleLength);
		tb.addValueChangeHandler(this);
		tb.addFocusHandler(this);
		tb.addBlurHandler(this);
		setConverter(ToStringConverter.INSTANCE);
		addHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER) {
					setFocus(false);
					setFocus(true);
				}
			}
		}, KeyPressEvent.getType());
	}

	public int getVisibleLen() {
		return tb.getVisibleLength();
	}

	public void setVisibleLen(int visibleLength) {
		tb.setVisibleLength(visibleLength < 0 ? 256 : visibleLength);
	}

	public int getMaxLen() {
		return tb.getMaxLength();
	}

	public void setMaxLen(int maxLen) {
		tb.setMaxLength(maxLen < 0 ? 256 : maxLen);
		if(maxLen == -1) {
			removeValidator(StringLengthValidator.class);
		}
		else {
			addValidator(new StringLengthValidator(-1, maxLen));
		}
	}

	@Override
	public String doGetText() {
		return tb.getText();
	}

	public void setText(String text) {
		tb.setText(text);
	}

	@Override
	public void setEnabled(boolean enabled) {
		tb.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public IEditable<String> getEditable() {
		return tb;
	}
}
