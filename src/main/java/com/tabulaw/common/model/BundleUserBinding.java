/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import javax.validation.constraints.NotNull;

import com.tabulaw.schema.BusinessKeyDef;
import com.tabulaw.schema.BusinessObject;

/**
 * Associates a {@link QuoteBundle} to a {@link User}.
 * <p>
 * NOTE: No primary surrogate key is needed here.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Quote Bundle User Binding", properties = {
	"bundleId", "userId"
}))
public class BundleUserBinding extends EntityBase {

	private static final long serialVersionUID = -4676769373977438262L;

	private String bundleId, userId;

	/**
	 * Constructor
	 */
	public BundleUserBinding() {
		super();
	}

	/**
	 * Constructor
	 * @param bundleId
	 * @param userId
	 */
	public BundleUserBinding(String bundleId, String userId) {
		super();
		this.bundleId = bundleId;
		this.userId = userId;
	}

	@Override
	public IEntity clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.BUNDLE_USER_BINDING;
	}

	@Override
	public String getId() {
		return bundleId + '|' + userId;
	}

	@Override
	public void setId(String id) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	@NotNull
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bundleId == null) ? 0 : bundleId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(getClass() != obj.getClass()) return false;
		BundleUserBinding other = (BundleUserBinding) obj;
		if(bundleId == null) {
			if(other.bundleId != null) return false;
		}
		else if(!bundleId.equals(other.bundleId)) return false;
		if(userId == null) {
			if(other.userId != null) return false;
		}
		else if(!userId.equals(other.userId)) return false;
		return true;
	}
}
