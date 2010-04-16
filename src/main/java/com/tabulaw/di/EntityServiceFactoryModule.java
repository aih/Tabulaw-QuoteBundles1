/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import org.springframework.security.providers.dao.UserCache;
import org.springframework.security.providers.dao.cache.NullUserCache;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;

/**
 * @author jpk
 */
public class EntityServiceFactoryModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserCache.class).to(NullUserCache.class).in(Scopes.SINGLETON);
		bind(UserService.class).in(Scopes.SINGLETON);
		bind(UserDataService.class).in(Scopes.SINGLETON);
	}

}
