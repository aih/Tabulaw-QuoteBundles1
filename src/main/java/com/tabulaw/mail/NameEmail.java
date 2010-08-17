package com.tabulaw.mail;

import com.google.inject.Inject;

/**
 * Encapsulates a name and email address
 * @author jpk
 */
public class NameEmail implements Cloneable {

	private final String name, emailAddress;

	/**
	 * Constructor
	 * @param name
	 * @param emailAddress
	 */
	@Inject
	public NameEmail(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
	}

	/**
	 * Constructor
	 * @param emailAddress
	 */
	public NameEmail(String emailAddress) {
		this(null, emailAddress);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null || obj instanceof NameEmail == false) return false;
		final NameEmail that = (NameEmail) obj;
		return that.getEmailAddress() != null && that.getEmailAddress().equals(this.getEmailAddress());
	}

	@Override
	public int hashCode() {
		return getEmailAddress() == null ? 0 : 29 * getEmailAddress().hashCode();
	}

	@Override
	public NameEmail clone() {
		try {
			final NameEmail cln = (NameEmail) super.clone();
			return cln;
		}
		catch(final CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	@Override
	public String toString() {
		return "NameEmail [emailAddress=" + emailAddress + ", name=" + name + "]";
	}
}
