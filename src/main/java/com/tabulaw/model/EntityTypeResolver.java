/**
 * The Logic Lab
 * @author jpk
 * @since Apr 16, 2010
 */
package com.tabulaw.model;

import com.tabulaw.common.model.Authority;
import com.tabulaw.common.model.BundleUserBinding;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;

/**
 * @author jpk
 */
public class EntityTypeResolver implements IEntityTypeResolver {

	@Override
	public Class<?> resolveEntityClass(String entityType) throws IllegalArgumentException {
		EntityType et = Enum.valueOf(EntityType.class, entityType);
		switch(et) {
			case AUTHORITY:
				return Authority.class;
			case BUNDLE_USER_BINDING:
				return BundleUserBinding.class;
			case CASE:
				return CaseRef.class;
			case DOCUMENT:
				return DocRef.class;
			case QUOTE:
				return Quote.class;
			case QUOTE_BUNDLE:
				return QuoteBundle.class;
			case USER:
				return User.class;
			default:
			case NOTE:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public String resolveEntityType(Class<?> clz) throws IllegalArgumentException {
		if(clz == Authority.class) {
			return EntityType.AUTHORITY.name();
		}
		else if(clz == BundleUserBinding.class) {
			return EntityType.BUNDLE_USER_BINDING.name();
		}
		else if(clz == BundleUserBinding.class) {
			return EntityType.BUNDLE_USER_BINDING.name();
		}
		else if(clz == CaseRef.class) {
			return EntityType.CASE.name();
		}
		else if(clz == DocRef.class) {
			return EntityType.DOCUMENT.name();
		}
		/*
		else if(clz == Note.class) {
			return EntityType.NOTE;
		}
		*/
		else if(clz == Quote.class) {
			return EntityType.QUOTE.name();
		}
		else if(clz == QuoteBundle.class) {
			return EntityType.QUOTE_BUNDLE.name();
		}
		throw new IllegalArgumentException();
	}

}
