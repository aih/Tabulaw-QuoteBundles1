/**
 * The Logic Lab
 * @author jpk Feb 11, 2009
 */
package com.tabulaw.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.tabulaw.di.ExceptionHandlerModule.OnErrorEmail;
import com.tabulaw.mail.EmailDispatcher;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.MailManager;
import com.tabulaw.mail.MailRouting;
import com.tabulaw.mail.NameEmail;

/**
 * ExceptionHandler - Emails exception notification emails.
 * @author jpk
 */
public class ExceptionHandler implements IExceptionHandler {

	private static final Log log = LogFactory.getLog(ExceptionHandler.class);

	private final EmailDispatcher emailDispatcher;

	private final NameEmail onErrorEmail;

	/**
	 * Constructor
	 * @param emailDispatcher
	 * @param onErrorEmail
	 */
	@Inject
	public ExceptionHandler(EmailDispatcher emailDispatcher, @OnErrorEmail NameEmail onErrorEmail) {
		super();
		this.emailDispatcher = emailDispatcher;
		this.onErrorEmail = onErrorEmail;
	}

	/**
	 * Used for doling out exception notification emails.
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

	public void handleException(final Throwable t) {

		// log the exception
		log.error(t.getMessage(), t);

		// email the exception
		if(emailDispatcher != null) {
			final Map<String, Object> data = new HashMap<String, Object>();
			data.put("header", "Exception Notification (" + t.getClass().getSimpleName() + ")");
			synchronized (this) {
				data.put("datetime", sdf.format(new Date()));
			}
			String emsg = t.getMessage();
			if(emsg == null) {
				emsg = t.getClass().getSimpleName();
			}
			data.put("error", emsg);
			final StackTraceElement ste =
					(t.getStackTrace() == null || t.getStackTrace().length < 1) ? null : t.getStackTrace()[0];
			data.put("trace", ste == null ? "[NO STACK TRACE]" : ste.toString());
			MailManager mailManager = emailDispatcher.getMailManager();
			MailRouting mailRouting = mailManager.buildAppSenderMailRouting(onErrorEmail);
			IMailContext mailContext = mailManager.buildTextTemplateContext(mailRouting, "exception-notification", data);
			try {
				emailDispatcher.queueEmail(mailContext);
			}
			catch(InterruptedException e) {
				// TODO anything ?
			}
		}
	}
}
