/**
 * The Logic Lab
 * @author jpk
 * Feb 11, 2009
 */
package com.tll.tabulaw.server.rpc;

import com.tll.common.data.IModelRelatedRequest;
import com.tll.server.rpc.entity.IPersistServiceImpl;
import com.tll.server.rpc.entity.IPersistServiceImplResolver;

/**
 * PersistServiceImplResolver
 * @author jpk
 */
public class PersistServiceImplResolver implements IPersistServiceImplResolver {

	@Override
	public Class<? extends IPersistServiceImpl> resolve(IModelRelatedRequest request) 
	throws IllegalArgumentException {

		// TODO impl

		// unhandled
		throw new IllegalArgumentException("Unhandled request: " + request.descriptor());
	}
}
