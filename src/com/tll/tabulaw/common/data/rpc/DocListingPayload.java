/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import java.util.List;

import com.tll.common.data.Payload;
import com.tll.common.data.Status;
import com.tll.common.model.Model;

/**
 * @author jpk
 */
public class DocListingPayload extends Payload {

	private List<Model> cachedDocs;

	public DocListingPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 * @param cachedDocs
	 */
	public DocListingPayload(Status status, List<Model> cachedDocs) {
		super(status);
		this.cachedDocs = cachedDocs;
	}

	public List<Model> getCachedDocs() {
		return cachedDocs;
	}
}
