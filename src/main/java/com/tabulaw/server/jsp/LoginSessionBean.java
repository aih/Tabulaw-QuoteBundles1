package com.tabulaw.server.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tabulaw.server.UserContext;

public class LoginSessionBean {

	private HttpServletRequest request;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public boolean isLoggedIn() {
		if (request != null) {
			HttpSession session = request.getSession(false);
			return session.getAttribute(UserContext.KEY) != null;
		} else {
			return false;
		}
	}
}
