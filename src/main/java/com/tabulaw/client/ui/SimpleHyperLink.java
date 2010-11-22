/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Nov 21, 2007
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * SimpleHyperLink - An anchor element that binds click handlers with out
 * invoking the browser's default click event action which involves GWT's
 * history mechanism which is what we want to avoid.
 * @author jpk
 */
public class SimpleHyperLink extends Widget implements HasHTML, HasClickHandlers {

	/**
	 * Styles - (widget-tll.css)
	 * @author jpk
	 */
	static class Styles {

		public static final String SHL = "tll-shl";
	}

	/**
	 * Constructor
	 */
	public SimpleHyperLink() {
		this(null, null);
	}

	/**
	 * Constructor
	 * @param text
	 */
	public SimpleHyperLink(String text) {
		this(text, null);
	}

	/**
	 * Constructor
	 * @param text
	 * @param html treat <code>text</code> as an html string?
	 * @param clickHandler
	 */
	public SimpleHyperLink(String text, boolean html, ClickHandler clickHandler) {
		setElement(DOM.createAnchor());

		// prevents text selection by double-click
		getElement().setPropertyString("href", "#");

		setStyleName(Styles.SHL);

		if(text != null) {
			if(html) {
				setHTML(text);
			}
			else {
				setText(text);
			}
		}

		if(clickHandler != null) {
			addClickHandler(clickHandler);
		}
	}

	/**
	 * Constructs
	 * @param text a String or <code>null</code>
	 * @param clickHandler May be <code>null</code>
	 */
	public SimpleHyperLink(String text, ClickHandler clickHandler) {
		this(text, false, clickHandler);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		if(event.getTypeInt() == Event.ONCLICK) {
			// keep '#' out of the location bar
			DOM.eventPreventDefault(event);
			//DOM.eventCancelBubble(event, true);
		}
	}

	// HasText methods
	public void setText(String text) {
		getElement().setInnerText(text);
	}

	public String getText() {
		return getElement().getInnerText();
	}

	// HasHTML mehtods
	public String getHTML() {
		return getElement().getInnerHTML();
	}

	public void setHTML(String html) {
		getElement().setInnerHTML(html);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
