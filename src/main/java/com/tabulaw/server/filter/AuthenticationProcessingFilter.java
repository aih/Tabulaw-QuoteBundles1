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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	public static final String AUTH_EXCEPTION_KEY = "AuthenticationExcepiton";

	private static final String filterProcessesUrl = "/login";

	static class SecurityContext {

		User user;
	}

	// private static ThreadLocal<SecurityContext> contextHolder = new
	// ThreadLocal<SecurityContext>();

	static class Authentication {

		String username, password;

		boolean authenticated;

		User principal;
		String[] roles;

		public Authentication(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}
	}

	/*
	SecurityContext getSecurityContext() {
		if(contextHolder.get() == null) {
			contextHolder.set(new SecurityContext());
		}
		return contextHolder.get();
	}

	void setSecurityContext(SecurityContext context) {
		Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
		contextHolder.set(context);
	}

	void clearContext() {
		contextHolder.set(null);
	}
	*/

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false);

		// boolean showLoginPage = false;

		if(requiresAuthentication(httpRequest, httpResponse)) {
			if(log.isDebugEnabled()) {
				log.debug("Request is to process authentication");
			}

			// auto-create session if necessary
			if(session == null) {
				log.info("Creating http session");
				session = httpRequest.getSession(true);
			}

			Authentication authResult;

			String finalUrl = null;

			try {
				authResult = attemptAuthentication(httpRequest);

				// create an user context for this user session
				log.debug("Creating user context from security context..");
				final UserContext context = new UserContext();
				context.setUser(authResult.principal);
				session.setAttribute(UserContext.KEY, context);

				finalUrl = httpRequest.getContextPath() + "/login";
			}
			catch(Exception failed) {
				// Authentication failed
				log.debug("User authentication failed");

				// put it in the session to get grabbed down the line
				session.setAttribute(AUTH_EXCEPTION_KEY, failed);

				finalUrl = httpRequest.getContextPath() + "/login?login_error=1";
			}

			httpResponse.sendRedirect(httpResponse.encodeRedirectURL(finalUrl));
			return;
		}

		chain.doFilter(request, response);
	}

	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();

		// strip everything after the first semi-colon if present
		int pathParamIndex = uri.indexOf(';');
		if(pathParamIndex > 0) {
			uri = uri.substring(0, pathParamIndex);
		}

		if("".equals(request.getContextPath())) {
			return uri.endsWith(filterProcessesUrl);
		}

		return uri.endsWith(request.getContextPath() + filterProcessesUrl);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	public Authentication attemptAuthentication(HttpServletRequest request) throws Exception {
		String username = request.getParameter("j_username");
		String password = request.getParameter("j_password");

		if(username == null) {
			username = "";
		}

		if(password == null) {
			password = "";
		}

		username = username.trim();

		Authentication authRequest = new Authentication(username, password);

		// Place the last username attempted into HttpSession for views
		HttpSession session = request.getSession(false);
		if(session == null) throw new IllegalStateException("No http session exists.");

		// session.setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY,
		// TextUtils.escapeEntities(username));
		session.setAttribute("lastUsername", username);

		// Allow subclasses to set the "details" property
		// setDetails(request, authRequest);

		try {
			// return this.getAuthenticationManager().authenticate(authRequest);
			PersistContext persistContext = (PersistContext) session.getServletContext().getAttribute(PersistContext.KEY);
			UserService userService = persistContext.getUserService();
			User user = userService.findByEmail(authRequest.username);

			// compare passwords
			if(UserService.isPasswordValid(password, user.getPassword(), user.getEmailAddress())) {
				authRequest.principal = user;
				return authRequest;
			}

			throw new Exception("Bad credentials");
		}
		catch(RuntimeException re) {
			throw new Exception(re);
		}
	}
}
