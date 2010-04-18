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
 * LogoutServlet - Handles logout submissions.
 * @author jpk
 */
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = -2243215015629188186L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			HttpSession session = req.getSession(false);
			if(session != null) {
				session.invalidate();
			}
		}
		catch(IllegalStateException e) {
		}
		resp.setContentType("text/html");
		resp.getWriter().write("");
	}

}
