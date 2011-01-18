/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
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
			UserService userService;
			@Inject
			UserDataService userDataService;

			@Override
			public PersistContext get() {
				return new PersistContext(userService, userDataService);
			}
		}).in(Scopes.SINGLETON);
	}

}
