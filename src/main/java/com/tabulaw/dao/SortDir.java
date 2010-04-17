package com.tabulaw.dao;

import com.tll.IMarshalable;
import com.tll.INameValueProvider;

/**
 * Sort direction enumeration.
 * @author jpk
 */
public enum SortDir implements INameValueProvider<String>, IMarshalable {
	ASC("Ascending", "asc", "ascending"),
	DESC("Descending", "desc", "descending");

	private final String name;
	private final String sqlClause;
	private final String jdoqlClause;

	SortDir(String name, String sqlclause, String jdoqlClause) {
		this.name = name;
		this.sqlClause = sqlclause;
		this.jdoqlClause = jdoqlClause;
	}

	public String getName() {
		return name;
	}

	public String getSqlClause() {
		return sqlClause;
	}

	public String getJdoqlClause() {
		return jdoqlClause;
	}

	public String getValue() {
		return toString();
	}

	@Override
	public String toString() {
		return name();
	}
}
