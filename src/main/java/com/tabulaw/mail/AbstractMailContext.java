package com.tabulaw.mail;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

/**
 * The abstract mail context. Holds routing, encoding and attachment data.
 * Concrete sub-classes must provide subject and message content.
 * @author jpk
 */
abstract class AbstractMailContext implements IMailContext {

	private final MailRouting routing;

	private final String encoding;

	private final List<Attachment> attachments = new ArrayList<Attachment>();

	boolean sent;

	/**
	 * Constructor
	 * @param routing
	 * @param encoding
	 */
	protected AbstractMailContext(MailRouting routing, String encoding) {
		super();
		this.routing = routing;
		this.encoding = encoding;
	}

	public MailRouting getRouting() {
		return routing;
	}

	public String getEncoding() {
		return encoding;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void addAttachment(String attachmentName, DataSource dataSource) {
		attachments.add(new Attachment(attachmentName, dataSource));
	}

	public void addAttachment(String attachmentName, String data, String contentType) {
		final DataSource dataSource = new StringDataSource(attachmentName, data, contentType);
		addAttachment(attachmentName, dataSource);
	}

	public void markSent() {
		this.sent = true;
	}

	public boolean wasSent() {
		return sent;
	}

	@Override
	public final String toString() {
		return getName();
	}
}