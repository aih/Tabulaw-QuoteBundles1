/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 9, 2009
 */
package com.tabulaw.client.ui.msg;

/**
 * IHasMsgPopupRegistry - A way to generically get/set a
 * {@link MsgPopupRegistry}.
 * @author jpk
 */
public interface IHasMsgPopupRegistry {

	/**
	 * @return the mregistry.
	 */
	MsgPopupRegistry getMsgPopupRegistry();

	/**
	 * Sets the mregistry
	 * @param mregistry
	 */
	void setMsgPopupRegistry(MsgPopupRegistry mregistry);
}
