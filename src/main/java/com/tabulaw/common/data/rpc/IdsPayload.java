/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 23, 2010
 */
package com.tabulaw.common.data.rpc;

import java.util.Map;

import com.tabulaw.common.data.Status;

/**
 * Payload for transporting assignable ids categorized by entity type.
 * @author jpk
 */
public class IdsPayload extends Payload {

	private Map<String, Long[]> ids;

	public IdsPayload() {
		super();
	}

	public IdsPayload(Status status) {
		super(status);
	}

	public Map<String, Long[]> getIds() {
		return ids;
	}

	public void setIds(Map<String, Long[]> ids) {
		this.ids = ids;
	}
}
