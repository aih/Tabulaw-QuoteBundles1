package com.tabulaw.mail;

import com.google.inject.Inject;

/**
 * Encapsulates a name and email address
 * @author jpk
 */
public class NameEmail implements Cloneable {

	private String name;

	private String emailAddress;

	/**
	 * Constructor
	 */
	public NameEmail() {
		super();
	}

	/**
	 * Constructor
	 * @param name
	 * @param emailAddress
	 */
	@Inject
	public NameEmail(String name, String emailAddress) {
		this();
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

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	protected NameEmail clone() {
		try {
			final NameEmail cln = (NameEmail) super.clone();
			return cln;
		}
		catch(final CloneNotSupportedException e) {
			throw new Error("NameEmail cloning not supported and should be!");
		}
	}

}
