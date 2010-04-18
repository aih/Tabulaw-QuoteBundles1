/**
 * The Logic Lab
 * @author jpk
 * @since Sep 14, 2009
 */
package com.tll.client.validate;

import java.util.Collection;

import com.tll.client.ui.IWidgetRef;


/**
 * AbstractErrorHandler
 * @author jpk
 */
public abstract class AbstractErrorHandler implements IErrorHandler {

	@Override
	public final void handleError(Error error, int displayFlags) {
		if(error != null) {
			final int ef = getDisplayType().flag();
			if((displayFlags & ef) == ef) {
				doHandleError(error);
			}
		}
	}

	@Override
	public final void handleErrors(Collection<Error> errors, int displayFlags) {
		if(errors != null) {
			for(final Error error : errors) {
				handleError(error, displayFlags);
			}
		}
	}

	@Override
	public final void resolveError(IWidgetRef source, ErrorClassifier classifier, int displayFlags) {
		final int ef = getDisplayType().flag();
		if((displayFlags & ef) == ef) {
			doResolveError(source, classifier);
		}
	}

	/**
	 * Does the actual error handling.
	 * @param error
	 */
	protected abstract void doHandleError(Error error);

	/**
	 * Does the actual error resolving.
	 * @param source
	 * @param classifier
	 */
	protected abstract void doResolveError(IWidgetRef source, ErrorClassifier classifier);
}
