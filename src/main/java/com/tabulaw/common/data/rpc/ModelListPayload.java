/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 27, 2010
 */
package com.tabulaw.common.data.rpc;

import java.util.List;

import com.tabulaw.common.data.Status;
import com.tabulaw.model.IEntity;

/**
 * Payload for transporting a list of entities of prescribed type.
 * @author jpk
 * @param <E> the entity type
 */
public class ModelListPayload<E extends IEntity> extends Payload {

	private List<E> modelList;

	/**
	 * Constructor
	 */
	public ModelListPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public ModelListPayload(Status status) {
		super(status);
	}

	public void setModelList(List<E> modelList) {
		this.modelList = modelList;
	}

	public List<E> getModelList() {
		return modelList;
	}
}
