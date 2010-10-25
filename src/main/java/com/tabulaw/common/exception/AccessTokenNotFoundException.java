package com.tabulaw.common.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
/*-
 * Tells to client side to make OAuth authorization
 */
public class AccessTokenNotFoundException extends RuntimeException implements
		IsSerializable {

}
