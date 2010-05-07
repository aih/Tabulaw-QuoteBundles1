/**
 * The Logic Lab
 * @author jpk
 * @since Apr 16, 2010
 */
package com.tabulaw.model;

import com.tabulaw.common.model.BundleUserBinding;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.DocUserBinding;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.UserState;

/**
 * @author jpk
 */
public class EntityTypeResolver implements IEntityTypeResolver {

	@Override
	public Class<? extends IEntity> resolveEntityClass(String entityType) throws IllegalArgumentException {
		EntityType et = EntityType.fromString(entityType);
		switch(et) {
			case BUNDLE_USER_BINDING:
				return BundleUserBinding.class;
			case DOC_USER_BINDING:
				return DocUserBinding.class;
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
			case USER_STATE:
				return UserState.class;
		}
		throw new IllegalArgumentException("Un-handled entity type: " + et);
	}

	@Override
	public String resolveEntityType(Class<? extends IEntity> clz) throws IllegalArgumentException {
		if(clz == BundleUserBinding.class) {
			return EntityType.BUNDLE_USER_BINDING.name();
		}
		else if(clz == DocUserBinding.class) {
			return EntityType.DOC_USER_BINDING.name();
		}
		else if(clz == User.class) {
			return EntityType.USER.name();
		}
		else if(clz == UserState.class) {
			return EntityType.USER_STATE.name();
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
		
		throw new IllegalArgumentException("Un-handled entity class: " + clz);
	}

}
