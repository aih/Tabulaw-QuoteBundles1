/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Jan 21, 2009
 */
package com.tabulaw.criteria;

/**
 * ISelectNamedQueryDef - Programmatic counterpart to entity and scalar select
 * queries declared in the persistence context (orm.xml).
 * @author jpk
 */
public interface ISelectNamedQueryDef {

	/**
	 * @return the entityType
	 */
	Class<?> getEntityType();

	/**
	 * @return The name of the named query.
	 */
	String getQueryName();

	/**
	 * Is the query scalar (or entity based)?
	 * @return true/false
	 */
	boolean isScalar();

	/**
	 * Does the query support recordset-based paging?
	 * <p>
	 * If <code>true</code>, a couterpart count query is expected to be declared
	 * for this base query name having the following naming convention: <br>
	 * <code>
	 * {baseQueryName}.count
	 * </code>
	 * @return true/false
	 */
	boolean isSupportsPaging();
}
