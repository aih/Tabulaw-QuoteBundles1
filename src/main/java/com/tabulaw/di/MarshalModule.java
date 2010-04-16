/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.di;

import com.tabulaw.server.rpc.EntityTypeResolver;
import com.tabulaw.server.rpc.MarshalOptionsResolver;
import com.tll.server.marshal.IMarshalOptionsResolver;
import com.tll.server.rpc.entity.IEntityTypeResolver;

/**
 * @author jpk
 */
public class MarshalModule extends com.tll.di.MarshalModule {

	@Override
	protected Class<? extends IEntityTypeResolver> getEntityTypeResolverImplType() {
		return EntityTypeResolver.class;
	}

	@Override
	protected Class<? extends IMarshalOptionsResolver> getMarshalOptionsResolverImplType() {
		return MarshalOptionsResolver.class;
	}

}
