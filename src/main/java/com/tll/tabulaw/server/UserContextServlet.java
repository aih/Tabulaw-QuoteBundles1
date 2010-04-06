/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tll.tabulaw.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jpk
 */
public class UserContextServlet extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(UserContextServlet.class);

	private static final long serialVersionUID = -2678303311166693090L;
	
	private static boolean authenticate(String username, String password) {
		if(!"tabulaw".equals(username)) {
			return false;
		}
		if(!"poc123".equals(password)) {
			return false;
		}
		return true;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// for now this is just a stub
		HttpSession session = req.getSession(false);
		if(session == null) {
			// authenticate
			String username = req.getParameter("j_username");
			String password = req.getParameter("j_password");
			if(!authenticate(username, password)) {
				PrintWriter pw = resp.getWriter();
				pw.write("Invalid credentials");
				pw.flush();
				return;
			}
			session = req.getSession(true);
			log.info("Created user session: " + session);
		}
	}
}
