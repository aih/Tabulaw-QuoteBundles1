package com.tabulaw.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * HttpServletRequest that supports HttpSession overriding<br><br>
 * 
 * If the request has attribute "CHANGED_SESSION_ATTRIBUTE" the session
 * specified in this attribute is used instead of standard session for the request
 * <br><br>
 * 
 * This class is used to be able specify session in GET or POST "sessionToken" parameter 
 * 
 * @author yuri
 *
 */
public class RESTServletRequest extends HttpServletRequestWrapper {

	private static final String CHANGED_SESSION_ATTRIBUTE = "CHANGED_SESSION_ATTRIBUTE"; 
	
	public RESTServletRequest(HttpServletRequest request) {
		super(request);
	}
	
	private HttpSession getChangedSession() {
		Object session = getAttribute(CHANGED_SESSION_ATTRIBUTE);
		if (session != null && session instanceof HttpSession) {
			return (HttpSession) session;
		}
		return null;
	}
	
	@Override
	public String getRequestedSessionId() {
		return getChangedSession() == null ? super.getRequestedSessionId() 
				: getChangedSession().getId();
	}

	@Override
	public HttpSession getSession() {
		return getChangedSession() == null ? super.getSession() : getChangedSession();
	}

	@Override
	public HttpSession getSession(boolean create) {
		return getChangedSession() == null ? super.getSession(create): getChangedSession();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return getChangedSession() == null ? super.isRequestedSessionIdFromCookie() : false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return getChangedSession() == null ? super.isRequestedSessionIdFromURL() : true;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return getChangedSession() == null ? super.isRequestedSessionIdValid() : true;
	}
	
	public static void changeSession(HttpServletRequest request, HttpSession session) {
		if (session == null) {
			throw new java.lang.IllegalArgumentException("Session can't be null!");
		}
		HttpSession currentSession = request.getSession(false);
		if (currentSession != null) {
			currentSession.invalidate();
		}
		// make changed session alive
		Object object = session.getAttribute("alive");
		session.setAttribute("alive", null);
		session.setAttribute("alive", object);
		
		request.setAttribute(CHANGED_SESSION_ATTRIBUTE, session);
		if (request.getSession(false) != session) {
			throw new IllegalStateException("Request doesn't support session changing");
		}
	}
}
