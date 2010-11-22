/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jopaki
 * @since Aug 26, 2010
 */
package com.tabulaw.model;

import org.hibernate.validator.constraints.Length;

import com.tabulaw.util.StringUtil;

/**
 * Clause definition entity.
 * @author jopaki
 */
public class ClauseDef extends EntityBase {

	private static final long serialVersionUID = 7371102243708658624L;

	private String id, text;

	@Override
	protected IEntity newInstance() {
		return new ClauseDef();
	}

	@Override
	public String getEntityType() {
		return EntityType.CLAUSE_DEF.name();
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

	@Length(max = 4000)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		ClauseDef cd = (ClauseDef) cln;
		cd.text = text;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + StringUtil.abbr(getText(), 50) + ")";
	}
}
