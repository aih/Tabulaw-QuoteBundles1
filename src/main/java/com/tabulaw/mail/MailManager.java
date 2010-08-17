package com.tabulaw.mail;

import java.util.Map;

import org.springframework.mail.MailSendException;

import com.google.inject.Inject;
import com.tabulaw.di.MailModule.DefaultMailRouting;

/**
 * Builds {@link IMailContext} implementations. Client's should obtain a
 * {@link IMailContext} only through methods in this class.
 * @author jpk
 */
public final class MailManager {

	/**
	 * The default routing
	 */
	private final MailRouting defaultRouting;

	/**
	 * The mail sender
	 */
	private final MailSender mailSender;

	/**
	 * Constructor
	 * @param defaultRouting
	 * @param mailSender
	 */
	@Inject
	public MailManager(@DefaultMailRouting MailRouting defaultRouting, MailSender mailSender) {
		super();
		this.defaultRouting = defaultRouting;
		this.mailSender = mailSender;
	}

	private String getEncoding() {
		return System.getProperty("encoding");
	}

	/**
	 * @return the "application's" email address - the sending email address for
	 *         automatically generated emails.
	 */
	public NameEmail getApplicationSendingEmail() {
		return defaultRouting.getSender();
	}

	/**
	 * Builds a mail routing using the app email and given recipient.
	 * @param recipient
	 * @return {@link MailRouting}
	 */
	public MailRouting buildAppSenderMailRouting(NameEmail recipient) {
		return new MailRouting(getApplicationSendingEmail(), recipient);
	}

	/**
	 * Builds a mail routing using the app email and given recipient email
	 * address.
	 * @param recipientEmailAddress
	 * @return {@link MailRouting}
	 */
	public MailRouting buildAppSenderMailRouting(String recipientEmailAddress) {
		return buildAppSenderMailRouting(new NameEmail(recipientEmailAddress));
	}

	/**
	 * Builds a clone of the default mail routing adding the given recipient.
	 * @param recipient
	 * @return {@link MailRouting}
	 */
	public MailRouting buildDefaultMailRouting(NameEmail recipient) {
		final MailRouting routing = defaultRouting.clone();
		routing.addRecipient(recipient);
		return routing;
	}

	/**
	 * Builds an empty mail routing instance adding the given recipient email
	 * address.
	 * @param recipientEmailAddress
	 * @return {@link MailRouting}
	 */
	public MailRouting buildDefaultMailRouting(String recipientEmailAddress) {
		return buildDefaultMailRouting(new NameEmail(recipientEmailAddress));
	}

	/**
	 * Builds a mail routing instance adding the given sender and recipient.
	 * @param sender
	 * @param recipient
	 * @return {@link MailRouting}
	 */
	public MailRouting buildMailRouting(NameEmail sender, NameEmail recipient) {
		final MailRouting routing = defaultRouting.clone();
		routing.setSender(sender);
		routing.addRecipient(recipient);
		return routing;
	}

	/**
	 * Builds a mail routing instance adding the given sender and recipient email
	 * addresses.
	 * @param senderEmailAddress
	 * @param recipientEmailAddress
	 * @return {@link MailRouting}
	 */
	public MailRouting buildMailRouting(String senderEmailAddress, String recipientEmailAddress) {
		return buildMailRouting(new NameEmail(senderEmailAddress), new NameEmail(recipientEmailAddress));
	}

	/**
	 * Builds a simple mail context given a subject and body and mail routing.
	 * @param mailRouting
	 * @param subject
	 * @param body
	 * @return {@link IMailContext}
	 */
	public IMailContext buildSimpleContext(MailRouting mailRouting, String subject, String body) {
		return new SimpleMailContext(mailRouting, getEncoding(), subject, body);
	}

	/**
	 * Builds a simple mail context given a subject and body with default mail
	 * routing dictated by the application context.
	 * @param subject
	 * @param body
	 * @return {@link IMailContext}
	 * @see #buildSimpleContext(MailRouting, String, String)
	 */
	public IMailContext buildSimpleContextWithDefaultRouting(String subject, String body) {
		return buildSimpleContext(defaultRouting, subject, body);
	}

	/**
	 * Builds an HTML-base templated mail context given a template name, and
	 * template parameters.
	 * @param mailRouting
	 * @param templateName
	 * @param parameters
	 * @return {@link IMailContext}
	 */
	public IMailContext buildTextTemplateContext(MailRouting mailRouting, String templateName,
			Map<String, Object> parameters) {
		return new TemplatedMailContext(mailRouting, getEncoding(), templateName, false, parameters);
	}

	/**
	 * Builds an HTML-base templated mail context given a template name, and
	 * template parameters with default mail routing dictated by the application
	 * context.
	 * @param templateName
	 * @param parameters
	 * @return {@link IMailContext}
	 * @see #buildTextTemplateContext(MailRouting, String, Map)
	 */
	public IMailContext buildTextTemplateContextWithDefaultRouting(String templateName, Map<String, Object> parameters) {
		return buildTextTemplateContext(defaultRouting, templateName, parameters);
	}

	/**
	 * Builds an HTML-base templated mail context given a template name, and
	 * template parameters.
	 * @param mailRouting
	 * @param templateName
	 * @param parameters
	 * @return {@link IMailContext}
	 */
	public IMailContext buildHtmlTemplateContext(MailRouting mailRouting, String templateName,
			Map<String, Object> parameters) {
		return new TemplatedMailContext(mailRouting, getEncoding(), templateName, true, parameters);
	}

	/**
	 * Builds an HTML-base templated mail context given a template name, and
	 * template parameters with default mail routing dictated by the application
	 * context.
	 * @param templateName
	 * @param parameters
	 * @return {@link IMailContext}
	 * @see #buildHtmlTemplateContext(MailRouting, String, Map)
	 */
	public IMailContext buildHtmlTemplateContextWithDefaultRouting(String templateName, Map<String, Object> parameters) {
		return buildHtmlTemplateContext(defaultRouting, templateName, parameters);
	}

	/**
	 * This is it!: Sends an email message
	 * @param context the mail context
	 * @throws MailSendException
	 */
	public void sendEmail(IMailContext context) throws MailSendException {
		if(context.wasSent()) {
			throw new IllegalStateException("Mail message: '" + context + "' was already sent.");
		}
		mailSender.send(context);
		context.markSent();
	}
}