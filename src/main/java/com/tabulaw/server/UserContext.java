/**
 * The Logic Lab
 * @author jpk
 * @since Apr 9, 2010
 */
package com.tabulaw.server;

import java.io.Serializable;

import com.tabulaw.common.model.User;

public class UserContext implements Serializable {

	/**
	 * A unique token to serve as a pointer to an instance of this type.
	 */
	public static final String KEY = UserContext.class.getName();

	private static final long serialVersionUID = -5842387902136812951L;

	private User user;

	/**
	 * @return The currently logged in user.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Set the currently logged in user.
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
}