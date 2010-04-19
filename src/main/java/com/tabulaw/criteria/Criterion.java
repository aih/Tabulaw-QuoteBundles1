package com.tabulaw.criteria;

import com.tabulaw.util.Comparator;

/**
 * Object representing a criterion definition. It contains the field name and
 * value as well as the comparator. Any other fields that can be used to
 * describe a criterion should be added here.
 * @author jpk
 */
public class Criterion implements ICriterion {

	private static final long serialVersionUID = -9033958462037763702L;

	private String field;

	private Object value;

	private Comparator comparator;

	private boolean caseSensitive = true; // true by default

	public Criterion() {
		this(null, null);
	}

	public Criterion(String field, Object value) {
		this(field, value, Comparator.EQUALS, true);
	}

	public Criterion(String field, Object value, Comparator comparator, boolean caseSensitive) {
		super();
		setFieldValueComparator(field, value, comparator);
		setCaseSensitive(caseSensitive);
	}

	public boolean isGroup() {
		return false;
	}

	/**
	 * Returns true if this criterion is case sensitive. False otherwise. Certain
	 * subclasses will never be case sensitive, such as a boolean criterion and
	 * can override this method to always return false.
	 * @return true if this criterion is case sensitive, false otherwise.
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Returns the comparator that should be used in the query as the relationship
	 * between the field and the value.
	 * @return the comparator enum
	 */
	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * Returns the name of the field used by the persistence framework.
	 * @return the name of the field
	 * @see #getPropertyName()
	 */
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Returns the property name used by the UI for this criterion. In most cases,
	 * the property name will be equivalent to the field name. However, in the
	 * case of foreign key fields, the field value will include the ".id" whereas
	 * the property name will be only the reference entity name.
	 * @return the property name
	 */
	public String getPropertyName() {
		return this.field;
	}

	/**
	 * Returns the value for the field that should be applied to the query.
	 * @return the value to used in the query
	 */
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setFieldValue(String field, Object value) {
		setField(field);
		setValue(value);
	}

	public void setFieldValueComparator(String field, Object value, Comparator comp) {
		setFieldValue(field, value);
		setComparator(comp);
	}

	public boolean isSet() {
		if(Comparator.LIKE.equals(getComparator()) && "%".equals(getValue())) {
			// if we are doing a like query and the value is just %, then ignore this
			// criterion
			return false;
		}
		return (getValue() != null);
	}

	public void clear() {
		setFieldValue(null, null);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final Criterion other = (Criterion) obj;
		if(caseSensitive != other.caseSensitive) return false;
		if(comparator == null) {
			if(other.comparator != null) return false;
		}
		else if(!comparator.equals(other.comparator)) return false;
		if(field == null) {
			if(other.field != null) return false;
		}
		else if(!field.equals(other.field)) return false;
		if(value == null) {
			if(other.value != null) return false;
		}
		else if(!value.equals(other.value)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (caseSensitive ? 1231 : 1237);
		result = prime * result + ((comparator == null) ? 0 : comparator.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Fld:" + field + "|Val:" + value + "|Cmp:" + comparator + "|CaseSens:" + caseSensitive;
	}
}
