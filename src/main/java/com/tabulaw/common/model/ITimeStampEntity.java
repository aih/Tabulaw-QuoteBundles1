package com.tabulaw.common.model;

import java.util.Date;

/**
 * ITimeStampEntity - Entity with timestamping support definition.
 * @author jpk
 */
public interface ITimeStampEntity extends IEntity {
	
	/**
	 * @return The date created
	 */
	Date getDateCreated();

	/**
	 * @param date
	 */
	void setDateCreated(Date date);

	/**
	 * @return The date modified
	 */
	Date getDateModified();

	/**
	 * @param date
	 */
	void setDateModified(Date date);
}
