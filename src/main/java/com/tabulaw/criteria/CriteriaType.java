/**
 * The Logic Lab
 * @author jpk
 * Mar 6, 2008
 */
package com.tabulaw.criteria;

/**
 * CriteriaType - Defines the supported criteria types.
 * @author jpk
 */
public enum CriteriaType {
	ENTITY,
	ENTITY_NAMED_QUERY,
	SCALAR_NAMED_QUERY;

	/**
	 * @return <code>true</code> when this criteria type represents scalar
	 *         results.
	 */
	public boolean isScalar() {
		return this == SCALAR_NAMED_QUERY;
	}

	/**
	 * @return <code>true</code> when this criteria will translate to a jpa
	 *         handlable query Object.
	 */
	public boolean isQuery() {
		return this == ENTITY_NAMED_QUERY || this == SCALAR_NAMED_QUERY;
	}

}
