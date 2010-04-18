/**
 * The Logic Lab
 * @author jpk
 * Mar 23, 2008
 */
package com.tll.client.ui.option;

import com.google.gwt.event.shared.GwtEvent;

/**
 * OptionEvent - Event type for {@link IOptionHandler}
 * @author jpk
 */
public class OptionEvent extends GwtEvent<IOptionHandler> {
  
	/**
	 * The event type.
	 */
	public static final Type<IOptionHandler> TYPE = new Type<IOptionHandler>();
	
	/**
	 * EventType - Enables specific event type distinction w/o having to replicate
	 * hander/handlers interfaces.
	 * @author jpk
	 */
	public static enum EventType {
		CHANGED,
		SELECTED;
	}

	/**
	 * The specific type of option event.
	 */
	public final EventType etype;

	/**
	 * The text of the {@link Option} that was clicked.
	 */
	public final String optionText;
	
	/**
	 * Constructor
	 * @param etype The option event "type".
	 * @param optionText The Option text
	 */
	public OptionEvent(EventType etype, String optionText) {
		this.etype = etype;
		this.optionText = optionText;
	}

	@Override
	protected void dispatch(IOptionHandler handler) {
		handler.onOptionEvent(this);
	}

	@Override
	public GwtEvent.Type<IOptionHandler> getAssociatedType() {
		return TYPE;
	}

	
	/**
	 * @return the option event type.
	 */
	public EventType getOptionEventType() {
		return etype;
	}

	/**
	 * @return the optionText
	 */
	public String getOptionText() {
		return optionText;
	}

	@Override
	public String toDebugString() {
		return etype + "|" + optionText;
	}
}
