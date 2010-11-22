/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Aug 29, 2007
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.IMarshalable;
import com.tabulaw.common.data.Status;

/**
 * Payload - Common container with which to send data to client.
 * @author jpk
 */
public class Payload implements IMarshalable {

	protected Status status;

	/**
	 * Constructor
	 */
	public Payload() {
		super();
		this.status = new Status();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public Payload(Status status) {
		super();
		this.status = status;
	}

	public final Status getStatus() {
		if(status == null) {
			status = new Status();
		}
		return status;
	}

	public final boolean hasErrors() {
		return status == null ? false : status.hasErrors();
	}
}
