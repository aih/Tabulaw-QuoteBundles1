/**
 * The Logic Lab
 * @author jpk
 * Sep 3, 2007
 */
package com.tll.tabulaw.common.data.rpc;

import com.tll.common.data.Payload;
import com.tll.common.data.Status;
import com.tll.common.model.Model;

/**
 * AdminContextPayload - Payload for initializing the client-side admin
 * context.
 * @author jpk
 */
public class UserContextPayload extends Payload {

	/**
	 * debug config param
	 */
	private boolean debug;

	/**
	 * environment config param
	 */
	private String environment;

	/**
	 * The logged in user or the user for this http session.
	 */
	private Model user;

	/**
	 * Constructor
	 */
	public UserContextPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 * @param debug
	 * @param environment
	 * @param user
	 */
	public UserContextPayload(Status status, boolean debug, String environment, Model user) {
		super(status);
		this.debug = debug;
		this.environment = environment;
		this.user = user;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @return the user
	 */
	public Model getUser() {
		return user;
	}
}
