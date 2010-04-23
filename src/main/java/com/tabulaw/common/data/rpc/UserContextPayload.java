/**
 * The Logic Lab
 * @author jpk
 * Sep 3, 2007
 */
package com.tabulaw.common.data.rpc;

import java.util.List;

import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.UserState;

/**
 * AdminContextPayload - Payload for initializing the client-side admin context.
 * @author jpk
 */
public class UserContextPayload extends Payload {

	/**
	 * The logged in user or the user for this http session.
	 */
	private User user;

	private UserState userState;

	private List<QuoteBundle> bundles;

	/**
	 * Constructor
	 */
	public UserContextPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public UserContextPayload(Status status) {
		super(status);
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return The persisted client-side user state from the last logged in
	 *         session.
	 */
	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	/**
	 * @return The defined quote bundles and the referenced qoutes for the user.
	 */
	public List<QuoteBundle> getBundles() {
		return bundles;
	}

	public void setBundles(List<QuoteBundle> bundles) {
		this.bundles = bundles;
	}
}
