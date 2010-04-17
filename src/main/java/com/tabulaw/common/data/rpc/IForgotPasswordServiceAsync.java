/**
 * The Logic Lab
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tll.common.data.Payload;

/**
 * IForgotPasswordServiceAsync
 * @author jpk
 */
public interface IForgotPasswordServiceAsync {

	void requestPassword(String emailAddress, AsyncCallback<Payload> callback);
}
