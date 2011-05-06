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
public class BundleUserBinding extends EntityBase  implements Comparable<BundleUserBinding> {

	private static final long serialVersionUID = -4676769373977438262L;

	private String id, bundleId;
	private User user;

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
	public BundleUserBinding(String bundleId, User user) {
		super();
		this.bundleId = bundleId;
		this.user = user;
	}

	@Override
	protected void doClone(IEntity cln) {
		BundleUserBinding bub = (BundleUserBinding)cln;
		bub.bundleId = this.bundleId;
		bub.id = this.id;
		User cuser = (User)this.user.clone();
		bub.user = cuser; 
	}

	@Override
	protected IEntity newInstance() {
		return new BundleUserBinding();
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
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		if(user == null) throw new NullPointerException();
		this.user = user;
	}

	@Override
	public int compareTo(BundleUserBinding o) {
		// TODO Any criteria to compare permissions?
		return 0;
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
