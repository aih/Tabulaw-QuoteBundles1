/**
 * The Logic Lab
 * @author jpk Dec 24, 2007
 */
package com.tll.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * RequestContext - Provides servlets w/ the http request and current session.
 * @author jpk
 */
public final class RequestContext {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	/**
	 * Constructor
	 * @param request The http request
	 * @param response The http response
	 */
	public RequestContext(final HttpServletRequest request, final HttpServletResponse response) {
		super();
		if(request == null) {
			throw new IllegalArgumentException("No http servlet request specified.");
		}
		if(response == null) {
			throw new IllegalArgumentException("No http servlet response specified.");
		}
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpSession getSession() {
		return request.getSession(false);
	}
	
	public ServletContext getServletContext() {
		return getSession().getServletContext();
	}

	protected HttpServletResponse getResponse() {
		return response;
	}
}
