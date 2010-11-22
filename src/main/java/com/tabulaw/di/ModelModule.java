/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tabulaw.model.EntityTypeResolver;
import com.tabulaw.model.IEntityTypeResolver;
import com.tabulaw.schema.EntityMetadata;
import com.tabulaw.schema.IEntityMetadata;
import com.tabulaw.schema.ISchemaInfo;
import com.tabulaw.schema.SchemaInfo;

/**
 * @author jpk
 */
public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		
		// ValidationFactory
		bind(ValidatorFactory.class).toProvider(new Provider<ValidatorFactory>() {

			@Override
			public ValidatorFactory get() {
				return Validation.buildDefaultValidatorFactory();
			}
		}).in(Scopes.SINGLETON);
		
		bind(IEntityMetadata.class).to(EntityMetadata.class).in(Scopes.SINGLETON);

		bind(ISchemaInfo.class).to(SchemaInfo.class).in(Scopes.SINGLETON);
		
		bind(IEntityTypeResolver.class).to(EntityTypeResolver.class).in(Scopes.SINGLETON);
	}

}
