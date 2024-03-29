/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Jan 13, 2008
 */
package com.tabulaw.client.model;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.model.IEntity;
import com.tabulaw.model.ModelKey;

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
		LOADED,
		ADDED,
		UPDATED,
		DELETED;
	}
	
	private final ModelChangeOp change;
	private final IEntity model;
	private final ModelKey modelKey;
	private final Widget wsource;

	/**
	 * Constructor
	 * @param wsource the logical widget source of the event
	 * @param change
	 * @param model
	 * @param modelKey
	 */
	public ModelChangeEvent(Widget wsource, ModelChangeOp change, IEntity model, ModelKey modelKey) {
		this.wsource = wsource;
		this.change = change;
		this.model = model;
		this.modelKey = modelKey;
	}
	
	public Widget getWSource() {
		return wsource;
	}

	public ModelChangeOp getChangeOp() {
		return change;
	}

	public IEntity getModel() {
		return model;
	}

	public ModelKey getModelKey() {
		return model == null ? modelKey : new ModelKey(model.getEntityType(), model.getId());
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
