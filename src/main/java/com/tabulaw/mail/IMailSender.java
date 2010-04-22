package com.tabulaw.mail;

import org.springframework.mail.MailSendException;

/**
 * Provision to send email.
 * @author jpk
 */
public interface IMailSender {

	/**
	 * @param <C> The mail context type
	 * @param context The send mail context holding necessary parameters to send
	 *        the email.
	 * @throws MailSendException
	 */
	public <C extends IMailContext> void send(C context) throws MailSendException;
}
