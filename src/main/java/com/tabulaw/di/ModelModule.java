/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.tabulaw.model.EntityAssembler;
import com.tll.dao.db4o.Db4oEntityFactory;
import com.tll.di.ValidationModule;
import com.tll.model.EntityMetadata;
import com.tll.model.IEntityAssembler;
import com.tll.model.IEntityFactory;
import com.tll.model.IEntityMetadata;
import com.tll.schema.ISchemaInfo;
import com.tll.schema.SchemaInfo;

/**
 * @author jpk
 */
public class ModelModule extends ValidationModule {

	@Override
	protected void configure() {
		super.configure();
		bind(IEntityMetadata.class).to(EntityMetadata.class).in(Scopes.SINGLETON);

		bind(ISchemaInfo.class).to(SchemaInfo.class).in(Scopes.SINGLETON);

		// IEntityFactory
		bind(new TypeLiteral<IEntityFactory<?>>(){}).to(Db4oEntityFactory.class).in(Scopes.SINGLETON);

		// IEntityAssembler
		bind(IEntityAssembler.class).to(EntityAssembler.class).in(Scopes.SINGLETON);
	}

}
