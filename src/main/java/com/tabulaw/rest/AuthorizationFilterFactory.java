package com.tabulaw.rest;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

import com.tabulaw.rest.resources.BaseResource;
import com.tabulaw.server.UserContext;

/**
 * 
 * Resource filter which is responsible to process AuthorizationRequired annotation.
 * If the requested resource or method has this annotation the filter automatically
 * gets "sessionToken" parameter and checks is it valid.   
 * 
 * @author yuri
 *
 */
public class AuthorizationFilterFactory implements ResourceFilterFactory {

	@Context
	HttpServletRequest httpRequest;
	
	@Override
	public List<ResourceFilter> create(AbstractMethod method) {
		AbstractResource resource = method.getResource();
		
		if (method.isAnnotationPresent(AuthorizationRequired.class) ||
				resource.isAnnotationPresent(AuthorizationRequired.class)) {
			return Arrays.asList((ResourceFilter) new AuthorizationResourceFilter());
		}
		return null;
	}
	
	private class AuthorizationResourceFilter implements ResourceFilter, ContainerRequestFilter {

		@Override
		public ContainerRequest filter(ContainerRequest request) {
			String sessionToken = request.getQueryParameters().getFirst("sessionToken");
			if (sessionToken == null && ! request.getMethod().equals("GET")) {
				sessionToken = request.getFormParameters().getFirst("sessionToken");
			}			
			if (sessionToken != null) {
				HttpSession session = RESTSessionManager.findSession(sessionToken);
				if (session != null) {
					RESTServletRequest.changeSession(httpRequest, session);
					if (session.getAttribute(BaseResource.REST_USER_KEY) == null &&
						session.getAttribute(UserContext.KEY) != null) {
						session.setAttribute(BaseResource.REST_USER_KEY, session.getAttribute(UserContext.KEY));
					}
					return request;
				}
				throw new WebApplicationException(Status.UNAUTHORIZED);
			} 
			throw new WebApplicationException(Status.BAD_REQUEST);			
		}

		@Override
		public ContainerRequestFilter getRequestFilter() {
			return this;
		}

		@Override
		public ContainerResponseFilter getResponseFilter() {
			return null;
		}
	}
}
