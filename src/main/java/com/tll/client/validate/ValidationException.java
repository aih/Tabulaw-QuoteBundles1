/**
 * The Logic Lab
 * @author jpk Apr 13, 2008
 */
package com.tll.client.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * ValidationException - The one and only validation exception type.
 * @author jpk
 */
@SuppressWarnings("serial")
public final class ValidationException extends Exception {

	/**
	 * The associated errors.
	 */
	private final ArrayList<Error> errors = new ArrayList<Error>();

	/**
	 * Constructor
	 * @param error
	 */
	public ValidationException(Error error) {
		if(error == null) throw new IllegalArgumentException("Null error");
		this.errors.add(error);
	}

	/**
	 * Constructor
	 * @param errors a collection of errors
	 */
	public ValidationException(Collection<Error> errors) {
		if(errors == null || errors.size() < 1) throw new IllegalArgumentException("No errors");
		this.errors.addAll(errors);
	}

	/**
	 * Constructor - Creates a simple non-targeted error.
	 * @param error the error message
	 */
	public ValidationException(final String error) {
		this(new Error(ErrorClassifier.CLIENT, null, error));
	}

	/**
	 * @return the associated errors.
	 */
	public List<Error> getErrors() {
		return errors;
	}
}
