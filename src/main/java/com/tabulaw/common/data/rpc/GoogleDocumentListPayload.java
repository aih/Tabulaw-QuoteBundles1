package com.tabulaw.common.data.rpc;

import java.util.ArrayList;
import java.util.List;

import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.common.data.Status;

public class GoogleDocumentListPayload extends Payload {

	private List<GoogleDocument> documents = new ArrayList<GoogleDocument>();

	public GoogleDocumentListPayload() {
		super();
	}

	public GoogleDocumentListPayload(Status status) {
		super(status);
	}

	public void setDocuments(List<GoogleDocument> documents) {
		this.documents = documents;
	}

	public List<GoogleDocument> getDocuments() {
		return documents;
	}

}
