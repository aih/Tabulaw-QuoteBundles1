/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import com.google.gwt.dev.util.collect.HashMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tabulaw.service.entity.UserDataService;
import com.tll.mail.MailManager;
import com.tll.model.IEntityAssembler;
import com.tll.model.IEntityFactory;
import com.tll.schema.ISchemaInfo;
import com.tll.server.marshal.IMarshalOptionsResolver;
import com.tll.server.marshal.Marshaler;
import com.tll.server.rpc.IExceptionHandler;
import com.tll.server.rpc.entity.IEntityTypeResolver;
import com.tll.server.rpc.entity.PersistContext;
import com.tll.service.IService;
import com.tll.service.entity.EntityServiceFactory;
import com.tll.service.entity.IEntityServiceFactory;

/**
 * @author jpk
 */
public class PersistContextModule extends AbstractModule {

	@Override
	protected void configure() {
		
		// IEntityServiceFactory
		bind(IEntityServiceFactory.class).toProvider(new Provider<IEntityServiceFactory>() {
			
			@Inject
			UserDataService uds;
			
			@Override
			public IEntityServiceFactory get() {
				HashMap<Class<? extends IService>, IService> map = new HashMap<Class<? extends IService>, IService>();
				map.put(UserDataService.class, uds);
				return new EntityServiceFactory(map);
			}
		});
		
		// PersistContext
		bind(PersistContext.class).toProvider(new Provider<PersistContext>() {

			@Inject(optional = true)
			MailManager mailManager;
			@Inject
			ISchemaInfo schemaInfo;
			@Inject
			Marshaler marshaler;
			@Inject
			IMarshalOptionsResolver marshalOptionsResolver;
			@Inject
			IEntityTypeResolver entityTypeResolver;
			@Inject
			IEntityFactory<?> entityFactory;
			@Inject
			IEntityAssembler entityAssembler;
			@Inject
			IEntityServiceFactory entityServiceFactory;
			@Inject
			IExceptionHandler exceptionHandler;
			//@Inject
			//PersistCache persistCache;

			@Override
			public PersistContext get() {
				return new PersistContext(mailManager, schemaInfo, marshaler, marshalOptionsResolver, entityTypeResolver,
						entityFactory, entityAssembler, entityServiceFactory, exceptionHandler, null);
			}
		}).in(Scopes.SINGLETON);
	}

}
