/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tll.tabulaw.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tll.common.model.Model;
import com.tll.schema.PropertyType;
import com.tll.tabulaw.common.model.PocEntityType;
import com.tll.tabulaw.server.rpc.UserContextService.UserContext;

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

		// TODO temp manage http sessions in filter!
		HttpSession session = req.getSession(false);
		if(session == null) {
			session = req.getSession(true);
			log.info("Created user session: " + session);
		}

		// authenticate
		String username = req.getParameter("j_username");
		String password = req.getParameter("j_password");
		if(!authenticate(username, password)) {
			PrintWriter pw = resp.getWriter();
			pw.write("Invalid credentials");
			pw.flush();
			return;
		}

		UserContext userContext = (UserContext) session.getAttribute(UserContext.KEY);
		if(userContext == null) {
			userContext = new UserContext();
			session.setAttribute(UserContext.KEY, userContext);
		}

		// create user model
		Date now = new Date();
		Model mUser = new Model(PocEntityType.USER);
		mUser.setId("1");
		mUser.setString("username", username);
		mUser.setString("password", username);
		mUser.setProperty("enabled", Boolean.TRUE, PropertyType.BOOL);
		mUser.setProperty("dateCreated", now, PropertyType.DATE);
		mUser.setProperty("dateModified", now, PropertyType.DATE);

		userContext.setUser(mUser);
	}
}
