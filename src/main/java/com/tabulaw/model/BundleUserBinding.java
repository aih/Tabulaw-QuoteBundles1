/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import javax.validation.constraints.NotNull;

/**
 * Associates a {@link QuoteBundle} to a {@link User}.
 * <p>
 * NOTE: No primary surrogate key is needed here.
 * @author jpk
 */
public class BundleUserBinding extends EntityBase {

	private static final long serialVersionUID = -4676769373977438262L;

	private String id, bundleId, userId;

	private boolean orphaned;
	
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
	 * @param orphaned Is this bundle for orphaned quotes?
	 */
	public BundleUserBinding(String bundleId, String userId, boolean orphaned) {
		super();
		this.bundleId = bundleId;
		this.userId = userId;
		this.orphaned = orphaned;
	}

	/**
	 * Is this a binding to an orphaned quote?
	 * @return true/false
	 */
	public boolean isOrphaned() {
		return orphaned;
	}

	public void setOrphaned(boolean orphaned) {
		this.orphaned = orphaned;
	}

	@Override
	protected void doClone(IEntity cln) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected IEntity newInstance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityType() {
		return EntityType.BUNDLE_USER_BINDING.name();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@NotNull
	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		if(bundleId == null) throw new NullPointerException();
		this.bundleId = bundleId;
	}

	@NotNull
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		if(userId == null) throw new NullPointerException();
		this.userId = userId;
	}

	/*
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
	*/
}
