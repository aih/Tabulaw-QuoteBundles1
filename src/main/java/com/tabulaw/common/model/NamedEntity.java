package com.tabulaw.common.model;

/**
 * Named entity abstract class
 * @author jpk
 */
public abstract class NamedEntity extends EntityBase implements INamedEntity {

	private static final long serialVersionUID = -2428890910891561540L;

	protected String name;

	@Override
	public abstract IEntity clone();

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " '" + getName() + "'";
	}
}