/**
 * The Logic Lab
 * @author jpk
 * Sep 14, 2007
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.common.data.Status;
import com.tabulaw.model.IEntity;
import com.tabulaw.model.ModelKey;

/**
 * Generic model data transport.
 * @param <E> the entity type
 * @author jpk
 */
public final class ModelPayload<E extends IEntity> extends Payload {

	/**
	 * The model.
	 */
	private E model;

	private ModelKey ref;

	/**
	 * Constructor
	 */
	public ModelPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public ModelPayload(Status status) {
		super(status);
	}

	/**
	 * Constructor
	 * @param status
	 * @param model
	 */
	public ModelPayload(Status status, E model) {
		super(status);
		this.model = model;
	}

	public E getModel() {
		return model;
	}

	public void setModel(E model) {
		this.model = model;
	}

	public ModelKey getRef() {
		return ref;
	}

	public void setRef(ModelKey ref) {
		this.ref = ref;
	}
}
