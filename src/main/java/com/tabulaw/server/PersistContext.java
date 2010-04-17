/**
 * The Logic Lab
 * @author jpk Jan 30, 2009
 */
package com.tabulaw.server;

import javax.servlet.ServletContext;

import net.sf.ehcache.CacheManager;

import com.google.inject.Inject;
import com.tabulaw.model.IEntityFactory;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;
import com.tll.mail.MailManager;
import com.tll.schema.ISchemaInfo;
import com.tll.server.rpc.IExceptionHandler;

/**
 * Servlet context scoped object providing necessary apis for gettin stuff done
 * server side.
 * @author jpk
 */
public final class PersistContext {

	private static final long key = 7366163949288867262L;

	/**
	 * The key identifying the {@link PersistContext} in the
	 * {@link ServletContext}.
	 */
	public static final String KEY = Long.toString(key);

	private final MailManager mailManager;
	private final ISchemaInfo schemaInfo;
	private final IEntityFactory<?> entityFactory;
	private final IExceptionHandler exceptionHandler;
	private final CacheManager cacheManager;

	private final UserService userService;
	private final UserDataService userDataService;

	/**
	 * Constructor
	 * @param mailManager
	 * @param schemaInfo
	 * @param entityFactory
	 * @param exceptionHandler
	 * @param cacheManager
	 * @param userService
	 * @param userDataService
	 */
	@Inject
	public PersistContext(MailManager mailManager, ISchemaInfo schemaInfo, IEntityFactory<?> entityFactory,
			IExceptionHandler exceptionHandler, CacheManager cacheManager, UserService userService,
			UserDataService userDataService) {
		super();
		this.mailManager = mailManager;
		this.schemaInfo = schemaInfo;
		this.entityFactory = entityFactory;
		this.exceptionHandler = exceptionHandler;
		this.cacheManager = cacheManager;

		this.userService = userService;
		this.userDataService = userDataService;
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public ISchemaInfo getSchemaInfo() {
		return schemaInfo;
	}

	public IEntityFactory<?> getEntityFactory() {
		return entityFactory;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
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
