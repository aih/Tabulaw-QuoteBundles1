package com.tabulaw.mail;

/**
 * Simple mail context having immutable email subject and email content string
 * properties.
 * @author jpk
 */
class SimpleMailContext extends AbstractMailContext {

	private final String subject, content;

	/**
	 * Constructor
	 * @param routing
	 * @param encoding
	 * @param subject
	 * @param content
	 */
	public SimpleMailContext(MailRouting routing, String encoding, String subject, String content) {
		super(routing, encoding);
		this.subject = subject;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getSubject() {
		return subject;
	}

	public String getName() {
		return getSubject();
	}
}