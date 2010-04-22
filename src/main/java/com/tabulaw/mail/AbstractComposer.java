package com.tabulaw.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * The abstract mime message composer.
 * @author jpk
 * @param <C> the mail context type
 */
abstract class AbstractComposer<C extends IMailContext> implements IComposer<C> {

	/**
	 * Implementation specific composition.
	 * @param helper Spring's mime message helper.
	 * @param context The mail context.
	 * @throws MailPreparationException
	 */
	protected abstract void composeImpl(MimeMessageHelper helper, C context) throws MailPreparationException;

	public final MimeMessage compose(MimeMessage mimeMessage, C context) throws MailPreparationException {

		// create a mime message helper
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage, true, context.getEncoding());
		}
		catch(final MessagingException me) {
			throw new MailPreparationException("Unable to create the mime message helper", me);
		}

		// apply the routing
		final MailRouting mailRouting = context.getRouting();
		if(mailRouting == null) {
			throw new MailPreparationException("No email routing specified.");
		}
		try {
			// sender
			final NameEmail sender = mailRouting.getSender();
			if(sender != null) {
				helper.setFrom(sender.getEmailAddress(), sender.getName());
			}

			List<NameEmail> list;

			// recipients
			list = mailRouting.getRecipients();
			if(list.isEmpty()) {
				throw new MailPreparationException("No email recipients specified");
			}
			for(final NameEmail email : list) {
				helper.addTo(email.getEmailAddress(), email.getName());
			}

			// ccs
			list = mailRouting.getCcList();
			if(list != null && list.size() > 0) {
				for(final NameEmail email : list) {
					helper.addCc(email.getEmailAddress(), email.getName());
				}
			}

			// bccs
			list = mailRouting.getBccList();
			if(list != null && list.size() > 0) {
				for(final NameEmail email : list) {
					helper.addBcc(email.getEmailAddress(), email.getName());
				}
			}

		}
		catch(final UnsupportedEncodingException uee) {
			throw new MailPreparationException("Unsupported mime message encoding: " + uee.getMessage(), uee);
		}
		catch(final MessagingException me) {
			throw new MailPreparationException("Trouble performing the initial mime message compose:" + me.getMessage(), me);
		}

		// do implemenation specific composing
		composeImpl(helper, context);

		// add attachments
		try {
			final List<Attachment> attachments = context.getAttachments();
			if(!attachments.isEmpty()) {
				for(final Attachment attachment : attachments) {
					helper.addAttachment(attachment.getName(), attachment.getDataSource());
				}
			}

		}
		catch(final MessagingException me) {
			throw new MailPreparationException("Unable to add email attachments:" + me.getMessage(), me);
		}

		return helper.getMimeMessage();
	}
}
