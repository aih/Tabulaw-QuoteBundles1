package com.tabulaw.rest.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.tabulaw.model.User;

@XmlRootElement(name = "session")
public class SessionResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String sessionToken;
	
	private User user;
	
	public SessionResponse() {
		
	}

	public SessionResponse(String sessionToken, User user) {
		super();
		this.sessionToken = sessionToken;
		this.user = user;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
