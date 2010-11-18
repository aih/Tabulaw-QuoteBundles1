/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Sep 18, 2009
 */
package com.tabulaw.server;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.db4o.ObjectContainer;
import com.google.inject.Injector;

/**
 * Db4oBootstrapper
 * @author jpk
 */
public class Db4oBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(Db4oBootstrapper.class);

	private static final String KEY = Integer.toString(Db4oBootstrapper.class.getName().hashCode());

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.debug("Bootstrapping db4o..");
		// instantiate db4o (by forcing instantiation of the trans manager)
		injector.getInstance(PlatformTransactionManager.class);
		// retain the db4o's object container ref
		final Object oc = injector.getInstance(ObjectContainer.class);
		if(oc == null) throw new Error("Unable to obtain db4o's object container");
		servletContext.setAttribute(KEY, oc);
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		final ObjectContainer oc = (ObjectContainer) servletContext.getAttribute(KEY);
		if(oc != null) {
			log.debug("Shutting down db4o..");
			oc.close();
		}
	}
}
