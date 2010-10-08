package com.tabulaw.common.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GoogleDocument implements IsSerializable {

	private String resourceId;
	private String title;
	private String date;
	private String author;
	private String type;

	public GoogleDocument() {
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

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	@Override
	public String toString() {
		return "[title=" + getTitle() + " resourceId=" + getResourceId()
				+ " date=" + getDate() + " author=" + getAuthor() + "]";
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
