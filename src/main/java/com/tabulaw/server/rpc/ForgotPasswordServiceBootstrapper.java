/**
 * The Logic Lab
 * @author jpk
 * Feb 4, 2009
 */
package com.tabulaw.server.rpc;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Injector;
import com.tabulaw.server.IBootstrapHandler;
import com.tabulaw.service.IForgotPasswordHandler;
import com.tll.mail.MailManager;

/**
 * ForgotPasswordServiceBootstrapper
 * @author jpk
 */
public class ForgotPasswordServiceBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(ForgotPasswordServiceBootstrapper.class);

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.debug("Creating the ForgotPasswordServiceContext...");
		final MailManager mailManager = injector.getInstance(MailManager.class);
		final IExceptionHandler exceptionHandler = injector.getInstance(IExceptionHandler.class);
		final IForgotPasswordHandler handler = injector.getInstance(IForgotPasswordHandler.class);
		servletContext.setAttribute(ForgotPasswordServiceContext.KEY, new ForgotPasswordServiceContext(
				handler, mailManager, exceptionHandler));
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		// no-op
	}

}
