package com.tabulaw.openid;

import org.apache.commons.lang.StringUtils;

import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.service.entity.UserService;

public class UserSignIn {

	private final PersistContext persistContext;

	public UserSignIn(PersistContext persistContext) {
		this.persistContext = persistContext;
	}

	public User doUserSignIn(UserInfo openIdUser) {
		UserService userService = persistContext.getUserService();
		User user;
		try {
			user = userService.findByEmail(openIdUser.getEmail());
		} catch (EntityNotFoundException e) {
			user = userService.create(getUserName(openIdUser),
					openIdUser.getEmail(), getPasswordGenerated());
		}
		return user;
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
