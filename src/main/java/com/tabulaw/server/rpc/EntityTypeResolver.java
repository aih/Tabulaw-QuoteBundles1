/**
 * The Logic Lab
 * @author jpk
 * @since May 15, 2009
 */
package com.tabulaw.server.rpc;

import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.PocEntityType;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tll.common.model.IEntityType;
import com.tll.server.rpc.entity.IEntityTypeResolver;

/**
 * Bridge between client-side entity type decl and server-side entity type decl.
 * @author jpk
 */
public class EntityTypeResolver implements IEntityTypeResolver {

	@Override
	public Class<?> resolveEntityClass(IEntityType entityType) throws IllegalArgumentException {
		if(entityType instanceof PocEntityType == false) throw new IllegalArgumentException("Expeceted PocEntityType");
		
		final PocEntityType set = (PocEntityType) entityType;
		switch(set) {
			case USER:
				return User.class;
			case CASE:
				return CaseRef.class;
			case DOCUMENT:
				return DocRef.class;
			case QUOTE:
				return Quote.class;
			case QUOTE_BUNDLE:
				return QuoteBundle.class;
			default:
			case NOTE:
				throw new IllegalArgumentException("Un-handled entity type: " + set);
		}
	}

	@Override
	public IEntityType resolveEntityType(Class<?> clz) throws IllegalArgumentException {
		if(User.class == clz) {
			return PocEntityType.USER;
		}
		else if(CaseRef.class == clz) {
			return PocEntityType.CASE;
		}
		if(DocRef.class == clz) {
			return PocEntityType.DOCUMENT;
		}
		if(Quote.class == clz) {
			return PocEntityType.QUOTE;
		}
		if(QuoteBundle.class == clz) {
			return PocEntityType.QUOTE_BUNDLE;
		}
		throw new IllegalArgumentException("Un-handled entity class: " + clz);
	}

}
