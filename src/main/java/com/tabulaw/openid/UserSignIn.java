package com.tabulaw.openid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserService;

public class UserSignIn {

	private final HttpServletRequest req;
	private final PersistContext persistContext;

	public UserSignIn(HttpServletRequest req) {
		this.req = req;
		this.persistContext = (PersistContext) req.getSession(false)
				.getServletContext().getAttribute(PersistContext.KEY);
	}

	public User signInUser(UserInfo openIdUser) throws IOException {
		UserService userService = persistContext.getUserService();
		User user;
		try {
			user = userService.findByEmail(openIdUser.getEmail());
		} catch (EntityNotFoundException e) {
			user = userService.create(getUserName(openIdUser),
					openIdUser.getEmail(), getPasswordGenerated());
		}
		reLoginUser(user);
		return user;
	}


	private void reLoginUser(User user) throws IOException {
		logout(req);
		if (user != null) {
			HttpSession session = req.getSession();
			UserContext context = new UserContext();
			context.setUser(user);
			session.setAttribute(UserContext.KEY, context);
		}
	}

	private void logout(HttpServletRequest req) {
		try {
			HttpSession session = req.getSession(false);
			if (session != null) {
				session.invalidate();
			}
		} catch (IllegalStateException e) {
		}
	}
	private String getPasswordGenerated() {
		// TODO change to random password and an user will reset the password
		// later
		return "openid";
	}

	private String getUserName(UserInfo user) {
		String name = StringUtils.defaultString(user.getFirstName()) + " "
				+ StringUtils.defaultString(user.getLastName());
		return StringUtils.trim(name);
	}
}
