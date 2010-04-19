/**
 * The Logic Lab
 * @author jpk Mar 2, 2009
 */
package com.tabulaw.client.validate;

import com.tabulaw.client.ui.IWidgetRef;
import com.tabulaw.client.ui.msg.IHasMsgDisplay;
import com.tabulaw.client.ui.msg.IMsgDisplay;

/**
 * BillboardValidationFeedback
 * @author jpk
 */
public final class BillboardValidationFeedback extends AbstractErrorHandler implements IHasMsgDisplay {

	private IMsgDisplay msgDisplayWidget;

	/**
	 * Constructor
	 * @param msgDisplayWidget the message display widget to which messages are
	 *        appended by way of this error handler.
	 */
	public BillboardValidationFeedback(IMsgDisplay msgDisplayWidget) {
		setMsgDisplay(msgDisplayWidget);
	}

	@Override
	public ErrorDisplay getDisplayType() {
		return ErrorDisplay.GLOBAL;
	}

	@Override
	public IMsgDisplay getMsgDisplay() {
		return msgDisplayWidget;
	}

	@Override
	public void setMsgDisplay(IMsgDisplay msgDisplay) {
		if(msgDisplay == null) throw new IllegalArgumentException("Null msg display");
		this.msgDisplayWidget = msgDisplay;
	}

	@Override
	protected void doHandleError(Error error) {
		assert error != null;
		if(error.getTarget() == null) {
			msgDisplayWidget.add(error.getMessages(), error.getClassifier() == null ? null :  Integer.valueOf(error.getClassifier().hashCode()));
		}
		else {
			msgDisplayWidget.add(error.getTarget(), error.getMessages(), error.getClassifier() == null ? null : Integer.valueOf(error.getClassifier().hashCode()));
		}
	}

	@Override
	protected void doResolveError(IWidgetRef source, ErrorClassifier classifier) {
		if(source == null) {
			msgDisplayWidget.removeUnsourced(classifier == null ? null : Integer.valueOf(classifier.hashCode()));
		}
		else {
			msgDisplayWidget.remove(source, classifier == null ? null : Integer.valueOf(classifier.hashCode()));
		}
	}

	@Override
	public void clear(ErrorClassifier classifier) {
		msgDisplayWidget.remove(classifier.hashCode());
	}

	@Override
	public void clear() {
		msgDisplayWidget.clear();
	}
}
