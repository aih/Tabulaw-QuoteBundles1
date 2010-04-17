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

import com.tll.schema.BusinessKeyDef;
import com.tll.schema.BusinessObject;

/**
 * The user entity.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Email Address", properties = { "emailAddress"
}))
public class User extends TimeStampEntity implements IUserRef {

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

	@Override
	public EntityType getEntityType() {
		return EntityType.USER;
	}

	@Length(max = MAXLEN_NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotEmpty
	@Email
	@Length(max = MAXLEN_EMAIL_ADDRESS)
	@Override
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

	@Override
	public String getUsername() {
		return getEmailAddress();
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
}
