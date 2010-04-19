/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import com.google.inject.Scopes;
import com.tabulaw.model.EntityMetadata;
import com.tabulaw.model.EntityTypeResolver;
import com.tabulaw.model.IEntityMetadata;
import com.tabulaw.model.IEntityTypeResolver;
import com.tabulaw.schema.ISchemaInfo;
import com.tabulaw.schema.SchemaInfo;

/**
 * @author jpk
 */
public class ModelModule extends ValidationModule {

	@Override
	protected void configure() {
		super.configure();
		bind(IEntityMetadata.class).to(EntityMetadata.class).in(Scopes.SINGLETON);

		bind(ISchemaInfo.class).to(SchemaInfo.class).in(Scopes.SINGLETON);
		
		bind(IEntityTypeResolver.class).to(EntityTypeResolver.class).in(Scopes.SINGLETON);

		// IEntityFactory
		// bind(new
		// TypeLiteral<IEntityFactory<?>>(){}).to(Db4oEntityFactory.class).in(Scopes.SINGLETON);

		// IEntityAssembler
		// bind(IEntityAssembler.class).to(EntityAssembler.class).in(Scopes.SINGLETON);
	}

}
