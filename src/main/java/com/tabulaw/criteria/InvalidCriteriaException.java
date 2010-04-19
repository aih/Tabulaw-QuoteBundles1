package com.tabulaw.criteria;


/**
 * Throw when {@link Criteria} derived instances are found to be invalid due to
 * either invalid contained values or when the criteria is null or empty
 * (contains no {@link Criterion} objects.
 * @author jpk
 */
public class InvalidCriteriaException extends CriteriaException {

	private static final long serialVersionUID = 5508051537602537427L;

	/**
	 * Constructor
	 */
	public InvalidCriteriaException() {
		super("Invalid or empty criteria");
	}

	/**
	 * Constructor
	 * @param message
	 */
	public InvalidCriteriaException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 */
	public InvalidCriteriaException(String message, Throwable cause) {
		super(message, cause);
	}
}
