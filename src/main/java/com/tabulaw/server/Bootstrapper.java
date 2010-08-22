/**
 * The Logic Lab
 */
package com.tabulaw.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.tabulaw.config.Config;
import com.tabulaw.config.ConfigRef;
import com.tabulaw.config.IConfigAware;

/**
 * Bootstrapper - Responsible for starting up and shutting down the app.
 * @author jpk
 */
public class Bootstrapper implements ServletContextListener {

	private static final Log log = LogFactory.getLog(Bootstrapper.class);

	/**
	 * The servlet context param name identifying the dependency injection modules
	 * to load.
	 */
	static final String DEPENDENCY_MODULE_CLASS_NAMES = "di.modules";

	/**
	 * The servlet context param name identifying the dependency injection
	 * handlers.
	 */
	static final String DEPENDENCY_HANDLER_CLASS_NAMES = "di.handlers";

	/**
	 * Creates a dependency injector from the {@link ServletContext}'s init
	 * params.
	 * @param context The servlet context.
	 * @param stage
	 * @param config
	 * @return new dependency injector instance
	 */
	private static Injector createInjector(ServletContext context, Stage stage, Config config) {
		final String[] moduleClassNames = StringUtils.split(context.getInitParameter(DEPENDENCY_MODULE_CLASS_NAMES));
		if(moduleClassNames == null || moduleClassNames.length < 1) {
			throw new Error("No bootstrap module class names declared.");
		}

		final List<Module> modules = new ArrayList<Module>(moduleClassNames.length + 1);

		// velocity module
		modules.add(new Module() {

			@Override
			public void configure(Binder binder) {
				binder.bind(VelocityEngine.class).toProvider(new Provider<VelocityEngine>() {

					public VelocityEngine get() {
						try {
							Properties props = new Properties();
							final ClassLoader cl = Thread.currentThread().getContextClassLoader();
							InputStream is = cl.getResourceAsStream("/velocity.properties");
							props.load(is);
							VelocityEngine ve = new VelocityEngine(props);
							return ve;
						}
						catch(final Exception e) {
							throw new IllegalStateException("Unable to instantiate the velocity engine: " + e.getMessage(), e);
						}
					}

				}).in(Scopes.SINGLETON);
			}
		});

		for(final String mcn : moduleClassNames) {
			try {
				final Module m = (Module) Class.forName(mcn, true, Bootstrapper.class.getClassLoader()).newInstance();
				if(m instanceof IConfigAware) {
					((IConfigAware) m).setConfig(config);
				}
				modules.add(m);
			}
			catch(final ClassNotFoundException e) {
				throw new Error("Module class: " + mcn + " not found.");
			}
			catch(final InstantiationException e) {
				throw new Error("Unable to instantiate module class: " + mcn);
			}
			catch(final IllegalAccessException e) {
				throw new Error("Unable to access module class: " + mcn);
			}
		}

		if(log.isDebugEnabled()) {
			log.debug("Creating " + stage.toString() + " bootstrap di injector...");
		}
		return Guice.createInjector(stage, modules);
	}

	private List<IBootstrapHandler> handlers;

	/**
	 * Creates a dependency injector from the {@link ServletContext}'s init
	 * params.
	 * @param context The servlet context.
	 */
	private void loadDependencyHandlers(ServletContext context) {
		final String[] handlerClassNames = StringUtils.split(context.getInitParameter(DEPENDENCY_HANDLER_CLASS_NAMES));
		if(handlerClassNames == null || handlerClassNames.length < 1) {
			throw new Error("No bootstrap handlers declared.");
		}
		handlers = new ArrayList<IBootstrapHandler>(handlerClassNames.length);
		for(final String hcn : handlerClassNames) {
			try {
				handlers.add((IBootstrapHandler) Class.forName(hcn).newInstance());
			}
			catch(final ClassNotFoundException e) {
				throw new Error("Handler class: " + hcn + " not found.");
			}
			catch(final InstantiationException e) {
				throw new Error("Unable to instantiate handler class: " + hcn);
			}
			catch(final IllegalAccessException e) {
				throw new Error("Unable to access handler class: " + hcn);
			}
		}
	}

	/**
	 * Hook to do init stuff *before* the bootstrapper handlers and the dependency
	 * injector is created.
	 * @param event
	 */
	protected void beforeInjection(ServletContextEvent event) {
		// default no-op
	}

	public void contextInitialized(ServletContextEvent event) {
		final ServletContext servletContext = event.getServletContext();

		// load *all* found config properties
		// NOTE: this is presumed to be the first contact point with the config
		// instance!
		final Config config;
		try {
			config = Config.load(new ConfigRef(true));
		}
		catch(final IllegalArgumentException e) {
			throw new Error("Unable to load config: " + e.getMessage(), e);
		}

		// put the config in the servlet context
		servletContext.setAttribute(Config.KEY, config);

		// pre dependency injection initializing
		log.debug("Performing pre-injection initialization..");
		beforeInjection(event);

		// load the dependency handler definitions
		log.debug("Creating dependency handlers..");
		loadDependencyHandlers(servletContext);

		final String stage = config.getString("stage", "prod");
		log.info("Bootstrapping app [stage: " + stage + "]..");

		// create the dependency injector
		log.debug("Creating dependency injector..");
		final Injector injector =
				createInjector(servletContext, "dev".equals(stage) ? Stage.DEVELOPMENT : Stage.PRODUCTION, config);

		// start 'em up
		if(handlers != null) {
			log.debug("Starting up dependency handlers..");
			for(final IBootstrapHandler handler : handlers) {
				handler.startup(injector, servletContext);
			}
		}
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// shut 'em down
		if(handlers != null) {
			log.debug("Shutting down dependency handlers..");
			for(final IBootstrapHandler handler : handlers) {
				handler.shutdown(servletContextEvent.getServletContext());
			}
		}
	}
}
