/*
 * The Logic Lab
 */
package com.tabulaw.criteria;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.tabulaw.common.model.IEntity;
import com.tabulaw.model.NameKey;
import com.tll.model.bk.IBusinessKey;
import com.tll.util.Comparator;
import com.tll.util.DBType;
import com.tll.util.DateRange;

/**
 * CriterionGroup
 * @author jpk
 */
public class CriterionGroup implements ICriterion, Iterable<ICriterion> {

	private static final long serialVersionUID = 502701212641369513L;

	private boolean junction = true; // default to AND junction

	private Set<ICriterion> group;

	/**
	 * Constructor
	 */
	public CriterionGroup() {
		super();
	}

	/**
	 * Constructor
	 * @param isConjunction
	 */
	public CriterionGroup(boolean isConjunction) {
		this();
		this.junction = isConjunction;
	}

	public boolean isGroup() {
		return true;
	}

	public Set<ICriterion> getGroup() {
		return group;
	}

	public boolean isConjunction() {
		return junction;
	}

	public void setJunction(boolean junction) {
		this.junction = junction;
	}

	private Set<ICriterion> getGroupInternal() {
		if(group == null) {
			group = new HashSet<ICriterion>(3);
		}
		return group;
	}

	/**
	 * Adds a single criterion to this group.
	 * @param ctn The criterion
	 * @return this
	 */
	public CriterionGroup addCriterion(Criterion ctn) {
		getGroupInternal().add(ctn);
		return this;
	}

	/**
	 * Adds a collection of criterion to this group.
	 * @param clc The criterion collection
	 * @return this
	 */
	public CriterionGroup addCriterion(Collection<Criterion> clc) {
		getGroupInternal().addAll(clc);
		return this;
	}

	/**
	 * Adds a criterion to this group with all parameters for a criterion taken in
	 * as method arguments. This is the generalized way to add a criterion.
	 * @param fieldName The field name
	 * @param fieldValue The field value
	 * @param comp The comparator
	 * @param isCaseSensitive Is case sensitive?
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(String fieldName, Object fieldValue, Comparator comp, boolean isCaseSensitive) {
		getGroupInternal().add(new Criterion(fieldName, fieldValue, comp, isCaseSensitive));
		return this;
	}

	/**
	 * Adds an enum criterion to this group.
	 * @param fieldName The field name
	 * @param enm The enum value
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(String fieldName, Enum<?> enm) {
		addCriterion(fieldName, enm, Comparator.EQUALS, true);
		return this;
	}

	/**
	 * Adds a primary key criterion.
	 * @param entityType the entity type
	 * @param pk the primary key
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(Class<? extends IEntity> entityType, Object pk) {
		addCriterion(IEntity.PK_FIELDNAME, pk, Comparator.EQUALS, true);
		return this;
	}

	/**
	 * Adds a business key criterion to this group.
	 * @param key
	 * @param isCaseSensitive
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(IBusinessKey<? extends IEntity> key, boolean isCaseSensitive) {
		for(final String fname : key.getPropertyNames()) {
			addCriterion(fname, key.getPropertyValue(fname), Comparator.EQUALS, isCaseSensitive);
		}
		return this;
	}

	/**
	 * Adds a name key criterion to this group.
	 * @param nameKey
	 * @param isCaseSensitive
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(NameKey<?> nameKey, boolean isCaseSensitive) {
		return addCriterion(nameKey.getNameProperty(), nameKey.getName(), Comparator.EQUALS, isCaseSensitive);
	}

	/**
	 * Adds a Foreign Key criterion to this group.
	 * @param relatedPropertyName The related property name
	 * @param relatedEntityType the related entity type
	 * @param foreignKey The foreign key. If <code>null</code>, a datastore-wise
	 *        NULL identifier will be specified in the created criterion.
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(String relatedPropertyName, Class<? extends IEntity> relatedEntityType,
			Object foreignKey) {
		final String fkname = relatedPropertyName + "." + IEntity.PK_FIELDNAME;
		if(foreignKey == null) {
			return addCriterion(fkname, DBType.NULL, Comparator.IS, false);
		}
		return addCriterion(fkname, foreignKey, Comparator.EQUALS, false);
	}

	/**
	 * Adds a search token based criterion to this group account for possible
	 * wildcards ("%") in the search token argument. If the given search token is
	 * <code>null</code>, the field will be compared for <code>null</code> (i.e.:
	 * "is null")
	 * @param fieldName The field name
	 * @param searchToken The search token
	 * @param isCaseSensitive Is case sensitive?
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(String fieldName, String searchToken, boolean isCaseSensitive) {
		if(searchToken == null) {
			return addCriterion(fieldName, null, Comparator.IS, false);
		}
		return addCriterion(fieldName, searchToken, (searchToken.indexOf('%') < 0) ? Comparator.EQUALS : Comparator.LIKE,
				isCaseSensitive);
	}

	/**
	 * Adds a date range criterion to this group.
	 * @param fieldName The field name
	 * @param dateRange The date range
	 * @return this for method chaining
	 */
	public CriterionGroup addCriterion(String fieldName, DateRange dateRange) {
		return addCriterion(fieldName, dateRange, Comparator.BETWEEN, false);
	}

	/**
	 * Removes a criterion from this group.
	 * @param ctn
	 * @return the removed group
	 */
	public CriterionGroup removeCriterion(Criterion ctn) {
		group.remove(ctn);
		return this;
	}

	public Iterator<ICriterion> iterator() {
		return group == null ? null : group.iterator();
	}

	/**
	 * Removes <em>all</em> criterion in this group.
	 */
	public void clear() {
		if(group != null) {
			group.clear();
		}
	}

	/**
	 * Checks to see if at least on criterion exists and furthermore that
	 * <em>all</em> held criterion are set as well.
	 * @return true/false
	 */
	public boolean isSet() {
		if(group == null || group.size() < 1) {
			return false;
		}
		for(final ICriterion c : group) {
			if(!c.isSet()) {
				return false;
			}
		}
		return true;
	}

	public int size() {
		return group == null ? 0 : group.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + (junction ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final CriterionGroup other = (CriterionGroup) obj;
		if(group == null) {
			if(other.group != null) return false;
		}
		else if(!group.equals(other.group)) return false;
		if(junction != other.junction) return false;
		return true;
	}

}
