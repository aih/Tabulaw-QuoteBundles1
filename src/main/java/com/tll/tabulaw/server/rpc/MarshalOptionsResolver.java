/**
 * The Logic Lab
 * @author jpk
 * @since Jun 24, 2009
 */
package com.tll.tabulaw.server.rpc;

import com.tll.common.model.IEntityType;
import com.tll.server.marshal.IMarshalOptionsResolver;
import com.tll.server.marshal.MarshalOptions;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * MarshalOptionsResolver
 * @author jpk
 */
public class MarshalOptionsResolver implements IMarshalOptionsResolver {

	private static final MarshalOptions DEFAULT_OPTIONS = new MarshalOptions(false, 0);
	
	public static final MarshalOptions USER_OPTIONS = DEFAULT_OPTIONS;
	public static final MarshalOptions CASE_OPTIONS = DEFAULT_OPTIONS;
	public static final MarshalOptions DOC_OPTIONS = new MarshalOptions(true, 1);
	public static final MarshalOptions QUOTE_OPTIONS = DOC_OPTIONS;
	
	// qb(0) -> qoute(1) -> doc(2) -> case(3)
	public static final MarshalOptions QUOTE_BUNDLE_OPTIONS = new MarshalOptions(true, 3);
	
	@Override
	public MarshalOptions resolve(IEntityType entityType) throws IllegalArgumentException {
		if(entityType instanceof PocEntityType) {
			final PocEntityType set = (PocEntityType) entityType;
			switch(set) {
				case USER:
					return USER_OPTIONS;
				case CASE:
					return CASE_OPTIONS;
				case DOCUMENT:
					return DOC_OPTIONS;
				case QUOTE:
					return QUOTE_OPTIONS;
				case QUOTE_BUNDLE:
					return QUOTE_BUNDLE_OPTIONS;
			}
		}
		throw new IllegalArgumentException("Un-handled entity type: " + entityType);
	}
}
