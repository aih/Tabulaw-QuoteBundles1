/**
 * The Logic Lab
 * @author jpk
 * Sep 14, 2007
 */
package com.tabulaw.common.data;

import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.ModelKey;

/**
 * Generic model data transport.
 * @author jpk
 */
public final class ModelPayload extends ModelDataPayload {

	/**
	 * The model.
	 */
	private IEntity model;

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
	public ModelPayload(Status status, IEntity model) {
		super(status);
		this.model = model;
	}

	public IEntity getModel() {
		return model;
	}

	public void setModel(IEntity model) {
		this.model = model;
	}

	public ModelKey getRef() {
		return ref;
	}

	public void setRef(ModelKey ref) {
		this.ref = ref;
	}
}
