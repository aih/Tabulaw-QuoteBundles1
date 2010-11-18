/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * May 11, 2008
 */
package com.tabulaw.client.ui.edit;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An edit event wrapping a type that represents "edited content".
 * @param <T> edited content type
 * @author jpk
 */
public final class EditEvent<T> extends GwtEvent<IEditHandler<T>> {

	public static enum EditOp {
		SAVE,
		DELETE,
		CANCEL;
	}

	/**
	 * Fires add edit event.
	 * @param <T> edit content type
	 * @param source 
	 * @param added added content
	 */
	public static <T> void fireSave(IHasEditHandlers<T> source, T added) {
		source.fireEvent(new EditEvent<T>(EditOp.SAVE, added));
	}

	/**
	 * Fires a delete edit event.
	 * @param <T> edit content type
	 * @param source
	 */
	public static <T> void fireDelete(IHasEditHandlers<T> source) {
		source.fireEvent(new EditEvent<T>(EditOp.DELETE, null));
	}

	/**
	 * Fires a cancel edit event.
	 * @param <T> edit content type
	 * @param source
	 */
	public static <T> void fireCancel(IHasEditHandlers<T> source) {
		source.fireEvent(new EditEvent<Object>(EditOp.CANCEL, null));
	}

	public static final Type<IEditHandler<?>> TYPE = new Type<IEditHandler<?>>();

	private final EditOp op;

	private final T content;

	/**
	 * Constructor
	 * @param op edit op
	 * @param content edited content
	 */
	private EditEvent(EditOp op, T content) {
		this.op = op;
		this.content = content;
	}

	public EditOp getOp() {
		return op;
	}

	/**
	 * @return The edited content
	 */
	public T getContent() {
		return content;
	}

	@Override
	protected void dispatch(IEditHandler<T> handler) {
		handler.onEdit(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public GwtEvent.Type<IEditHandler<T>> getAssociatedType() {
		return (Type) TYPE;
	}

	@Override
	public String toDebugString() {
		return super.toDebugString();
	}
}
