/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.server;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Injector;

/**
 * @author jpk
 */
public class PersistContextBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(PersistContextBootstrapper.class);

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.info("Bootstrapping persist context..");

		// store the sole persist context
		final PersistContext context = injector.getInstance(PersistContext.class);
		servletContext.setAttribute(PersistContext.KEY, context);
		
		// initialize the user service
		context.getUserService().init();
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		// no-op
	}
}
