package com.tabulaw.criteria;

import com.tabulaw.model.IEntity;

/**
 * Criteria - Holds fairly versatile criterion for querying entity data through
 * the dao api.
 * @param <E> entity type
 * @author jpk
 */
public final class Criteria<E extends IEntity> {

	private static final long serialVersionUID = 4274638102260498756L;

	private CriteriaType criteriaType;

	private Class<E> entityClass;

	private ISelectNamedQueryDef namedQueryDefinition;

	private final CriterionGroup primaryGroup = new CriterionGroup();

	/**
	 * Constructor
	 */
	public Criteria() {
		super();
	}

	/**
	 * Constructor - Use this constructor for {@link CriteriaType#ENTITY} type
	 * criteria.
	 * @param entityClass May NOT be <code>null</code>.
	 */
	public Criteria(Class<E> entityClass) {
		super();
		this.criteriaType = CriteriaType.ENTITY;
		assert entityClass != null;
		this.entityClass = entityClass;
	}

	/**
	 * Constructor - Use this constructor for criteria pointing to a named query.
	 * @param namedQueryDefinition The named query definition
	 */
	@SuppressWarnings("unchecked")
	public Criteria(ISelectNamedQueryDef namedQueryDefinition) {
		super();
		this.criteriaType =
			namedQueryDefinition.isScalar() ? CriteriaType.SCALAR_NAMED_QUERY : CriteriaType.ENTITY_NAMED_QUERY;
		this.entityClass = (Class<E>) namedQueryDefinition.getEntityType();
		this.namedQueryDefinition = namedQueryDefinition;
	}

	public CriteriaType getCriteriaType() {
		return criteriaType;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public ISelectNamedQueryDef getNamedQueryDefinition() {
		return namedQueryDefinition;
	}

	public boolean isSet() {
		return criteriaType.isQuery() ? (namedQueryDefinition != null) : primaryGroup.isSet();
	}

	public CriterionGroup getPrimaryGroup() {
		return primaryGroup;
	}

	public void clear() {
		if(primaryGroup != null) {
			primaryGroup.clear();
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("Type: ");
		sb.append(criteriaType);
		if(entityClass != null) {
			sb.append(" (");
			sb.append(entityClass);
			if(criteriaType.isQuery()) {
				sb.append(", base query name: ");
				sb.append(namedQueryDefinition.getQueryName());
			}
			sb.append(")");
		}
		sb.append(" Criteria");
		sb.append(" (Set: ");
		sb.append(isSet() ? "Yes)" : "No)");
		return sb.toString();
	}

}
