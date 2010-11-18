/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 19, 2010
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.IMarshalable;

/**
 * @author jpk
 */
public class UserRegistrationRequest implements IMarshalable {

	private String name, emailAddress, password;

	/**
	 * Constructor
	 */
	public UserRegistrationRequest() {
		super();
	}

	/**
	 * Constructor
	 * @param name
	 * @param emailAddress
	 * @param password
	 */
	public UserRegistrationRequest(String name, String emailAddress, String password) {
		super();
		this.name = name;
		this.emailAddress = emailAddress;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
