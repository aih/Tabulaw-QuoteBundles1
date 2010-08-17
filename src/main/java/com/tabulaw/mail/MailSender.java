package com.tabulaw.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * The mail sender.
 * @author jpk
 */
public class MailSender {

	private static final Log log = LogFactory.getLog(MailSender.class);

	/**
	 * List of {@link JavaMailSender} objects. The order dictates the try priority
	 * of the mail sender.
	 */
	private final List<JavaMailSender> javaMailSenders;

	/**
	 * The number of times to re-try sending an email.
	 */
	private final int numberOfSendRetries;

	/**
	 * The delay in mili-seconds to wait until re-trying to send an email.
	 */
	private final int sendRetryDelayMilis;

	/**
	 * List of supported email composers.
	 */
	private List<IComposer<? extends IMailContext>> composers = new ArrayList<IComposer<? extends IMailContext>>();

	/**
	 * Constructor
	 * @param javaMailSenders
	 * @param numberOfSendRetries
	 * @param sendRetryDelayMilis
	 * @param composers
	 */
	public MailSender(List<JavaMailSender> javaMailSenders, int numberOfSendRetries, int sendRetryDelayMilis,
			List<IComposer<? extends IMailContext>> composers) {
		super();
		this.javaMailSenders = javaMailSenders;
		this.numberOfSendRetries = numberOfSendRetries;
		this.sendRetryDelayMilis = sendRetryDelayMilis;
		this.composers = composers;
	}

	@SuppressWarnings("unchecked")
	private <C extends IMailContext> IComposer<C> getComposerFromContext(C context) {
		for(final IComposer c : composers) {
			if(c.supports(context.getClass())) {
				return c;
			}
		}
		throw new IllegalArgumentException("Unsupported mail context type: " + context.getClass().getSimpleName());
	}

	public <C extends IMailContext> void send(C context) throws MailSendException {
		final IComposer<C> composer = getComposerFromContext(context);
		int retries;

		for(final JavaMailSender sender : javaMailSenders) {
			retries = 0;// reset
			final MimeMessage mimeMessage = sender.createMimeMessage();
			composer.compose(mimeMessage, context);

			while(retries++ < numberOfSendRetries) {
				// send the email
				try {
					if(log.isDebugEnabled()) log.debug("Sending email: " + context + "...");
					sender.send(mimeMessage);
					log.info(context + " Message sent.");
					return;
				}
				catch(final MailAuthenticationException mae) {
					log.error("Failed email delivery attempt: " + mae.getMessage());
					break;// no retries for this exception
				}
				catch(final MailSendException mse) {
					log.error("Failed email delivery attempt: " + mse.getMessage());
					try {
						Thread.sleep(sendRetryDelayMilis);
					}
					catch(final InterruptedException ie) {
						throw new IllegalStateException("Thread [MailSender] was interrupted!", ie);
					}
				}
			}
		}

		log.error("Email delivery for '" + context + "' FAILED");
		throw new MailSendException("Failed email delivery attempt: " + context);
	}

}
