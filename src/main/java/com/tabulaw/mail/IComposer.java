package com.tabulaw.mail;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailPreparationException;

/**
 * Composes send-ready mime messages.
 * @author jpk
 * @param <C> The mail context type
 */
public interface IComposer<C extends IMailContext> {

	/**
	 * Does this composer support the given mail context type?
	 * @param contextClass
	 * @return true/false
	 */
	boolean supports(Class<C> contextClass);

	/**
	 * Composes a mime message given a mail context.
	 * @param mimeMessage The mime message that recieves property assignments
	 *        based on the state of the provided mail context.
	 * @param context The mail context.
	 * @return A send-ready mime message.
	 * @throws MailPreparationException upon error composing the message.
	 */
	MimeMessage compose(MimeMessage mimeMessage, C context) throws MailPreparationException;
}
