package com.tabulaw.client.validate;

import java.util.ArrayList;

/**
 * CompositeValidator
 * @author jkirton
 */
public class CompositeValidator implements IValidator {

	private final ArrayList<IValidator> validators = new ArrayList<IValidator>();

	/**
	 * Adds a validator.
	 * @param validator The validator to add
	 * @return this
	 * @throws IllegalArgumentException When the validator already exists or one
	 *         of the same type (class) exists.
	 */
	public CompositeValidator add(IValidator validator) throws IllegalArgumentException {
		toAddCheck(validator);
		validators.add(validator);
		return this;
	}
	
	/**
	 * Removes a validator by reference only.
	 * @param validator The validator to remove
	 * @return <code>true</code> if the validator was successfully remvoed.
	 */
	public boolean remove(IValidator validator) {
		return validators.remove(validator);
	}

	/**
	 * Removes a validator by type. We can do this since we enfore a type
	 * constraint when adding validators.
	 * @param type the validator type
	 * @return true/false
	 */
	public boolean remove(Class<? extends IValidator> type) {
		IValidator rmv = null;
		for(final IValidator v : validators) {
			if(v.getClass() == type) {
				rmv = v;
				break;
			}
		}
		if(rmv != null) {
			validators.remove(rmv);
			return true;
		}
		return false;
	}

	/**
	 * Ensures that only one validator of the given type exists.
	 * @param validator
	 * @throws IllegalArgumentException
	 */
	private void toAddCheck(IValidator validator) throws IllegalArgumentException {
		if(validator == null) throw new IllegalArgumentException("A validator must be specified.");
		for(final IValidator v : validators) {
			if(v == validator || v.getClass() == validator.getClass()) {
				throw new IllegalArgumentException("This validator or one of the same type already exists.");
			}
		}
	}

	public Object validate(Object value) throws ValidationException {
		Object retValue = value;
		for(final IValidator v : validators) {
			retValue = v.validate(retValue);
		}
		return retValue;
	}
}
