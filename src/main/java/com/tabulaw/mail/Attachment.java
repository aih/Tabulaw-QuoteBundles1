package com.tabulaw.mail;

import javax.activation.DataSource;

/**
 * Represents an email attachement.
 * @author jpk
 */
final class Attachment {

	String name;
	DataSource dataSource;

	public Attachment() {
		super();
	}

	public Attachment(String attachmentName, DataSource dataSource) {
		super();
		this.name = attachmentName;
		this.dataSource = dataSource;
	}

	public String getName() {
		return name;
	}

	public void setName(String attachmentName) {
		this.name = attachmentName;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}