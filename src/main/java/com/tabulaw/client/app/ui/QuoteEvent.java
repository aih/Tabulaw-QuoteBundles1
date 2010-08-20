/**
 * The Logic Lab
 * @author jpk
 * @since May 9, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jpk
 */
public class QuoteEvent extends GwtEvent<IQuoteHandler> {

	public static final Type<IQuoteHandler> TYPE = new Type<IQuoteHandler>();

	public static enum QuoteType {

		/**
		 * Indicates a quote for which to copy its quote text to the current cursor
		 * location in an editable doc.
		 */
		CURRENT_PASTE,
	}

	private final QuoteType qtype;

	/**
	 * Fire a quote event on the registered listeners under the given handler.
	 * @param handler
	 * @param qtype
	 */
	public static void fireQuoteEvent(IHasQuoteHandlers handler, QuoteType qtype) {
		handler.fireEvent(new QuoteEvent(qtype));
	}

	/**
	 * Constructor
	 * @param qtype
	 */
	public QuoteEvent(QuoteType qtype) {
		super();
		this.qtype = qtype;
	}

	@Override
	protected void dispatch(IQuoteHandler handler) {
		handler.onQuoteEvent(this);
	}

	@Override
	public Type<IQuoteHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return The quote type
	 */
	public QuoteType getQtype() {
		return qtype;
	}
}
