/**
 * The Logic Lab
 * @author jpk
 * Sep 3, 2007
 */
package com.tll.tabulaw.common.data.rpc;

import java.util.List;

import com.tll.common.data.Payload;
import com.tll.common.data.Status;
import com.tll.common.model.Model;

/**
 * AdminContextPayload - Payload for initializing the client-side admin context.
 * @author jpk
 */
public class UserContextPayload extends Payload {

	/**
	 * The logged in user or the user for this http session.
	 */
	private Model user;

	private List<Model> bundles;

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
	public Model getUser() {
		return user;
	}

	public void setUser(Model user) {
		this.user = user;
	}

	/**
	 * @return The defined quote bundles and the referenced qoutes for the user.
	 */
	public List<Model> getBundles() {
		return bundles;
	}

	public void setBundles(List<Model> bundles) {
		this.bundles = bundles;
	}
}
