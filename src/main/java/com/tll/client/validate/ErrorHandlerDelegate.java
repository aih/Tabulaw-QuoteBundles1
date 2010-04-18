/**
 * The Logic Lab
 * @author jpk
 * Mar 4, 2009
 */
package com.tll.client.validate;

import java.util.Collection;
import java.util.HashSet;

import com.tll.client.ui.IWidgetRef;
import com.tll.client.ui.msg.IHasMsgDisplay;
import com.tll.client.ui.msg.IMsgDisplay;

/**
 * ErrorHandlerDelegate - Acts as a single error handler that actually delegates
 * calls to child handlers.
 * @author jpk
 */
public final class ErrorHandlerDelegate implements IErrorHandler, IHasMsgDisplay {

	private final HashSet<IErrorHandler> handlers = new HashSet<IErrorHandler>();

	private IHasMsgDisplay msgDisplayAwareErrorHandler;

	/**
	 * Constructor
	 * @param handlers The handlers to which validation feedback is delegated
	 */
	public ErrorHandlerDelegate(IErrorHandler... handlers) {
		for(final IErrorHandler handler : handlers) {
			if(handler != null) {
				if(handler instanceof IHasMsgDisplay) {
					if(msgDisplayAwareErrorHandler != null)
						throw new IllegalArgumentException("Multiple msg display aware error handlers not allowed");
					msgDisplayAwareErrorHandler = (IHasMsgDisplay) handler;
				}
				this.handlers.add(handler);
			}
		}
	}

	@Override
	public ErrorDisplay getDisplayType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void handleError(Error error, int displayFlags) {
		for(final IErrorHandler handler : handlers) {
			handler.handleError(error, displayFlags);
		}
	}

	@Override
	public void handleErrors(Collection<Error> errors, int displayFlags) {
		for(final IErrorHandler handler : handlers) {
			handler.handleErrors(errors, displayFlags);
		}
	}

	@Override
	public void resolveError(IWidgetRef source, ErrorClassifier classifier, int displayFlags) {
		for(final IErrorHandler handler : handlers) {
			handler.resolveError(source, classifier, displayFlags);
		}
	}

	@Override
	public void clear(ErrorClassifier classifier) {
		for(final IErrorHandler handler : handlers) {
			handler.clear(classifier);
		}
	}

	@Override
	public void clear() {
		for(final IErrorHandler handler : handlers) {
			handler.clear();
		}
	}

	@Override
	public IMsgDisplay getMsgDisplay() {
		return msgDisplayAwareErrorHandler == null ? null : msgDisplayAwareErrorHandler.getMsgDisplay();
	}

	@Override
	public void setMsgDisplay(IMsgDisplay msgDisplay) {
		if(msgDisplayAwareErrorHandler != null) {
			msgDisplayAwareErrorHandler.setMsgDisplay(msgDisplay);
		}
	}

}
