/**
 * The Logic Lab
 * @author jpk Dec 28, 2008
 */
package com.tabulaw.client.ui.field;

import java.util.Date;
import java.util.Map;

import com.tabulaw.client.convert.EnumToDataMapConverter;
import com.tabulaw.client.ui.GridRenderer;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.client.validate.CreditCardValidator;
import com.tabulaw.client.validate.DateValidator;
import com.tabulaw.client.validate.EmailAddressValidator;
import com.tabulaw.client.validate.EnumValidator;

/**
 * FieldGroupFactory
 * @author jpk
 */
public abstract class FieldFactory {

	/**
	 * Creates a new {@link TextField} instance. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param visibleLength
	 * @return new field
	 */
	public static final TextField ftext(String name, String propName, String labelText, String helpText,
			int visibleLength) {
		return new TextField(name, propName, labelText, helpText, visibleLength);
	}

	/**
	 * Creates a new {@link PasswordField} instance. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param visibleLength
	 * @return new field
	 */
	public static final PasswordField fpassword(String name, String propName, String labelText, String helpText,
			int visibleLength) {
		return new PasswordField(name, propName, labelText, helpText, visibleLength);
	}

	/**
	 * Creates new {@link DateField} instance. <br>
	 * NOTE: the value type is {@link Date}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @return new field
	 */
	public static final DateField fdate(String name, String propName, String labelText, String helpText) {
		return new DateField(name, propName, labelText, helpText, null);
	}

	/**
	 * Creates new {@link DateField} instance. <br>
	 * NOTE: the value type is {@link Date}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param format the date format which defaults to {@link GlobalFormat#DATE}.
	 * @return new field
	 */
	public static final DateField fdate(String name, String propName, String labelText, String helpText,
			GlobalFormat format) {
		final DateField f = new DateField(name, propName, labelText, helpText, format);
		f.addValidator(DateValidator.get(format));
		return f;
	}

	/**
	 * Creates a Radio button field that is designed to be bound to a boolean type
	 * using String-wise constants "true" and "false" to indicate the boolean
	 * value respectively.
	 * @param fieldName the unique field name
	 * @param radioName the radio form input tag name attribute
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @return new field
	 */
	public static final RadioField fradio(String fieldName, String radioName, String propName, String labelText, String helpText) {
		return new RadioField(fieldName, radioName, propName, labelText, helpText);
	}

	/**
	 * Creates a Check box field that is designed to be bound to a boolean type
	 * using String-wise constants "true" and "false" to indicate the boolean
	 * value respectively. <br>
	 * NOTE: the value type is {@link Boolean}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @return new field
	 */
	public static final CheckboxField fcheckbox(String name, String propName, String labelText, String helpText) {
		return new CheckboxField(name, propName, labelText, helpText);
	}

	/**
	 * Creates a new {@link TextAreaField} instance. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param numRows
	 * @param numCols
	 * @return new field
	 */
	public static final TextAreaField ftextarea(String name, String propName, String labelText, String helpText,
			int numRows, int numCols) {
		return new TextAreaField(name, propName, labelText, helpText, numRows, numCols);
	}

	/**
	 * Creates a new {@link SelectField} instance.
	 * @param <V> the value type
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param data
	 * @return new field
	 */
	public static final <V> SelectField<V> fselect(String name, String propName, String labelText, String helpText,
			Map<V, String> data) {
		return new SelectField<V>(name, propName, labelText, helpText, data);
	}

	/**
	 * Creates a new {@link MultiSelectField} instance. <br>
	 * NOTE: the value type is {@link String}.
	 * @param <V> the collection element value type
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param data
	 * @return new field
	 */
	public static final <V> MultiSelectField<V> fmultiselect(String name, String propName, String labelText,
			String helpText, Map<V, String> data) {
		return new MultiSelectField<V>(name, propName, labelText, helpText, data);
	}

	/**
	 * Creates a new {@link SuggestField} instance. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param data
	 * @return new field
	 */
	public static final SuggestField fsuggest(String name, String propName, String labelText, String helpText,
			Map<String, String> data) {
		return new SuggestField(name, propName, labelText, helpText, data);
	}

	/**
	 * Creates a new {@link RadioGroupField} instance.
	 * @param <V> the value type
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param data name/value pairs where the map is keyed by the name
	 * @param renderer renders the radio buttons in a grid
	 * @return new field
	 */
	public static final <V> RadioGroupField<V> fradiogroup(String name, String propName, String labelText,
			String helpText, Map<V, String> data, GridRenderer renderer) {
		return new RadioGroupField<V>(name, propName, labelText, helpText, renderer, data);
	}

	/**
	 * Creates a new {@link TextField} instance with email address validation. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param visibleLength
	 * @return new field
	 */
	public static final TextField femail(String name, String propName, String labelText, String helpText,
			int visibleLength) {
		final TextField f = ftext(name, propName, labelText, helpText, visibleLength);
		f.addValidator(EmailAddressValidator.INSTANCE);
		return f;
	}

	/**
	 * Creates a new {@link TextField} instance with credit card number
	 * validation. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText The on hover tool tip text
	 * @param visibleLength
	 * @return new field
	 */
	public static final TextField fcreditcard(String name, String propName, String labelText, String helpText,
			int visibleLength) {
		final TextField f = ftext(name, propName, labelText, helpText, visibleLength);
		f.addValidator(CreditCardValidator.INSTANCE);
		return f;
	}

	/**
	 * Creates a new {@link SelectField} whose options are enumeration elements. <br>
	 * NOTE: the value type is {@link Enum}.
	 * @param <E> the enum type
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param type
	 * @return select field containing String-wise enum values of the given type
	 */
	@SuppressWarnings("unchecked")
	public static final <E extends Enum<E>> SelectField<E> fenumselect(String name, String propName, String labelText,
			String helpText,
			final Class<E> type) {
		final SelectField<E> f =
			fselect(name, propName, labelText, helpText, EnumToDataMapConverter.INSTANCE.convert(type));
		//f.addValidator(new EnumValidator(type));
		return f;
	}

	/**
	 * Creates a new {@link RadioGroupField} whose options are the elements of the
	 * given enumeration type. <br>
	 * NOTE: the value type is {@link Enum}.
	 * @param <E> the enum type
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param type
	 * @param renderer renders the radio buttons in a grid
	 * @return radio group field containing radio buttons each corresponding the
	 *         an enumeration.
	 */
	@SuppressWarnings("unchecked")
	public static final <E extends Enum<E>> RadioGroupField<E> fenumradio(String name, String propName, String labelText,
			String helpText,
			final Class<E> type, GridRenderer renderer) {
		final RadioGroupField<E> f =
			fradiogroup(name, propName, labelText, helpText, EnumToDataMapConverter.INSTANCE
					.convert(type), renderer);
		f.addValidator(new EnumValidator(type));
		return f;
	}

	/**
	 * Creates a new {@link SuggestField} whose suggestions are defined by the
	 * given ref map. <br>
	 * NOTE: the value type is {@link String}.
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param refmap
	 * @return select field containing the app currencies
	 */
	public static final SuggestField frefdata(String name, String propName, String labelText, String helpText,
			Map<String, String> refmap) {
		return fsuggest(name, propName, labelText, helpText, refmap);
	}

	/**
	 * Creates an entity name text field. <br>
	 * NOTE: the value type is {@link String}.
	 * @param fnamePrefix The optional field name prefix to maintain field name
	 *        uniqueness
	 * @return The created entity name field
	 */
	public static final TextField entityNameField(String fnamePrefix) {
		return ftext((fnamePrefix == null ? "" : fnamePrefix) + "name", "name", "Name", "Name",
				30);
	}

	/**
	 * Creates entity date created and date modified read only fields returning
	 * them in an array where the first element is the date created field. <br>
	 * NOTE: the value type is {@link Date}.
	 * @param fnamePrefix The optional field name prefix to maintain field name
	 *        uniqueness
	 * @return DateField array
	 */
	public static final DateField[] entityTimestampFields(String fnamePrefix) {
		final DateField dateCreated =
			fdate((fnamePrefix == null ? "" : fnamePrefix) + "dateCreated", "dateCreated", "Created", "Date Created");
		final DateField dateModified =
			fdate((fnamePrefix == null ? "" : fnamePrefix) + "dateModified", "dateModified", "Modified", "Date Modified");
		dateCreated.setReadOnly(true);
		dateModified.setReadOnly(true);
		return new DateField[] {
			dateCreated, dateModified };
	}
}
