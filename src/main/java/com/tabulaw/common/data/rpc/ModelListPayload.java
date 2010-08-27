/**
 * The Logic Lab
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tabulaw.common.data.rpc;

import java.util.List;

import com.tabulaw.common.data.Status;
import com.tabulaw.model.DocRef;

/**
 * @author jpk
 */
public class DocListingPayload extends Payload {

	private List<DocRef> docList;

	public DocListingPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public DocListingPayload(Status status) {
		super(status);
	}

	public void setDocList(List<DocRef> docList) {
		this.docList = docList;
	}

	public List<DocRef> getDocList() {
		return docList;
	}
}
