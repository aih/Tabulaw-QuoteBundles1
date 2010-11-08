package com.tabulaw.common.data.rpc;

import java.util.ArrayList;
import java.util.List;

import com.tabulaw.common.data.Status;
import com.tabulaw.model.DocRef;

public class DocRefListPayload extends Payload {

	private List<DocRef> docRefs = new ArrayList<DocRef>();

	public DocRefListPayload() {
		super();
	}

	public DocRefListPayload(Status status) {
		super(status);
	}

	public void setDocRefs(List<DocRef> docRefs) {
		this.docRefs = docRefs;
	}

	public List<DocRef> getDocRefs() {
		return docRefs;
	}
}
