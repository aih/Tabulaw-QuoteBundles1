/**
 * The Logic Lab
 * @author jpk Nov 5, 2007
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.user.client.ui.HasName;
import com.tabulaw.client.ui.IWidgetRef;
import com.tabulaw.client.validate.IHasErrorHandler;
import com.tabulaw.client.validate.IValidator;
import com.tabulaw.client.validate.ValidationException;
import com.tabulaw.schema.IPropertyMetadataProvider;

/**
 * IField - Abstraction for managing the display and editing of data.
 * <p>
 * <em><b>NOTE: </b>fields are considered equal only if their names are the same.</em>
 * @author jpk
 */
public interface IField extends HasName, IWidgetRef, IHasErrorHandler {

	/**
	 * Clears the field's value.
	 */
	void clearValue();

	/**
	 * Resets the field's value to that which was when originally set.
	 */
	void reset();

	/**
	 * @return <code>true</code> if this field is required.
	 */
	boolean isRequired();

	/**
	 * Sets the field's required property.
	 * @param required
	 */
	void setRequired(boolean required);

	/**
	 * @return <code>true</code> if this field is read only.
	 */
	boolean isReadOnly();

	/**
	 * Sets the field's read-only property.
	 * @param readOnly
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * @return <code>true</code> if this field is not disabled.
	 */
	boolean isEnabled();

	/**
	 * Sets the field's enabled property.
	 * @param enabled
	 */
	void setEnabled(boolean enabled);

	/**
	 * Is the field visible?
	 * @return true/false
	 */
	boolean isVisible();

	/**
	 * Sets the field's visibility.
	 * @param visible true/false
	 */
	void setVisible(boolean visible);

	/**
	 * Applies property metadata to this field.
	 * @param provider The property metadata provider.
	 * @param isNewModelData Is the model data new or existing?
	 */
	void applyPropertyMetadata(IPropertyMetadataProvider provider, boolean isNewModelData);

	/**
	 * Adds a validator.
	 * @param validator The validtor to add
	 * @throws IllegalArgumentException When the given validator is
	 *         <code>null</code> or one of the same type already exists.
	 */
	void addValidator(IValidator validator) throws IllegalArgumentException;

	/**
	 * Removes a validator given its type.
	 * @param type The validator type to remove
	 */
	void removeValidator(Class<? extends IValidator> type);

	/**
	 * Validates the field's state.
	 * @throws ValidationException When invalid
	 */
	void validate() throws ValidationException;

	/**
	 * Perform "incremental" validation during editing?
	 * <p>
	 * This usu. translates to validating each field when it loses focus.
	 * @param validate Validate or not?
	 */
	void validateIncrementally(boolean validate);
}
