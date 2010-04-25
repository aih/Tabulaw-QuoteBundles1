package com.tabulaw.common.model;

import java.util.Date;

import com.tabulaw.schema.Managed;

/**
 * An entity that contains audit information. The information currently stored
 * is the create/modify date and the create/modify user.
 * @author jpk
 */
public abstract class TimeStampEntity extends EntityBase implements ITimeStampEntity {

	private static final long serialVersionUID = 1800355868972602348L;

	private Date dateCreated, dateModified;

	/**
	 * Constructor
	 */
	public TimeStampEntity() {
		super();
	}

	/*
	 * NOTE: we don't enforce a <code>null</code> check since the
	 * {@link EntityTimeStamper} handles it automatically. I.e.: this is a managed
	 * property.
	 * @return The automatically set date of creation.
	 */
	@Managed
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date date) {
		this.dateCreated = date;
	}

	/*
	 * NOTE: we don't enforce a <code>null</code> check since the
	 * {@link EntityTimeStamper} handles it automatically. I.e.: this is a managed
	 * property.
	 * @return The automatically set date of modification.
	 */
	@Managed
	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date date) {
		dateModified = date;
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		TimeStampEntity tse = (TimeStampEntity) cln;
		tse.setDateCreated(dateCreated == null ? null : new Date(dateCreated.getTime()));
		tse.setDateModified(dateModified == null ? null : new Date(dateModified.getTime()));
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
