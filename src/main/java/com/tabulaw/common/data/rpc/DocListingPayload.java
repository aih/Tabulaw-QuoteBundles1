/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tabulaw.common.data.rpc;

import java.util.List;

import com.tabulaw.common.model.DocRef;
import com.tll.common.data.Payload;
import com.tll.common.data.Status;

/**
 * @author jpk
 */
public class DocListingPayload extends Payload {

	private List<DocRef> cachedDocs;

	public DocListingPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 * @param cachedDocs
	 */
	public DocListingPayload(Status status, List<DocRef> cachedDocs) {
		super(status);
		this.cachedDocs = cachedDocs;
	}

	public List<DocRef> getCachedDocs() {
		return cachedDocs;
	}
}
