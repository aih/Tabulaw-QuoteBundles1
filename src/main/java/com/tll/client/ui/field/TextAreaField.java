/**
 * The Logic Lab
 * @author jpk Nov 5, 2007
 */
package com.tll.client.ui.field;

import com.google.gwt.user.client.ui.TextArea;
import com.tll.client.convert.ToStringConverter;
import com.tll.client.validate.StringLengthValidator;

/**
 * TextAreaField
 * @author jpk
 */
public class TextAreaField extends AbstractField<String> implements IHasMaxLength {

	/**
	 * Impl
	 * @author jpk
	 */
	static final class Impl extends TextArea implements IEditable<String> {

		/**
		 * Constructor
		 */
		public Impl() {
			super();
			addStyleName(Styles.TBOX);
		}

	}

	int maxLen = -1;
	private final Impl ta;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param numRows if -1, value won't be set
	 * @param numCols if -1, value won't be set
	 */
	TextAreaField(String name, String propName, String labelText, String helpText, int numRows, int numCols) {
		super(name, propName, labelText, helpText);
		ta = new Impl();
		ta.addValueChangeHandler(this);
		ta.addFocusHandler(this);
		ta.addBlurHandler(this);
		setConverter(ToStringConverter.INSTANCE);
		setNumRows(numRows);
		setNumCols(numCols);
	}

	public int getNumRows() {
		return ta.getVisibleLines();
	}

	public void setNumRows(int numRows) {
		ta.setVisibleLines(numRows);
	}

	public int getNumCols() {
		return ta.getCharacterWidth();
	}

	public void setNumCols(int numCols) {
		ta.setCharacterWidth(numCols);
	}

	public int getMaxLen() {
		return maxLen;
	}

	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
		if(maxLen == -1) {
			removeValidator(StringLengthValidator.class);
		}
		else {
			addValidator(new StringLengthValidator(-1, maxLen));
		}
	}

	@Override
	public String doGetText() {
		return ta.getText();
	}

	public void setText(String text) {
		ta.setText(text);
	}

	@Override
	public void setEnabled(boolean enabled) {
		ta.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public IEditable<String> getEditable() {
		return ta;
	}
}
