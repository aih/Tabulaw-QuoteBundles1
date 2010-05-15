/**
 * The Logic Lab
 * @author jpk
 * @since May 15, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.tabulaw.util.StringUtil;

/**
 * A button with image and text as its face.
 * @author jpk
 */
public class ImageButton extends Button {

	private Image img;
	private Element span;

	/**
	 * Constructor
	 */
	public ImageButton() {
		super();
		setStylePrimaryName("imgBtn");
	}
	
	/**
	 * Constructor
	 * @param imageResource optional
	 * @param text required
	 */
	public ImageButton(ImageResource imageResource, String text) {
		this();
		if(StringUtil.isEmpty(text)) throw new IllegalArgumentException();
		if(imageResource != null) setResource(imageResource);
		setText(text);
	}

	public void setResource(ImageResource imageResource) {
		if(img == null) {
			img = new Image();
			DOM.insertBefore(getElement(), img.getElement(), DOM.getFirstChild(getElement()));
		}
		img.setResource(imageResource);
		//String definedStyles = img.getElement().getAttribute("style");
		//img.getElement().setAttribute("style", definedStyles + "; vertical-align:middle;");
		img.setStyleName("icon");
	}

	@Override
	public void setText(String text) {
		if(span == null) {
			span = DOM.createSpan();
			span.setClassName("text");
			if(img != null)
				DOM.appendChild(getElement(), span);
			else
				DOM.insertChild(getElement(), span, 0);
		}
		span.setInnerText(text);
	}

	@Override
	public String getText() {
		return span.getInnerText();
	}
}
