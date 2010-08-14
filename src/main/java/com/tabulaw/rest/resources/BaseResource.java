package com.tabulaw.rest.resources;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;

/**
 * 
 * Base class for all REST recources
 * 
 * @author yuri
 *
 */
public class BaseResource {	
	static final String REST_USER_KEY = UserContext.KEY + "-rest";

	@Context
	protected HttpServletRequest httpRequest;
	
	@Context
	ServletContext servletContext;
	
	protected PersistContext getPersistContext() {
		return (PersistContext) servletContext.getAttribute(PersistContext.KEY);		
	}
	
	protected UserDataService getDataService() {
		return getPersistContext().getUserDataService();
	}
	
	protected UserService getUserService() {
		return getPersistContext().getUserService();
	}
	
	protected UserContext getUser() {
		return (UserContext) httpRequest.getSession().getAttribute(REST_USER_KEY);
	}
	
	protected String getUserId() {
		return getUser().getUser().getId();
	}
}
