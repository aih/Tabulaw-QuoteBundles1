/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jopaki
 * @since May 21, 2010
 */
package com.tabulaw.client.model;

import com.tabulaw.model.IEntity;

/**
 * Indicates the ability to get/set an entity of a particular type.
 * @author jopaki
 * @param <E> the model type 
 */
public interface IHasModel<E extends IEntity> {

	/**
	 * @return the model instance.
	 */
	E getModel();
	
	/**
	 * Sets the model instance.
	 * @param model
	 */
	void setModel(E model);
}
