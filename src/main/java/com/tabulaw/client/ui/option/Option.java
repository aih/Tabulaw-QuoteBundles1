package com.tabulaw.client.ui.option;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.ui.ImageContainer;

/**
 * Option - A single option in an {@link OptionsPanel}.
 * @author jpk
 */
public class Option extends Label {

	/**
	 * Styles - (option.css)
	 * @author jpk
	 */
	protected static class Styles {

		public static final String OPTION = "option";
	}

	private final String text, id;

	/**
	 * Constructor
	 * @param text The option text
	 * @param id optional identifier token
	 */
	public Option(String text, String id) {
		this(text, id, null);
	}

	/**
	 * Constructor
	 * @param text The option text
	 * @param id optional identifier token
	 * @param img Optional image placed before the option text.
	 */
	public Option(String text, String id, Image img) {
		super();
		if(text == null) throw new NullPointerException();
		setStyleName(Styles.OPTION);
		this.text = text;
		this.id = id;
		if(img != null) {
			getElement().appendChild((new ImageContainer(img)).getElement());
		}
		final Element txt = DOM.createSpan();
		txt.setInnerText(text);
		getElement().appendChild(txt);
	}

	/**
	 * @return id token
	 */
	public String getId() {
		return id;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return getText();
	}
}