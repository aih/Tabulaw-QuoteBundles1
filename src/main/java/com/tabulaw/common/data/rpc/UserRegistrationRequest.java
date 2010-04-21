/**
 * The Logic Lab
 * @author jpk
 * @since Apr 19, 2010
 */
package com.tabulaw.common.data.rpc;

/**
 * @author jpk
 */
public class UserRegistrationRequest {

	private String emailAddress, password;

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
