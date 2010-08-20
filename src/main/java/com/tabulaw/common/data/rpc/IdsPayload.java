/**
 * The Logic Lab
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

	private Map<String, Integer[]> ids;

	public IdsPayload() {
		super();
	}

	public IdsPayload(Status status) {
		super(status);
	}

	public Map<String, Integer[]> getIds() {
		return ids;
	}

	public void setIds(Map<String, Integer[]> ids) {
		this.ids = ids;
	}
}
