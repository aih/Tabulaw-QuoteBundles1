/**
 * The Logic Lab
 * @author jpk
 * @since Apr 23, 2010
 */
package com.tabulaw.common.model;

import javax.validation.constraints.NotNull;

/**
 * @author jpk
 */
public class UserState extends EntityBase {

	private static final long serialVersionUID = -8340803996969639170L;

	private String userId;

	private String currentQuoteBundleId;

	@Override
	public IEntity clone() {
		UserState cln = new UserState();
		cln.userId = userId;
		return cln;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.USER_STATE;
	}

	@Override
	public String getId() {
		return getUserId();
	}

	@Override
	public void setId(String id) {
		setUserId(id);
	}

	@NotNull
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCurrentQuoteBundleId() {
		return currentQuoteBundleId;
	}

	public void setCurrentQuoteBundleId(String bundleId) {
		this.currentQuoteBundleId = bundleId;
	}
}
