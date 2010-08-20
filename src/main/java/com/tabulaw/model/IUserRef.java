package com.tabulaw.model;

/**
 * UserRef - Simple def to hold needed user data for resetting the password.
 * @author jpk
 */
public interface IUserRef {

	String getUserRefId();
	
	String getPassword();
	
	String getName();
	
	String getEmailAddress();
}