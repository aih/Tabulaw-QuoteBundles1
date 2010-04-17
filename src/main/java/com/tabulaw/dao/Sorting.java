package com.tabulaw.dao;

import com.tll.IMarshalable;

/**
 * Construct representing a hierarchical sorting directive.
 * @author jpk
 */
public class Sorting implements IMarshalable {

	static final long serialVersionUID = 4943672060530931835L;

	/**
	 * The sort columns in order or priority.
	 */
	private SortColumn[] columns;

	public Sorting() {
		super();
	}

	/**
	 * Constructor
	 * @param primaryPropertyName
	 */
	public Sorting(String primaryPropertyName) {
		this(new SortColumn(primaryPropertyName, (String) null));
	}

	/**
	 * Constructor
	 * @param primaryPropertyName
	 * @param parentAlias
	 */
	public Sorting(String primaryPropertyName, String parentAlias) {
		this(new SortColumn(primaryPropertyName, parentAlias));
	}

	/**
	 * Constructor
	 * @param primaryPropertyName
	 * @param direction
	 * @param ignoreCase
	 */
	public Sorting(String primaryPropertyName, SortDir direction, Boolean ignoreCase) {
		this(new SortColumn(primaryPropertyName, direction, ignoreCase));
	}

	/**
	 * Constructor
	 * @param primaryPropertyName
	 * @param parentAlias
	 * @param direction
	 * @param ignoreCase
	 */
	public Sorting(String primaryPropertyName, String parentAlias, SortDir direction, Boolean ignoreCase) {
		this(new SortColumn(primaryPropertyName, parentAlias, direction, ignoreCase));
	}

	/**
	 * Constructor
	 * @param columns
	 */
	public Sorting(SortColumn[] columns) {
		this();
		setColumns(columns);
	}

	/**
	 * Constructor
	 * @param sortColumn
	 */
	public Sorting(SortColumn sortColumn) {
		super();
		setPrimarySortColumn(sortColumn);
	}

	public int size() {
		return columns == null ? 0 : columns.length;
	}

	public void setColumns(SortColumn[] columns) {
		if(columns == null || columns.length < 1)
			throw new IllegalArgumentException("Unable to set sorting columns: null or empty columns array specified");
		this.columns = columns;
	}

	public SortColumn[] getColumns() {
		return columns;
	}

	private SortColumn[] safeGetColumns() {
		if(columns == null) {
			columns = new SortColumn[] { new SortColumn() };
		}
		return columns;
	}

	public SortColumn getPrimarySortColumn() {
		return safeGetColumns()[0];
	}

	public void setPrimarySortColumn(SortColumn column) {
		safeGetColumns()[0] = column;
	}

	@Override
	public final boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || obj instanceof Sorting == false) return false;
		final Sorting that = (Sorting) obj;
		if(this.size() != that.size() || this.size() < 1) return false;
		for(int i = 0; i < this.size(); i++) {
			if(!this.columns[i].equals(that.columns[i])) return false;
		}
		return true;
	}

	@Override
	public final int hashCode() {
		int hash = 1;
		if(columns != null && columns.length > 0) {
			for(final SortColumn element : columns) {
				hash += element.hashCode();
			}
		}
		return hash;
	}

	/**
	 * @return SQL compliant <code>ORDER BY</code> clause.
	 */
	public String getSqlOrderByClause() {
		if(size() < 1) return "";
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < columns.length; i++) {
			sb.append(columns[i].getSqlOrderByClause());
			if(i < columns.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	/**
	 * @return JDO2 compliant <em>ordering</em> clause
	 */
	public String getJdoOrderingClause() {
		if(size() < 1) return "";
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < columns.length; i++) {
			sb.append(columns[i].getJdoOrderingClause());
			if(i < columns.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	/**
	 * @return SQL valid order by clause.
	 * @see SortColumn#toString()
	 */
	@Override
	public String toString() {
		return getSqlOrderByClause();
	}
}
