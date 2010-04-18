/**
 * The Logic Lab
 */
package com.tabulaw.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.tabulaw.common.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserService;

/**
 * AuthenticationProcessingFilter
 * @author jpk
 */
public final class AuthenticationProcessingFilter implements Filter {
	
	private static final Log log = LogFactory.getLog(AuthenticationProcessingFilter.class);

	static class SecurityContext {

		User user;
	}

	private static ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<SecurityContext>();

	SecurityContext getContext() {
		if(contextHolder.get() == null) {
			contextHolder.set(new SecurityContext());
		}
		return contextHolder.get();
	}

	void setContext(SecurityContext context) {
		Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
		contextHolder.set(context);
	}
	
	void clearContext() {
    contextHolder.set(null);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		HttpSession session = httpRequest.getSession(false);
		if(session == null) {
			log.info("Creating Http session");
			session = httpRequest.getSession(true);
			// for now just stick it in
			{
				PersistContext persistContext = (PersistContext) session.getServletContext().getAttribute(PersistContext.KEY);
				UserService userService = persistContext.getUserService();
				User user = userService.findByEmail("anon@tabulaw.com");
				if(user == null) throw new IllegalStateException();
				getContext().user = user;
			}
			
		}
		
		UserContext userContext = (UserContext) session.getAttribute(UserContext.KEY);
		if(userContext == null) {
			log.debug("Creating user context from security context..");
			final User user = getContext().user;
			if(user == null) throw new IllegalStateException();
			final UserContext context = new UserContext();
			context.setUser(user);
			session.setAttribute(UserContext.KEY, context);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	/*
	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException{

		// create an AdminContext for this servlet session
		log.debug("Creating admin context from acegi security context..");
		final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user == null) throw new IllegalStateException();
		final UserContext context = new UserContext();
		context.setUser(user);
		request.getSession(false).setAttribute(UserContext.KEY, context);
	}

	@Override
	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException{
		log.debug("User authentication failed");
		// no-op
	}
	*/

}
