/*
 * Created on Jan 1, 2005
 */
package com.tabulaw.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tabulaw.schema.BusinessKeyDef;
import com.tabulaw.schema.BusinessObject;

/**
 * The user entity. NOTE: no surrogate primary key is needed here.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Email Address", properties = { "emailAddress"
}))
public class User extends TimeStampEntity implements IUserRef, INamedEntity {

	private static final long serialVersionUID = -6126885590318834318L;

	public static final int MAXLEN_NAME = 50;
	public static final int MAXLEN_EMAIL_ADDRESS = 128;
	public static final int MAXLEN_PASSWORD = 255;

	public static final String SUPERUSER = "jpk";

	private String name;

	private String emailAddress;

	private String password;

	private boolean locked = true;

	private boolean enabled = true;

	private Date expires;

	// NOTE make sure this is non-final for gwt rpc
	private ArrayList<Authority> authorities;

	/**
	 * Constructor
	 */
	public User() {
		super();
		authorities = new ArrayList<Authority>(3);
	}

	/**
	 * Constructor
	 * @param name
	 * @param emailAddress
	 * @param password
	 * @param locked
	 * @param enabled
	 * @param expires
	 * @param authorities
	 */
	public User(String name, String emailAddress, String password, boolean locked,
			boolean enabled, Date expires, ArrayList<Authority> authorities) {
		super();
		this.name = name;
		this.emailAddress = emailAddress;
		this.password = password;
		this.locked = locked;
		this.enabled = enabled;
		this.expires = expires;
		this.authorities = authorities;
	}

	@Override
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@Override
	public String getId() {
		return emailAddress;
	}

	@Override
	public void setId(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUserRefId() {
		return getId();
	}

	@Override
	public User clone() {
		ArrayList<Authority> cauth = authorities == null ? null : new ArrayList<Authority>(authorities.size());
		for(Authority a : authorities) {
			cauth.add(a.clone());
		}

		User cln = new User(name, emailAddress, password, locked, enabled, expires, cauth);
		cloneTimestamping(cln);
		return cln;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.USER;
	}

	@Override
	@Length(max = MAXLEN_NAME)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@NotEmpty
	@Email
	@Length(max = MAXLEN_EMAIL_ADDRESS)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return Returns the password.
	 */
	@NotEmpty
	@Length(max = MAXLEN_PASSWORD)
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the expires
	 */
	@NotNull
	public Date getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(Date expires) {
		this.expires = expires;
	}

	/**
	 * @return the locked
	 */
	@NotNull
	public boolean getLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return authorities
	 */
	@Valid
	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<Authority> authorities) {
		this.authorities = new ArrayList<Authority>(authorities);
	}

	/**
	 * Convenience method checking for presence of a given role for this user.
	 * @param role the role as a string
	 * @return true if this user is "in" the given role, false otherwise.
	 */
	public boolean inRole(String role) {
		if(authorities == null) return false;
		for(final Authority a : authorities) {
			if(a.equals(role)) return true;
		}
		return false;
	}

	public Authority getAuthority(String nme) {
		for(Authority a : authorities) {
			if(a.getName().equals(nme)) return a;
		}
		return null;
	}

	public void addAuthority(Authority authority) {
		authorities.add(authority);
	}

	public void removeAuthority(Authority authority) {
		authorities.remove(authority);
	}

	public int getNumAuthorities() {
		return authorities.size();
	}

	public void setUsername(String username) {
		setEmailAddress(username);
	}

	public boolean isAccountNonExpired() {
		return (new Date()).getTime() < (expires == null ? 0L : expires.getTime());
	}

	public boolean isAccountNonLocked() {
		return !getLocked();
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(getClass() != obj.getClass()) return false;
		User other = (User) obj;
		if(emailAddress == null) {
			if(other.emailAddress != null) return false;
		}
		else if(!emailAddress.equals(other.emailAddress)) return false;
		return true;
	}

}
