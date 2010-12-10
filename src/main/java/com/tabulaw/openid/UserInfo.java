package com.tabulaw.openid;

import java.io.Serializable;

/**
 * Simple representation of an authenticated user.
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = -6259021984767685123L;
	
	private String claimedId;
	private String email;
	private String firstName;
	private String lastName;
	private boolean hasOpenIdOAuth;

	public UserInfo() {
	}

	public UserInfo(String claimedId, String email, String firstName,
			String lastName, boolean hasOpenIdOAuth) {
		this.claimedId = claimedId;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.hasOpenIdOAuth = hasOpenIdOAuth;
	}

	public String getClaimedId() {
		return claimedId;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setHasOpenIdOAuth(boolean hasOpenIdOAuth) {
		this.hasOpenIdOAuth = hasOpenIdOAuth;
	}

	public boolean isHasOpenIdOAuth() {
		return hasOpenIdOAuth;
	}
}