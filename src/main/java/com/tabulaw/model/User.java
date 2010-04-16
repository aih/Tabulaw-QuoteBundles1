/*
 * Created on Jan 1, 2005
 */
package com.tabulaw.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import com.tll.model.IEntity;
import com.tll.model.IUserRef;
import com.tll.model.NamedTimeStampEntity;
import com.tll.schema.BusinessKeyDef;
import com.tll.schema.BusinessObject;

/**
 * The user entity.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Email Address", properties = { "emailAddress" }))
public class User extends NamedTimeStampEntity implements UserDetails, IUserRef {

	private static final long serialVersionUID = -6126885590318834318L;

	public static final int MAXLEN_NAME = 50;
	public static final int MAXLEN_EMAIL_ADDRESS = 128;
	public static final int MAXLEN_PASSWORD = 255;

	public static final String SUPERUSER = "jpk";

	private String emailAddress;

	private String password;

	private boolean locked = true;

	private boolean enabled = true;

	private Date expires;

	private Set<Authority> authorities = new LinkedHashSet<Authority>(3);

	public Class<? extends IEntity> entityClass() {
		return User.class;
	}

	@Length(max = MAXLEN_NAME)
	@Override
	public String getName() {
		return name;
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
	public Set<Authority> getAuthoritys() {
		return authorities;
	}

	/**
	 * Convenience method checking for presence of a given role for this user.
	 * @param role the role as a string
	 * @return true if this user is "in" the given role, false otherwise.
	 */
	public boolean inRole(String role) {
		final Set<Authority> as = getAuthoritys();
		if(as == null) return false;
		for(final Authority a : as) {
			if(a.equals(role)) return true;
		}
		return false;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthoritys(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Authority getAuthority(Object pk) {
		return findEntityInCollection(authorities, pk);
	}

	public Authority getAuthority(String nme) {
		return findNamedEntityInCollection(authorities, nme);
	}

	public void addAuthority(Authority authority) {
		addEntityToCollection(authorities, authority);
	}

	public void addAuthorities(Collection<Authority> clc) {
		addEntitiesToCollection(clc, authorities);
	}

	public void removeAuthority(Authority authority) {
		removeEntityFromCollection(authorities, authority);
	}

	public void removeAuthorities(Collection<Authority> clc) {
		clearEntityCollection(authorities);
	}

	public int getNumAuthorities() {
		return getCollectionSize(authorities);
	}

	/**
	 * Acegi implementation must not return null
	 * @return array of granted authorities
	 */
	public GrantedAuthority[] getAuthorities() {
		return authorities == null ? new Authority[0] : authorities.toArray(new Authority[authorities.size()]);
	}

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
