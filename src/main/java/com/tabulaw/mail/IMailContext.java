/**
 * 
 */
package com.tabulaw.mail;

import java.util.List;

import javax.activation.DataSource;

/**
 * Email context definition.
 * @author jpk
 */
public interface IMailContext {

	/**
	 * @return An instance unique name. Usually, the email subject.
	 */
	String getName();

	/**
	 * @return {@link MailRouting}
	 */
	MailRouting getRouting();

	/**
	 * @return {@link String}
	 */
	String getEncoding();

	/**
	 * @return List<Attachment>
	 */
	List<Attachment> getAttachments();

	/**
	 * @param attachmentName
	 * @param dataSource
	 */
	void addAttachment(String attachmentName, DataSource dataSource);

	/**
	 * @param attachmentName
	 * @param data
	 * @param contentType
	 */
	void addAttachment(String attachmentName, String data, String contentType);

	/**
	 * Denote this context as sent.
	 */
	void markSent();

	/**
	 * Was the context sent?
	 * @return true/false
	 */
	boolean wasSent();
}