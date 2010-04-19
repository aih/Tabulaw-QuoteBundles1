package com.tabulaw.client.validate;

import com.tabulaw.client.ui.field.FieldErrorHandler;
import com.tabulaw.client.ui.msg.IMsgDisplay;
import com.tabulaw.client.ui.msg.MsgPopupRegistry;

/**
 * ErrorHandlerBuilder - Builds {@link IErrorHandler} instances based on desired
 * modes of validation feedback.
 * @author jpk
 */
public abstract class ErrorHandlerBuilder {

	/**
	 * Assembles an appropriate {@link IErrorHandler} in the form of an {@link ErrorHandlerDelegate}.
	 * @param billboard include global (billboard) error display?
	 *        <em><code>msgDisplay</code> must be specified</em>
	 * @param field include local field error feedback?
	 * @param msgDisplay msg display implmentation. May be <code>null</code>.
	 *        providing field validation feedback
	 * @return A new {@link IErrorHandler} impl instance.
	 */
	public static ErrorHandlerDelegate build(boolean billboard, boolean field, IMsgDisplay msgDisplay) {
		FieldErrorHandler feh = null;
		BillboardValidationFeedback bvf = null;

		if(billboard && msgDisplay != null) {
			bvf = new BillboardValidationFeedback(msgDisplay);
		}
		if(field) {
			feh = new FieldErrorHandler(new MsgPopupRegistry());
		}

		if(feh != null && bvf != null) {
			return new ErrorHandlerDelegate(bvf, feh);
		}

		if(feh != null) return new ErrorHandlerDelegate(feh);
		if(bvf != null) return new ErrorHandlerDelegate(bvf);

		throw new IllegalArgumentException("No error handelr built");
	}

}
