/*
 * Created on Jan 1, 2005
 */
package com.tabulaw.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tabulaw.IMarshalable;
import com.tabulaw.model.bk.BusinessKeyDef;
import com.tabulaw.model.bk.BusinessObject;

/**
 * The user entity. NOTE: no surrogate primary key is needed here.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Email Address", properties = { "emailAddress" }))
@XmlRootElement(name = "user")
public class User extends TimeStampEntity implements IUserRef, INamedEntity, Comparable<User> {

	/**
	 * Role
	 * @author jpk
	 */
	public static enum Role implements IMarshalable {
		ADMINISTRATOR,
		USER,
		ANONYMOUS;
	}

	private static final long serialVersionUID = -6126885590318834318L;

	public static final int MAXLEN_NAME = 50;
	public static final int MAXLEN_EMAIL_ADDRESS = 128;
	public static final int MAXLEN_PASSWORD = 255;

	public static final String SUPERUSER = "Tabulaw Administrator";

	private String name;

	private String emailAddress;

	private String password;

	private boolean locked = false;

	private boolean enabled = true;

	private Date expires;

	private ArrayList<Role> roles;

	private HashSet<AppFeature> appFeatures;

	private HashMap<String,String> oauthParameters;

	private HashMap<String,String> oauthParametersExtra;
	
	/**
	 * Constructor
	 */
	public User() {
		super();
	}

	public boolean isSuperuser() {
		return SUPERUSER.equals(name);
	}

	public boolean isAdministrator() {
		return inRole(Role.ADMINISTRATOR);
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
	protected IEntity newInstance() {
		return new User();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);
		User u = (User) cln;

		ArrayList<Role> croles = roles == null ? null : new ArrayList<Role>(roles);

		u.name = name;
		u.emailAddress = emailAddress;
		u.password = password;
		u.locked = locked;
		u.enabled = enabled;
		u.expires = expires;
		u.roles = croles;
	}

	@Override
	public String getEntityType() {
		return EntityType.USER.name();
	}

	@NotEmpty
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
		if(emailAddress == null) throw new NullPointerException();
		this.emailAddress = emailAddress;
	}

	/**
	 * @return Returns the password.
	 */
	@NotEmpty
	@Length(max = MAXLEN_PASSWORD)
	@XmlTransient
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
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return roles
	 */
	@Valid
	public List<Role> getRoles() {
		if(roles == null) return Collections.emptyList();
		return new ArrayList<Role>(roles);
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles == null ? null : new ArrayList<Role>(roles);
	}

	/**
	 * Convenience method checking for presence of a given role for this user.
	 * @param role the role as a string
	 * @return true if this user is "in" the given role, false otherwise.
	 */
	public boolean inRole(Role role) {
		if(roles == null) return false;
		for(final Role a : roles) {
			if(a == role) return true;
		}
		return false;
	}

	public void addRole(Role role) {
		if(roles == null) roles = new ArrayList<Role>(2);
		roles.add(role);
	}

	public void removeRole(Role role) {
		if(roles != null) roles.remove(role);
	}

	public int getNumRoles() {
		return roles == null ? 0 : roles.size();
	}

	public void setUsername(String username) {
		setEmailAddress(username);
	}

	public boolean isExpired() {
		return !((new Date()).getTime() < (expires == null ? 0L : expires.getTime()));
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return list of app features to which the user has privilege.
	 */
	public Set<AppFeature> getAppFeatures() {
		return appFeatures;
	}

	public void setAppFeatures(Collection<AppFeature> appFeatures) {
		if(this.appFeatures == null) {
			this.appFeatures = new HashSet<AppFeature>();
		}
		this.appFeatures.addAll(appFeatures);
	}
	
	public void addAppFeature(AppFeature f) {
		if(this.appFeatures == null) {
			this.appFeatures = new HashSet<AppFeature>();
		}
		appFeatures.add(f);
	}
	
	public boolean hasAppFeature(AppFeature f) {
		return appFeatures == null ? false : appFeatures.contains(f);
	}
	
	public boolean removeAppFeature(AppFeature f) {
		if(appFeatures == null) return false;
		return appFeatures.remove(f);
	}

	@Override
	public int compareTo(User o) {
		return name != null && o.name != null ? name.compareTo(o.name) : 0;
	}

	@Override
	public Object getPropertyValue(String propertyPath) {
		if("name".equals(propertyPath)) {
			return getName();
		}
		if("dateCreated".equals(propertyPath)) {
			return getDateCreated();
		}
		if("dateModified".equals(propertyPath)) {
			return getDateModified();
		}
		else if("emailAddress".equals(propertyPath)) {
			return getEmailAddress();
		}
		else if("locked".equals(propertyPath)) {
			return isLocked();
		}
		else if("enabled".equals(propertyPath)) {
			return isEnabled();
		}
		else if("expires".equals(propertyPath)) {
			return getExpires();
		}
		else if("roles".equals(propertyPath)) {
			StringBuilder sb = new StringBuilder();
			for(Role a : getRoles()) {
				sb.append(",");
				sb.append(a.name());
			}
			return sb.length() > 0 ? sb.substring(1) : "";
		}

		return null;
	}

	public void setOAuthParameters(HashMap<String,String> oauthParameters) {
		this.oauthParameters = oauthParameters;
	}

	public Map<String,String> getOAuthParameters() {
		return oauthParameters;
	}

	public void setOAuthParametersExtra(HashMap<String,String> oauthParametersExtra) {
		this.oauthParametersExtra = oauthParametersExtra;
	}

	public Map<String,String> getOAuthParametersExtra() {
		return oauthParametersExtra;
	}
}
