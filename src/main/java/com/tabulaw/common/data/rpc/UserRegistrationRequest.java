/**
 * The Logic Lab
 * @author jpk
 * @since Apr 19, 2010
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.IMarshalable;

/**
 * @author jpk
 */
public class UserRegistrationRequest implements IMarshalable {

	private String emailAddress, password;

	public UserRegistrationRequest() {
		super();
	}

	public UserRegistrationRequest(String emailAddress, String password) {
		super();
		this.emailAddress = emailAddress;
		this.password = password;
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
