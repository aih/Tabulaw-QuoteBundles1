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
import com.tabulaw.model.IEntityFactory;
import com.tabulaw.server.PersistContext;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;
import com.tll.mail.MailManager;
import com.tll.schema.ISchemaInfo;
import com.tll.server.rpc.IExceptionHandler;

/**
 * @author jpk
 */
public class PersistContextModule extends AbstractModule {

	@Override
	protected void configure() {
		
		// PersistContext
		bind(PersistContext.class).toProvider(new Provider<PersistContext>() {

			@Inject(optional = true)
			MailManager mailManager;
			@Inject
			ISchemaInfo schemaInfo;
			@Inject
			IEntityFactory<?> entityFactory;
			//@Inject
			//IEntityAssembler entityAssembler;
			@Inject
			IExceptionHandler exceptionHandler;
			@Inject
			CacheManager persistCache;
			@Inject
			UserService userService;
			@Inject
			UserDataService userDataService;

			@Override
			public PersistContext get() {
				return new PersistContext(mailManager, schemaInfo, 
						entityFactory, exceptionHandler, persistCache, userService, userDataService);
			}
		}).in(Scopes.SINGLETON);
	}

}
