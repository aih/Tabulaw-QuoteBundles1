/**
 * The Logic Lab
 */
package com.tll.tabulaw.server.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.context.SecurityContextHolder;

import com.tll.server.filter.AuthenticationProcessingFilter;
import com.tll.tabulaw.model.User;
import com.tll.tabulaw.server.UserContext;

/**
 * TabulawAuthenticationProcessingFilter
 * @author jpk
 */
public final class TabulawAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
	
	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) /*throws IOException*/{

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
			AuthenticationException failed) /*throws IOException*/{
		log.debug("User authentication failed");
		// no-op
	}
}
