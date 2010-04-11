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

	private static final MarshalOptions DEFAULT_OPTIONS = new MarshalOptions(false, 1, null);
	
	public static final MarshalOptions USER_OPTIONS = DEFAULT_OPTIONS;
	public static final MarshalOptions CASE_OPTIONS = DEFAULT_OPTIONS;
	public static final MarshalOptions DOC_OPTIONS = new MarshalOptions(true, 2, null);
	public static final MarshalOptions QUOTE_OPTIONS = DEFAULT_OPTIONS;
	public static final MarshalOptions QUOTE_BUNDLE_OPTIONS = DOC_OPTIONS;

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
