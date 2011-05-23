/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Sep 21, 2009
 */
package com.tabulaw.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import com.tabulaw.config.Config;
import com.tabulaw.config.IConfigAware;
import com.tabulaw.config.IConfigKey;

/**
 * Db4oDaoModule
 * 
 * @author jpk
 */
public class DataSourceDaoModule extends AbstractModule implements IConfigAware {

	private static final int DEFAULT_TRANS_TIMEOUT = 60; // seconds

	private static final boolean DEFAULT_EMPLOY_SPRING_TRANSACTIONS = false;

	static final Log log = LogFactory.getLog(DataSourceDaoModule.class);

	/**
	 * Db4oFile annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface Db4oFile {
	}

	/**
	 * ConfigKeys.
	 * 
	 * @author jpk
	 */
	public static enum ConfigKeys implements IConfigKey {

		DB_CONTEXT("db.context"), DB_TRANS_TIMEOUT("db.transaction.timeout");

		private final String key;

		private ConfigKeys(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}
	} // ConfigKeys

	Config config;

	/**
	 * Constructor
	 */
	public DataSourceDaoModule() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param config
	 */
	public DataSourceDaoModule(Config config) {
		super();
		this.config = config;
	}

	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	protected final void configure() {
		log.info("Loading dao module...");

		log.info("Binding Spring's TransactionManager to Spring's @Transactional annotation..");
		final DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();

		bind(DataSource.class).toProvider(new Provider<DataSource>() {

			@Override
			public DataSource get() {
				Context initContext;
				DataSource ds = null;
				try {
					initContext = new InitialContext();
					 ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/pgsql");
				} catch (NamingException e1) {
					e1.printStackTrace();
				}
				return ds;
			}
		}).in(Scopes.SINGLETON);

		// PlatformTransactionManager (for transactions)
		bind(PlatformTransactionManager.class).toProvider(new Provider<PlatformTransactionManager>() {
			@Inject
			DataSource ds;

			@Override
			public PlatformTransactionManager get() {

				// set the transaction timeout
				final int timeout = config == null ? DEFAULT_TRANS_TIMEOUT : config.getInt(ConfigKeys.DB_TRANS_TIMEOUT
						.getKey(), DEFAULT_TRANS_TIMEOUT);
//				dataSourceTransactionManager.setDefaultTimeout(timeout);
				log.info("Set default transaction timeout to: " + timeout);

				dataSourceTransactionManager.setDataSource(ds);

				// required for AspectJ weaving of Spring's @Transactional
				// annotation
				// (must be invoked PRIOR to an @Transactional method call
				AnnotationTransactionAspect.aspectOf().setTransactionManager(dataSourceTransactionManager);

				return dataSourceTransactionManager;
			}
		}).asEagerSingleton();
		// IMPT: asEagerSingleton() to force binding trans manager to
		// @Transactional!
		
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class),
                new TransactionInterceptor(dataSourceTransactionManager, new AnnotationTransactionAttributeSource()));		
	}

}
