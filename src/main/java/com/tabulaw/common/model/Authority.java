package com.tabulaw.common.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.tabulaw.IMarshalable;
import com.tabulaw.schema.BusinessKeyDef;
import com.tabulaw.schema.BusinessObject;

/**
 * A defined user role called authority.
 * <p>
 * NOTE: The authority property is the id.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Authority", properties = { Authority.FIELDNAME_AUTHORITY
}))
public class Authority extends EntityBase implements INamedEntity, Comparable<Authority> {

	/**
	 * AuthorityRoles
	 * @author jpk
	 */
	public static enum AuthorityRoles implements IMarshalable {
		ROLE_ADMINISTRATOR,
		ROLE_USER,
		ROLE_ANONYMOUS;
	}

	static final long serialVersionUID = -4601781277584062384L;

	public static final String FIELDNAME_AUTHORITY = "authority";

	public static final int MAXLEN_AUTHORITY = 50;

	/**
	 * I.e. the role.
	 */
	private String authority;

	/**
	 * Constructor
	 */
	public Authority() {
		super();
	}

	/**
	 * Constructor
	 * @param authority
	 */
	public Authority(String authority) {
		super();
		setAuthority(authority);
	}

	@Override
	public Authority clone() {
		return new Authority(authority);
	}

	@Override
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.AUTHORITY;
	}

	@Override
	public String getId() {
		return getAuthority();
	}

	@Override
	public void setId(String id) {
		// the authority id the id
		throw new UnsupportedOperationException();
	}

	@NotEmpty
	@Length(max = MAXLEN_AUTHORITY)
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getName() {
		return getAuthority();
	}

	public void setName(String name) {
		setAuthority(name);
	}

	@Override
	public int compareTo(Authority o) {
		return authority.compareTo(o.authority);
	}

	@Override
	public String descriptor() {
		return getAuthority();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(getClass() != obj.getClass()) return false;
		Authority other = (Authority) obj;
		if(authority == null) {
			if(other.authority != null) return false;
		}
		else if(!authority.equals(other.authority)) return false;
		return true;
	}

	@Override
	public Object getPropertyValue(String propertyPath) {
		if(FIELDNAME_AUTHORITY.equals(propertyPath)) return getAuthority();
		return null;
	}
}
