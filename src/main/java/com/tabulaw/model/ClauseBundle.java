/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Collection of clauses.
 * @author jpk
 */
public class ClauseBundle extends EntityBase implements INamedEntity, Comparable<ClauseBundle> {

	private static final long serialVersionUID = 4667367329835058029L;

	private String id;

	private String name, description;

	private List<ClauseDef> clauses;

	/**
	 * Constructor
	 */
	public ClauseBundle() {
		super();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if(id == null) throw new NullPointerException();
		this.id = id;
	}

	@Override
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@Override
	protected IEntity newInstance() {
		return new ClauseBundle();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);
		ClauseBundle qb = (ClauseBundle) cln;

		ArrayList<ClauseDef> clnList = null;
		if(clauses != null) {
			clnList = new ArrayList<ClauseDef>(clauses.size());
			for(ClauseDef q : clauses) {
				clnList.add((ClauseDef) q.clone());
			}
		}

		qb.id = id;
		qb.name = name;
		qb.description = description;
		qb.clauses = clnList;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + getName() + ")";
	}

	@Override
	public String getEntityType() {
		return EntityType.CLAUSE_BUNDLE.name();
	}

	@NotEmpty
	@Length(max = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return A newly created list containing the referenced clauses in this
	 *         bundle.
	 */
	public List<ClauseDef> getClauses() {
		if(clauses == null) clauses = new ArrayList<ClauseDef>();
		return clauses;
	}

	public void setClauses(List<ClauseDef> quotes) {
		this.clauses = quotes;
	}

	/**
	 * Adds a qoute at the end of the quote list.
	 * @param quote
	 */
	public void addClause(ClauseDef quote) {
		if(quote == null) throw new NullPointerException();
		getClauses().add(quote);
	}

	/**
	 * Adds all clauses in the given collection to the internally managed list of
	 * clauses.
	 * @param clc collection of clauses to add
	 */
	public void addClause(Collection<ClauseDef> clc) {
		if(clc != null) getClauses().addAll(clc);
	}

	/**
	 * Inserts a qoute at the given index.
	 * @param quote
	 * @param index
	 */
	public void insertClause(ClauseDef quote, int index) {
		if(quote == null) throw new NullPointerException();
		getClauses().add(index, quote);
	}

	/**
	 * Removes the given quote.
	 * @param quote
	 * @return <code>true</code> if the quote was removed
	 */
	public boolean removeClause(ClauseDef quote) {
		if(clauses == null) return false;
		return clauses.remove(quote);
	}

	/**
	 * Removes a quote from the given index.
	 * @param index
	 * @return the removed quote or <code>null</code> if not found
	 */
	public ClauseDef removeClauseDef(int index) {
		if(clauses == null) return null;
		return clauses.remove(index);
	}

	/**
	 * Removes any and all contained clauses.
	 */
	public void clearClauseDefs() {
		if(clauses != null) clauses.clear();
	}

	@Length(max = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(ClauseBundle o) {
		return name != null && o.name != null ? name.compareTo(o.name) : 0;
	}
}
