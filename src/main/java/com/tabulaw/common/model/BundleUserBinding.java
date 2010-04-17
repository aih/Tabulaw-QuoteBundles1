/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import javax.validation.constraints.NotNull;

import com.tll.schema.BusinessKeyDef;
import com.tll.schema.BusinessObject;

/**
 * Associates a {@link QuoteBundle} to a {@link User}.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Quote Bundle User Binding", properties = { "bundleId", "userId" }))
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
	protected String getId() {
		return Integer.toString(bundleId.hashCode()) + '|' + Integer.toString(bundleId.hashCode());
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
}
