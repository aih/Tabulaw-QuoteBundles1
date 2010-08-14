package com.tabulaw.mail;

import java.io.IOException;

import javax.mail.MessagingException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.google.inject.Inject;
import com.tabulaw.di.MailModule.MailTemplatePath;
import com.tabulaw.di.MailModule.MailTemplateSuffixHtml;
import com.tabulaw.di.MailModule.MailTemplateSuffixText;
import com.tabulaw.util.VelocityUtil;

/**
 * Composes email mime messages from a templated mail context.
 * @author jpk
 */
public class TemplateComposer extends AbstractComposer<TemplatedMailContext> {

	public static final String SUBJECT_KEY = "subject";

	private final String baseTemplatePath;

	private final String textTemplateSuffix /* = "-text.vm" */;

	private final String htmlTemplateSuffix /* = "-html.vm" */;

	private final VelocityEngine velocityEngine;

	/**
	 * Constructor
	 * @param baseTemplatePath
	 * @param textTemplateSuffix
	 * @param htmlTemplateSuffix
	 * @param velocityEngine
	 */
	@Inject
	public TemplateComposer(@MailTemplatePath String baseTemplatePath, @MailTemplateSuffixText String textTemplateSuffix,
			@MailTemplateSuffixHtml String htmlTemplateSuffix, VelocityEngine velocityEngine) {
		super();
		this.baseTemplatePath = baseTemplatePath;
		this.textTemplateSuffix = textTemplateSuffix;
		this.htmlTemplateSuffix = htmlTemplateSuffix;
		this.velocityEngine = velocityEngine;
	}

	public boolean supports(Class<TemplatedMailContext> contextClass) {
		return TemplatedMailContext.class.isAssignableFrom(contextClass);
	}

	@Override
	public void composeImpl(MimeMessageHelper helper, TemplatedMailContext context) throws MailPreparationException {

		try {
			String templatePath = baseTemplatePath + context.getTemplate();
			templatePath += context.isHtmlTemplate() ? htmlTemplateSuffix : textTemplateSuffix;
			
			String mailText=VelocityUtil.mergeVelocityTemplate(this.velocityEngine, templatePath, context.getParameters());

			// set the subject
			if(context.getParameters().containsKey(SUBJECT_KEY)) {
				helper.setSubject((String) context.getParameters().get(SUBJECT_KEY));
			}

			helper.setText(mailText, context.isHtmlTemplate());
		}
		catch(final IOException ioe) {
			throw new MailPreparationException("Unable to compose templated mail content due to an I/O exception: "
					+ ioe.getMessage(), ioe);
		}
		catch(final VelocityException ve) {
			throw new MailPreparationException("Unable to compose templated mail content due to a Velocity exception: "
					+ ve.getMessage(), ve);
		}
		catch(final MessagingException me) {
			throw new MailPreparationException("unable to compose templated mail content due to a messaging exception: "
					+ me.getMessage(), me);
		}

	}

}
