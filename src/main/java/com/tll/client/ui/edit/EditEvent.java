/**
 * The Logic Lab
 * @author jpk
 * May 11, 2008
 */
package com.tll.client.ui.edit;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An edit event wrapping a type that represents "edited content".
 * @param <T> edited content type
 * @author jpk
 */
public final class EditEvent<T> extends GwtEvent<IEditHandler<T>> {

	public static enum EditOp {
		ADD,
		UPDATE,
		DELETE,
		CANCEL;

		public boolean isSave() {
			return this == ADD || this == UPDATE;
		}
	}

	/**
	 * Fires add edit event.
	 * @param <T> edit content type
	 * @param source 
	 * @param added added content
	 */
	public static <T> void fireAdd(IHasEditHandlers<T> source, T added) {
		source.fireEvent(new EditEvent<T>(EditOp.ADD, added));
	}

	/**
	 * Fires an edit event signifying a request to update.
	 * @param <T> edit content type
	 * @param source 
	 * @param updated updated content
	 */
	public static <T> void fireUpdate(IHasEditHandlers<T> source, T updated) {
		source.fireEvent(new EditEvent<T>(EditOp.UPDATE, updated));
	}

	/**
	 * Fires a delete edit event.
	 * @param <T> edit content type
	 * @param source
	 * @param deleted deleted content
	 */
	public static <T> void fireDelete(IHasEditHandlers<T> source, T deleted) {
		source.fireEvent(new EditEvent<T>(EditOp.DELETE, deleted));
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

	@SuppressWarnings("unchecked")
	@Override
	public GwtEvent.Type<IEditHandler<T>> getAssociatedType() {
		return (Type) TYPE;
	}

	@Override
	public String toDebugString() {
		return super.toDebugString();
	}
}
