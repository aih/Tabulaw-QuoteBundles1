package com.tabulaw.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.GrantedAuthority;

import com.tll.IMarshalable;
import com.tll.model.EntityBase;
import com.tll.model.IEntity;
import com.tll.model.INamedEntity;
import com.tll.schema.BusinessKeyDef;
import com.tll.schema.BusinessObject;

/**
 * Implementation of Acegi's
 * {@link org.springframework.security.GrantedAuthority} interface.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Authority", properties = { Authority.FIELDNAME_AUTHORITY }))
public class Authority extends EntityBase implements INamedEntity, GrantedAuthority {

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

	public Class<? extends IEntity> entityClass() {
		return Authority.class;
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
	public int compareTo(Object o) {
		if(o != null && o instanceof GrantedAuthority) {
			final String rhsRole = ((GrantedAuthority) o).getAuthority();
			if(rhsRole == null) {
				return -1;
			}
			return authority.compareTo(rhsRole);
		}
		return -1;
	}

	@Override
	public String descriptor() {
		return getAuthority();
	}
}
