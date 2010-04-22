package com.tabulaw.mail;

import javax.mail.MessagingException;

import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Composes mime messages from a simple mail context.
 * @author jpk
 */
public class SimpleComposer extends AbstractComposer<SimpleMailContext> {

	public boolean supports(Class<SimpleMailContext> contextClass) {
		return SimpleMailContext.class.isAssignableFrom(contextClass);
	}

	@Override
	protected void composeImpl(MimeMessageHelper helper, SimpleMailContext context) throws MailPreparationException {
		try {
			helper.setSubject(context.getSubject());
			helper.setText(context.getContent());
		}
		catch(final MessagingException me) {
			throw new MailPreparationException("Unable to compose simple mail message: " + me.getMessage(), me);
		}
	}

}
