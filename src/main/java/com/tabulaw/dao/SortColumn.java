package com.tabulaw.dao;

import java.io.Serializable;

import com.tabulaw.IMarshalable;
import com.tabulaw.schema.IPropertyNameProvider;

/**
 * Represents a sort directive for a single "column".
 * @author jpk
 */
public final class SortColumn implements Serializable, IMarshalable, IPropertyNameProvider {

	private static final long serialVersionUID = -4966927388892147102L;

	public static final Boolean DEFAULT_IGNORE_CASE = Boolean.TRUE;

	public static final SortDir DEFAULT_SORT_DIR = SortDir.ASC;

	/**
	 * The property name
	 */
	private String propertyName;

	/**
	 * The parent alias name used to resolve the actual sorting column.
	 */
	private String parentAlias;

	/**
	 * The sort direction: ascending or descending
	 */
	private SortDir direction = DEFAULT_SORT_DIR;

	/**
	 * Ignore upper/lower case when column is text-based?
	 */
	private Boolean ignoreCase = DEFAULT_IGNORE_CASE;

	/**
	 * Constructor
	 */
	public SortColumn() {
		super();
	}

	/**
	 * Constructor
	 * @param propertyName
	 */
	public SortColumn(String propertyName) {
		this(propertyName, DEFAULT_SORT_DIR, DEFAULT_IGNORE_CASE);
	}

	/**
	 * Constructor
	 * @param propertyName
	 * @param parentAlias
	 */
	public SortColumn(String propertyName, String parentAlias) {
		this(propertyName, parentAlias, DEFAULT_SORT_DIR, DEFAULT_IGNORE_CASE);
	}

	/**
	 * Constructor
	 * @param propertyName
	 * @param direction
	 */
	public SortColumn(String propertyName, SortDir direction) {
		this(propertyName, null, direction, DEFAULT_IGNORE_CASE);
	}

	/**
	 * Constructor
	 * @param propertyName
	 * @param parentAlias
	 * @param direction
	 */
	public SortColumn(String propertyName, String parentAlias, SortDir direction) {
		this(propertyName, parentAlias, direction, DEFAULT_IGNORE_CASE);
	}

	/**
	 * Constructor
	 * @param propertyName
	 * @param direction
	 * @param ignoreCase
	 */
	public SortColumn(String propertyName, SortDir direction, Boolean ignoreCase) {
		this(propertyName, null, direction, ignoreCase);
	}

	/**
	 * Constructor
	 * @param propertyName
	 * @param parentAlias
	 * @param direction
	 * @param ignoreCase
	 */
	public SortColumn(String propertyName, String parentAlias, SortDir direction, Boolean ignoreCase) {
		this();
		setPropertyName(propertyName);
		setParentAlias(parentAlias);
		setDirection(direction);
		setIgnoreCase(ignoreCase);
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public SortDir getDirection() {
		return direction;
	}

	public void setDirection(SortDir direction) {
		this.direction = direction;
	}

	public boolean isAscending() {
		return SortDir.ASC == direction;
	}

	public boolean isDescending() {
		return SortDir.DESC == direction;
	}

	public Boolean getIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
	 * The parent alias in the query string. This may be <code>null</code> and is
	 * needed when more than one alias is used in a query. With such a query, this
	 * property resolves the column. <br>
	 * Example, in the following query, the parent alias for the name column is
	 * "intf":
	 * 
	 * <pre>
	 *   select
	 * 	   intf.id as id,
	 * 	   intf.name as name,
	 * 	   intf.code as code,
	 * 	   intf.description as description,
	 * 	   intf.dateModified as dateModified,
	 * 	   count(options) as numOptions
	 * 	 from
	 * 	   Interface intf
	 * 	   join intf.options options
	 * 	 group by intf.name
	 * 	 order by intf.name
	 * </pre>
	 * @return The parent alias name.
	 */
	protected String getParentAlias() {
		return parentAlias;
	}

	/**
	 * @param parentAlias the parent alias name to set to properly resolve the
	 *        sort column in a query string.
	 */
	protected void setParentAlias(String parentAlias) {
		this.parentAlias = parentAlias;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final SortColumn other = (SortColumn) obj;
		if(direction == null) {
			if(other.direction != null) return false;
		}
		else if(!direction.equals(other.direction)) return false;
		if(ignoreCase == null) {
			if(other.ignoreCase != null) return false;
		}
		else if(!ignoreCase.equals(other.ignoreCase)) return false;
		if(parentAlias == null) {
			if(other.parentAlias != null) return false;
		}
		else if(!parentAlias.equals(other.parentAlias)) return false;
		if(propertyName == null) {
			if(other.propertyName != null) return false;
		}
		else if(!propertyName.equals(other.propertyName)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((ignoreCase == null) ? 0 : ignoreCase.hashCode());
		result = prime * result + ((parentAlias == null) ? 0 : parentAlias.hashCode());
		result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
		return result;
	}

	/**
	 * @return SQL valid order by clause. <br>
	 *         NOTE: Does NOT account for the ignoreCase flag.
	 * @see #getSqlOrderByClause()
	 */
	@Override
	public String toString() {
		return getSqlOrderByClause();
	}

	/**
	 * @return The SQL compliant <code>ORDER BY</code> clause. <br>
	 *         NOTE: Does NOT account for the <code>ignoreCase</code> flag.
	 */
	public String getSqlOrderByClause() {
		return (parentAlias == null ? "" : parentAlias + ".") + propertyName + " " + getDirection().getSqlClause();
	}

	/**
	 * @return The JDO2 compliant ordering clause <br>
	 *         NOTE: Does NOT account for the ignoreCase flag. <br>
	 *         NOTE: Ignores <code>parentAlias</code> (for now).
	 */
	public String getJdoOrderingClause() {
		return propertyName + " " + getDirection().getJdoqlClause();
	}
}
