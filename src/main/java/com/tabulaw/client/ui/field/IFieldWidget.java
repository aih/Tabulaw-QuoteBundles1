/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Feb 27, 2009
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.IPropertyNameProvider;
import com.tabulaw.client.convert.IHasConverter;
import com.tabulaw.client.ui.IHasHelpText;
import com.tabulaw.client.validate.IValidator;
import com.tabulaw.schema.PropertyMetadata;

/**
 * IFieldWidget - A physical non-group field capable of display in the ui.
 * @param <V> value type
 * @author jpk
 */
public interface IFieldWidget<V> extends IField, IPropertyNameProvider, HasValue<V>, 
HasText, IHasHelpText, IValidator, ValueChangeHandler<V>, 
Focusable, BlurHandler, FocusHandler, HasKeyDownHandlers,
IHasConverter<Object, V>, HasFocusHandlers, HasBlurHandlers {

	/**
	 * Styles - (field.css)
	 * @author jpk
	 */
	public static final class Styles {

		/**
		 * Style indicating a UI artifact is a field.
		 */
		public static final String FIELD = "fld";

		/**
		 * Style indicating a form input element of: input[type="text"],
		 * input[type="password"], select, textarea.
		 */
		public static final String TBOX = "tbox";

		/**
		 * Specific field style applied to checkboxes and radio buttons.
		 */
		public static final String CBRB = "cbrb";

		/**
		 * Style indicating a field's requiredness.
		 */
		public static final String REQUIRED = "rqd";

		/**
		 * Style indicating the field's value is dirty (changed).
		 */
		public static final String DIRTY = "dirty";

		/**
		 * Style indicating the field's value is invalid.
		 */
		public static final String INVALID = "error";

		/**
		 * Style for disabling a field.
		 */
		public static final String DISABLED = "disabled";

		/**
		 * Style for indicating read-only.
		 */
		public static final String READ_ONLY = "ro";

		/**
		 * Style for indicating a field has focus.
		 */
		public static final String ACTIVE = "active";

		/**
		 * Style indicating a UI artifact is a field title.
		 */
		public static final String FIELD_TITLE = "fldtitle";

	} // Styles

	/**
	 * This method behave's just like getValue() does but provides the value as a
	 * string.
	 * @return The field's value as a string.
	 * @see IFieldWidget#getValue()
	 */
	String getFieldValue();

	/**
	 * Sets the property name for this field.
	 * @param propName The property name
	 */
	void setPropertyName(String propName);

	/**
	 * @return the editable interface.
	 */
	IEditable<?> getEditable();

	/**
	 * @return The associated {@link FieldLabel} which may be <code>null</code>.
	 */
	FieldLabel getFieldLabel();

	/**
	 * @return The label text.
	 */
	String getLabelText();

	/**
	 * Sets the label text.
	 * @param text the label text
	 */
	void setLabelText(String text);

	/**
	 * Sets the ancestor Widget that contains this field.
	 * @param fieldContainer The desired ancestor {@link Widget}
	 */
	void setFieldContainer(Widget fieldContainer);

	/**
	 * Sets the ancestor Widget for this field's label {@link Widget}.
	 * @param fieldLabelContainer The desired ancestor {@link Widget}
	 */
	void setFieldLabelContainer(Widget fieldLabelContainer);

	/**
	 * Directly sets property metadata on this field widget.
	 * @param metadata
	 */
	void setPropertyMetadata(PropertyMetadata metadata);
}
