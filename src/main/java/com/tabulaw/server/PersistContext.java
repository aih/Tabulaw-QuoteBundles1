/**
 * The Logic Lab
 * @author jpk Jan 30, 2009
 */
package com.tabulaw.server;

import java.io.Serializable;

import javax.servlet.ServletContext;

import net.sf.ehcache.CacheManager;

import com.google.inject.Inject;
import com.tabulaw.schema.ISchemaInfo;
import com.tabulaw.server.rpc.IExceptionHandler;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;

/**
 * Servlet context scoped object providing necessary apis for gettin stuff done
 * server side.
 * @author jpk
 */
public final class PersistContext implements Serializable {

	private static final long serialVersionUID = 7366163949288867262L;

	/**
	 * The key identifying the {@link PersistContext} in the
	 * {@link ServletContext}.
	 */
	public static final String KEY = Long.toString(serialVersionUID);

	private final ISchemaInfo schemaInfo;
	private final CacheManager cacheManager;

	private final UserService userService;
	private final UserDataService userDataService;

	/**
	 * Constructor
	 * @param schemaInfo
	 * @param exceptionHandler
	 * @param cacheManager
	 * @param userService
	 * @param userDataService
	 */
	@Inject
	public PersistContext(ISchemaInfo schemaInfo, IExceptionHandler exceptionHandler, CacheManager cacheManager,
			UserService userService, UserDataService userDataService) {
		super();
		this.schemaInfo = schemaInfo;
		this.cacheManager = cacheManager;

		this.userService = userService;
		this.userDataService = userDataService;
	}

	public ISchemaInfo getSchemaInfo() {
		return schemaInfo;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public UserService getUserService() {
		return userService;
	}

	public UserDataService getUserDataService() {
		return userDataService;
	}
}
