/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.common.data.Status;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;

/**
 * Transport for a single doc.
 * @author jpk
 */
public class DocPayload extends Payload {

	private DocRef docRef;

	private DocContent docContent;

	/**
	 * Constructor
	 */
	public DocPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public DocPayload(Status status) {
		super(status);
	}

	public DocRef getDocRef() {
		return docRef;
	}

	public void setDocRef(DocRef docRef) {
		this.docRef = docRef;
	}

	public DocContent getDocContent() {
		return docContent;
	}

	public void setDocContent(DocContent docContent) {
		this.docContent = docContent;
	}
}
