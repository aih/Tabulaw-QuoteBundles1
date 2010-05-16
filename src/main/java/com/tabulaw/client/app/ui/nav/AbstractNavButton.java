/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.tabulaw.client.mvc.view.IViewInitializerProvider;

/**
 * Button like widget intended for use in a nav panel.
 * @author jpk
 */
abstract class AbstractNavButton extends FocusWidget implements IViewInitializerProvider {

	private Image img;
	private Element span;

	/**
	 * Constructor
	 */
	public AbstractNavButton() {
		super(Document.get().createDivElement());
		setStylePrimaryName("navButton");
	}

	/**
	 * Constructor
	 * @param buttonText
	 * @param buttonSecondaryStyle E.G: "arrow", "plus" or <code>null</code>
	 * @param imageResource optional image to show in button
	 */
	public AbstractNavButton(String buttonText, String buttonSecondaryStyle, ImageResource imageResource) {
		this();
		setDisplay(buttonText, buttonSecondaryStyle, imageResource);
	}

	/**
	 * Sets or resets the the button's face html.
	 * @param text required
	 * @param buttonSecondaryStyle optional
	 * @param imageResource optional image to show in button
	 */
	public void setDisplay(String text, String buttonSecondaryStyle, ImageResource imageResource) {

		if(imageResource != null) {
			if(img == null) {
				img = new Image();
				img.setStyleName("icon");
				DOM.insertBefore(getElement(), img.getElement(), DOM.getFirstChild(getElement()));
			}
			img.setResource(imageResource);
		}

		if(span == null) {
			span = DOM.createSpan();
			span.setClassName("text");
			if(img != null)
				DOM.appendChild(getElement(), span);
			else
				DOM.insertChild(getElement(), span, 0);
		}
		span.setInnerText(text);

		if(buttonSecondaryStyle != null) {
			addStyleDependentName(buttonSecondaryStyle);
		}
	}
}
