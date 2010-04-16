/**
 * The Logic Lab
 * @author jpk
 * @since Feb 24, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.shared.GwtEvent;
import com.tabulaw.client.model.MarkOverlay;

/**
 * TextSelectEvent
 * @author jpk
 */
public class TextSelectEvent extends GwtEvent<ITextSelectHandler> {

	public static final Type<ITextSelectHandler> TYPE = new Type<ITextSelectHandler>();

	private final MarkOverlay mark;

	/**
	 * Constructor
	 * @param mark
	 */
	public TextSelectEvent(MarkOverlay mark) {
		super();
		this.mark = mark;
	}

	public MarkOverlay getMark() {
		return mark;
	}

	@Override
	protected void dispatch(ITextSelectHandler handler) {
		handler.onTextSelect(this);
	}

	@Override
	public GwtEvent.Type<ITextSelectHandler> getAssociatedType() {
		return TYPE;
	}

}
