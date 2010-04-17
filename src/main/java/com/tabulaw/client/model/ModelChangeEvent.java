/**
 * The Logic Lab
 * @author jpk Jan 13, 2008
 */
package com.tabulaw.client.model;

import com.google.gwt.event.shared.GwtEvent;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.ModelKey;

/**
 * Used to dissemminate <em>successful</em> model changes.
 * @author jpk
 */
public final class ModelChangeEvent extends GwtEvent<IModelChangeHandler> {

	/**
	 * The event type.
	 */
	public static final Type<IModelChangeHandler> TYPE = new Type<IModelChangeHandler>();

	/**
	 * ModelChangeOp
	 * @author jpk
	 */
	public static enum ModelChangeOp {
		AUXDATA_READY,
		LOADED,
		ADDED,
		UPDATED,
		DELETED;
	}

	private final ModelChangeOp change;
	private final IEntity model;
	private final ModelKey modelKey;

	/**
	 * Constructor
	 * @param change
	 * @param model
	 * @param modelKey
	 */
	public ModelChangeEvent(ModelChangeOp change, IEntity model, ModelKey modelKey) {
		this.change = change;
		this.model = model;
		this.modelKey = modelKey;
	}

	public ModelChangeOp getChangeOp() {
		return change;
	}

	public IEntity getModel() {
		return model;
	}

	public ModelKey getModelKey() {
		return model == null ? modelKey : model.getKey();
	}

	@Override
	protected void dispatch(IModelChangeHandler handler) {
		handler.onModelChangeEvent(this);
	}

	@Override
	public Type<IModelChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	public String toString() {
		String s = change.toString();
		final ModelKey rk = getModelKey();
		if(rk != null) {
			s += " [ " + rk.toString() + " ]";
		}
		return s;
	}
}
