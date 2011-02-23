/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Jan 30, 2009
 */
package com.tabulaw.server;

import java.io.Serializable;

import javax.servlet.ServletContext;

import com.google.inject.Inject;
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

	private final UserService userService;
	private final UserDataService userDataService;

	/**
	 * Constructor
	 * @param schemaInfo
	 * @param cacheManager
	 * @param userService
	 * @param userDataService
	 */
	@Inject
	public PersistContext(UserService userService, UserDataService userDataService) {
		super();

		this.userService = userService;
		this.userDataService = userDataService;
	}

	public UserService getUserService() {
		return userService;
	}

	public UserDataService getUserDataService() {
		return userDataService;
	}
}
