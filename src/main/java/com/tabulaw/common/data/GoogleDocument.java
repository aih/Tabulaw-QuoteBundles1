package com.tabulaw.common.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GoogleDocument implements IsSerializable {

	private String resourceId;
	private String title;

	public GoogleDocument() {
	}

	public GoogleDocument(String resourceId, String title) {
		setResourceId(resourceId);
		setTitle(title);
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "[title=" + getTitle() + " resourceId=" + getResourceId() + "]";
	}
}
