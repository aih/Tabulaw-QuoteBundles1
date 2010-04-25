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

	/**
	 * Constructor
	 */
	public UserState() {
		super();
	}

	/**
	 * Constructor
	 * @param userId
	 */
	public UserState(String userId) {
		super();
		setUserId(userId);
	}

	@Override
	protected IEntity newInstance() {
		return new UserState();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);

		UserState us = (UserState) cln;
		us.userId = userId;
		us.currentQuoteBundleId = currentQuoteBundleId;
	}

	@Override
	public String getEntityType() {
		return EntityType.USER_STATE.name();
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
		if(userId == null) throw new NullPointerException();
		this.userId = userId;
	}

	public String getCurrentQuoteBundleId() {
		return currentQuoteBundleId;
	}

	/**
	 * Sets the current quote bundle id.
	 * @param bundleId
	 * @return <code>true</code> if the current quote bundle id value actually
	 *         changed
	 */
	public boolean setCurrentQuoteBundleId(String bundleId) {
		if(bundleId == null) throw new NullPointerException();
		if(bundleId.equals(currentQuoteBundleId)) return false;
		this.currentQuoteBundleId = bundleId;
		return true;
	}
}