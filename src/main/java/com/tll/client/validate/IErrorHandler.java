package com.tll.client.validate;

import java.util.Collection;

import com.tll.client.ui.IWidgetRef;

/**
 * IErrorHandler - Definition for handling {@link Error}s.
 * @author jpk
 */
public interface IErrorHandler {

	/**
	 * @return The display type
	 */
	ErrorDisplay getDisplayType();

	/**
	 * Handles an error.
	 * @param error the error
	 * @param displayFlags bit-wise {@link ErrorDisplay} flags that determines
	 *        whether this implementation should handle the given error
	 */
	void handleError(Error error, int displayFlags);

	/**
	 * Handles a collection of errors.
	 * @param errors the errors
	 * @param displayFlags bit-wise {@link ErrorDisplay} flags that determines
	 *        whether this implementation should handle the given errors
	 */
	void handleErrors(Collection<Error> errors, int displayFlags);

	/**
	 * Resolves (clears) validation based on filtering arguments.
	 * @param target Optional target widget reference
	 * @param classifier Optional error classifier
	 * @param displayFlags bit-wise {@link ErrorDisplay} flags that determines
	 *        whether this implementation should resolve the error
	 */
	void resolveError(IWidgetRef target, ErrorClassifier classifier, int displayFlags);

	/**
	 * Removes all errors found to be associated with the given classifier.
	 * @param classifier
	 */
	void clear(ErrorClassifier classifier);

	/**
	 * Removes <em>all</em> errors and clears internal state.
	 */
	void clear();
}
