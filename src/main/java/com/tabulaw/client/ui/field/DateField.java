/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Nov 7, 2007
 */
package com.tabulaw.client.ui.field;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.tabulaw.client.convert.IConverter;
import com.tabulaw.client.ui.IHasFormat;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.client.validate.DateValidator;
import com.tabulaw.client.validate.ValidationException;

/**
 * DateField
 * @author jpk
 */
public class DateField extends AbstractField<Date> implements IHasFormat {

	/**
	 * Impl
	 * @author jpk
	 */
	static final class Impl extends DateBox implements IEditable<Date> {

		/**
		 * Constructor
		 * @param name
		 */
		public Impl(String name) {
			super();
			getTextBox().setName(name);
			getTextBox().addStyleName(Styles.TBOX);
			getTextBox().setVisibleLength(8);
		}

		@Override
		public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
			return getTextBox().addKeyDownHandler(handler);
		}

		@Override
		public HandlerRegistration addBlurHandler(BlurHandler handler) {
			return getTextBox().addBlurHandler(handler);
		}

		@Override
		public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
			return getTextBox().addMouseOverHandler(handler);
		}

		@Override
		public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
			return getTextBox().addMouseOutHandler(handler);
		}

		@Override
		public HandlerRegistration addFocusHandler(FocusHandler handler) {
			return getTextBox().addFocusHandler(handler);
		}
	}

	/**
	 * ToDateConverter
	 * @author jpk
	 */
	final class ToDateConverter implements IConverter<Date, Object> {

		@SuppressWarnings("synthetic-access")
		@Override
		public Date convert(Object in) throws IllegalArgumentException {
			try {
				return (Date) DateValidator.get(dateFormat).validate(in);
			}
			catch(final ValidationException e) {
				throw new IllegalArgumentException(e);
			}
		}

	}

	/**
	 * The date display format.
	 */
	private GlobalFormat dateFormat;

	/**
	 * The target date box.
	 */
	private final Impl dbox;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param format
	 */
	DateField(String name, String propName, String labelText, String helpText, GlobalFormat format) {
		super(name, propName, labelText, helpText);
		dbox = new Impl(name);
		dbox.addValueChangeHandler(this);
		dbox.addFocusHandler(this);
		dbox.addBlurHandler(this);
		setFormat(format);
	}

	public GlobalFormat getFormat() {
		return dateFormat;
	}

	public void setFormat(GlobalFormat format) {
		if(format != null && !format.isDateFormat()) throw new IllegalArgumentException();
		this.dateFormat = format == null ? GlobalFormat.DATE : format;
		dbox.setFormat(new DefaultFormat(Fmt.getDateTimeFormat(dateFormat)));
		setConverter(new ToDateConverter());
	}

	@Override
	public IEditable<Date> getEditable() {
		return dbox;
	}

	@Override
	public String doGetText() {
		return dbox.getTextBox().getText();
	}

	public void setText(String text) {
		dbox.getTextBox().setText(text);
	}

	@Override
	public void setEnabled(boolean enabled) {
		dbox.setEnabled(enabled);
		super.setEnabled(enabled);
	}
}
