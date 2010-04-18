package com.tll;

import com.tll.util.StringUtil;

/**
 * Base class for all application exceptions. Exception should subclass this
 * class if they are identifying business logic exceptions caused by user error.
 * These exceptions need to be explicity declared in the method signatures.
 * @author jpk
 */
public abstract class ApplicationException extends Exception {

	private static final long serialVersionUID = -3660434051230189619L;

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 */
	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public ApplicationException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param cause
	 */
	public ApplicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * @param tokenMessage Message containing a replacement token.
	 * @param var The replacement value
	 * @see StringUtil#replaceVariables(String, Object)
	 */
	public ApplicationException(String tokenMessage, Object var) {
		super(StringUtil.replaceVariables(tokenMessage, var));
	}

	/**
	 * Constructor
	 * @param tokenMessage Message containing a replacement token.
	 * @param var The replacement value
	 * @param t the Throwable
	 * @see StringUtil#replaceVariables(String, Object[])
	 */
	public ApplicationException(String tokenMessage, Object var, Throwable t) {
		super(StringUtil.replaceVariables(tokenMessage, var), t);
	}

	/**
	 * Constructor
	 * @param tokenMessage Message containing one or more replacement tokens.
	 * @param vars The replacement values
	 * @see StringUtil#replaceVariables(String, Object[])
	 */
	public ApplicationException(String tokenMessage, String[] vars) {
		super(StringUtil.replaceVariables(tokenMessage, vars));
	}

	/**
	 * Constructor
	 * @param tokenMessage Message containing one or more replacement tokens.
	 * @param vars The replacement values
	 * @param t the Throwable
	 * @see StringUtil#replaceVariables(String, Object[])
	 */
	public ApplicationException(String tokenMessage, Object[] vars, Throwable t) {
		super(StringUtil.replaceVariables(tokenMessage, vars), t);
	}
}
