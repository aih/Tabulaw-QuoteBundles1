package com.tabulaw.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * Simple string-based implementation of javax.activation.DataSource interface.
 * @author jpk
 */
public class StringDataSource implements DataSource {

	private String name;
	private String data;
	private String contentType;

	/**
	 * 
	 */
	public StringDataSource() {
		super();
	}

	public StringDataSource(String name, String data, String contentType) {
		super();
		this.name = name;
		this.data = data;
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getInputStream() {
		// Reader reader = new StringReader(data);
		return new ByteArrayInputStream(data.getBytes());
	}

	public String getName() {
		return name;
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("No output streams available for this type of data source.");
	}

}
