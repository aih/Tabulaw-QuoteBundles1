/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 15, 2010
 */
package com.tabulaw.server;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Injector;

/**
 * Bootstraps stuff needed at the servlet/rpc processing level.
 * @author jopaki
 */
public class WebAppContextBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(WebAppContextBootstrapper.class);

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.info("Bootstrapping web app context..");

		// store the sole persist context
		final WebAppContext context = injector.getInstance(WebAppContext.class);
		servletContext.setAttribute(WebAppContext.KEY, context);
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		// TODO Auto-generated method stub

	}
}
