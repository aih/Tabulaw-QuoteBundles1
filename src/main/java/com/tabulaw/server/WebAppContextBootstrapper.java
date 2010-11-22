/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jopaki
 * @since Aug 15, 2010
 */
package com.tabulaw.server;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Injector;
import com.tabulaw.mail.EmailDispatcher;

/**
 * Bootstraps stuff needed at the servlet/rpc processing level.
 * @author jopaki
 */
public class WebAppContextBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(WebAppContextBootstrapper.class);

	private Thread edt;

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.info("Bootstrapping web app context..");

		// store the sole web app context
		final WebAppContext context = injector.getInstance(WebAppContext.class);
		servletContext.setAttribute(WebAppContext.KEY, context);

		// start up the email dispatcher in its own thread to keep the main web
		// request thread from becoming latent
		EmailDispatcher emailDispatcher = injector.getInstance(EmailDispatcher.class);
		edt = new Thread(emailDispatcher, "Email Dispatcher");
		edt.start();

		log.info("Web app context bootstrapped");
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		log.info("Shutting down web app context..");

		// kill the email dispatcher thread
		if(edt != null) {
			edt.interrupt();
			edt = null;
		}

		servletContext.removeAttribute(WebAppContext.KEY);

		log.info("Web app context shutdown");
	}
}
