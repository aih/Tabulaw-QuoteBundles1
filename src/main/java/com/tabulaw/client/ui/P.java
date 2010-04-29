package com.tabulaw.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * HTML p tag in widget form.
 * @author jpk
 */
public class P extends Widget implements HasHTML, HasText {

	public P() {
		setElement(DOM.createElement("p"));
	}

	public P(String text) {
		this();
		getElement().setInnerText(text);
	}

	@Override
	public String getHTML() {
		return getElement().getInnerHTML();
	}

	@Override
	public void setHTML(String html) {
		getElement().setInnerHTML(html);
	}

	@Override
	public String getText() {
		return getElement().getInnerText();
	}

	@Override
	public void setText(String text) {
		getElement().setInnerText(text);
	}

}