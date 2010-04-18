/**
 * The Logic Lab
 */
package com.tabulaw.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet - Handles login submissions.
 * @author jpk
 */
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 4786061277023363507L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws /* ServletException, */IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws /* ServletException, */IOException {
		HttpSession session = req.getSession(false);
		assert session != null;
		String msg = "";
		boolean loginError = (req.getParameter("login_error") != null);
		if(loginError) {
			/*
			AuthenticationException ae = (AuthenticationException) session
					.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
			if(ae != null) {
				// failed login (get error message)
				msg = ae.getMessage();
			}
			*/
		}
		resp.setContentType("text/html");
		resp.getWriter().write(msg);
	}

}
