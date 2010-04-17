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

	private Long bundleId, userId;

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
	public BundleUserBinding(Long bundleId, Long userId) {
		super();
		this.bundleId = bundleId;
		this.userId = userId;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.BUNDLE_USER_BINDING;
	}

	@NotNull
	public Long getBundleId() {
		return bundleId;
	}

	public void setBundleId(Long bundleId) {
		this.bundleId = bundleId;
	}

	@NotNull
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
