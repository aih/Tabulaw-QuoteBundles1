package com.tabulaw.openid;

import java.io.IOException;
import java.util.Random;

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
		return quickGeneratePassword();
	}

	private String getUserName(UserInfo user) {
		String name = StringUtils.defaultString(user.getFirstName()) + " "
				+ StringUtils.defaultString(user.getLastName());
		return StringUtils.trim(name);
	}

	/** The random number generator. */
	private static String quickGeneratePassword() {
		Random r = new Random();
		int MIN_LENGTH = 16;

		/*
		 * Set of characters that is valid. Must be printable, memorable, and
		 * "won't break HTML" (i.e., not ' <', '>', '&', '=', ...). or break
		 * shell commands (i.e., not ' <', '>', '$', '!', ...). I, L and O are
		 * good to leave out, as are numeric zero and one.
		 */
		char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
				'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
				'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
				'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
				'2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@', };

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < MIN_LENGTH; i++) {
			sb.append(goodChar[r.nextInt(goodChar.length)]);
		}

		return sb.toString();
	}
}
