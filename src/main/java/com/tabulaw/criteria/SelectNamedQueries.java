/**
 * The Logic Lab
 * @author jpk
 * Apr 30, 2008
 */
package com.tabulaw.criteria;

import com.tabulaw.common.model.User;
import com.tll.criteria.ISelectNamedQueryDef;

/**
 * Enumeration of the the defined named queries in the application.
 * @author jpk
 */
public enum SelectNamedQueries implements ISelectNamedQueryDef {
	USER_LISTING("user.list", User.class, true, true);

	private final String queryName;
	private final Class<?> entityType;
	private final boolean scalar;
	private final boolean supportsPaging;

	private SelectNamedQueries(String queryName, Class<?> entityType, boolean scalar, boolean supportsPaging) {
		this.queryName = queryName;
		this.entityType = entityType;
		this.scalar = scalar;
		this.supportsPaging = supportsPaging;
	}

	public String getQueryName() {
		return queryName;
	}

	public Class<?> getEntityType() {
		return entityType;
	}

	public boolean isScalar() {
		return scalar;
	}

	public boolean isSupportsPaging() {
		return supportsPaging;
	}

	@Override
	public String toString() {
		return queryName;
	}
}