/**
 * The Logic Lab
 * @author jpk Feb 11, 2009
 */
package com.tll.tabulaw.server.rpc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.tll.mail.MailManager;
import com.tll.mail.NameEmail;
import com.tll.server.rpc.IExceptionHandler;
import com.tll.tabulaw.di.EmailExceptionHandlerModule.OnErrorEmail;

/**
 * EmailExceptionHandler - Emails exception notification emails.
 * @author jpk
 */
public class EmailExceptionHandler implements IExceptionHandler {

	private static final Log log = LogFactory.getLog(EmailExceptionHandler.class);

	private final MailManager mailManager;

	private final NameEmail onErrorEmail;

	/**
	 * Constructor
	 * @param mailManager
	 * @param onErrorEmail
	 */
	@Inject
	public EmailExceptionHandler(MailManager mailManager, @OnErrorEmail NameEmail onErrorEmail) {
		super();
		this.mailManager = mailManager;
		this.onErrorEmail = onErrorEmail;
	}

	/**
	 * Used for doling out exception notification emails.
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

	public void handleException(final Throwable t) {
		if(mailManager == null) {
			log.warn("Can't email exception because no email manager specified.");
		}
		else {
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
			mailManager.sendEmail(mailManager.buildTextTemplateContext(mailManager.buildAppSenderMailRouting(onErrorEmail),
					"exception-notification", data));
		}
	}
}
