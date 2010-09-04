/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import net.sf.ehcache.CacheManager;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tabulaw.schema.ISchemaInfo;
import com.tabulaw.server.PersistContext;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;

/**
 * @author jpk
 */
public class PersistContextModule extends AbstractModule {

	@Override
	protected void configure() {

		// PersistContext
		bind(PersistContext.class).toProvider(new Provider<PersistContext>() {

			@Inject
			ISchemaInfo schemaInfo;
			@Inject
			CacheManager persistCache;
			@Inject
			UserService userService;
			@Inject
			UserDataService userDataService;

			@Override
			public PersistContext get() {
				return new PersistContext(schemaInfo, persistCache, userService, userDataService);
			}
		}).in(Scopes.SINGLETON);
	}

}
