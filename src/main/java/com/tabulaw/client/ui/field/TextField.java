/**
 * The Logic Lab
 * @author jpk Nov 5, 2007
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.tabulaw.client.convert.ToStringConverter;
import com.tabulaw.client.ui.IHasFormat;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.client.validate.StringLengthValidator;

/**
 * TextField
 * @author jpk
 */
public class TextField extends AbstractField<String> implements IHasMaxLength, IHasFormat {

	/**
	 * Impl
	 * @author jpk
	 */
	static final class Impl extends TextBox implements IEditable<String> {

		/**
		 * Constructor
		 * @param name 
		 */
		public Impl(String name) {
			super();
			addStyleName(Styles.TBOX);
			setName(name);
		}

	}

	private final Impl tb;

	/**
	 * Optional format direcive.
	 */
	private GlobalFormat format;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param visibleLength
	 */
	public TextField(String name, String propName, String labelText, String helpText, int visibleLength) {
		super(name, propName, labelText, helpText);
		tb = new Impl(name);
		setVisibleLen(visibleLength);
		tb.addValueChangeHandler(this);
		tb.addFocusHandler(this);
		tb.addBlurHandler(this);
		setConverter(ToStringConverter.INSTANCE);
		addHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER) {
					setFocus(false);
					setFocus(true);
				}
			}
		}, KeyPressEvent.getType());

	}

	@Override
	public GlobalFormat getFormat() {
		return format;
	}

	@Override
	public void setFormat(GlobalFormat format) {
		this.format = format;
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
	public void setEnabled(boolean enabled) {
		tb.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public String doGetText() {
		return tb.getText();
	}

	public void setText(String text) {
		tb.setText(text);
	}

	@Override
	public IEditable<String> getEditable() {
		return tb;
	}
}
