/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * Button like widget intended for use in a nav panel.
 * @author jpk
 */
public abstract class AbstractButton extends Composite {

	private final HTML html;

	/**
	 * Constructor - No ui content is set.
	 */
	public AbstractButton() {
		this(null, null);
	}

	/**
	 * Constructor
	 * @param buttonText
	 * @param buttonSecondaryStyle E.G: "arrow", "plus" or <code>null</code>
	 */
	public AbstractButton(String buttonText, String buttonSecondaryStyle) {
		super();
		html = new HTML();
		initWidget(html);
		setDisplay(buttonText, buttonSecondaryStyle);
	}
	
	protected String getTitleText(String buttonText) {
		return "View " + buttonText + "...";
	}

	/**
	 * Sets or resets the ui content of the nav button.
	 * @param buttonText
	 * @param buttonSecondaryStyle
	 */
	protected void setDisplay(String buttonText, String buttonSecondaryStyle) {
		html.setHTML("<span class=\"" + "text" + "\">" + (buttonText == null ? "" : buttonText) + "</span>");
		html.setStylePrimaryName("navButton");
		html.setTitle(getTitleText(buttonText));
		if(buttonSecondaryStyle != null) {
			html.addStyleDependentName(buttonSecondaryStyle);
		}
	}

	/*
	@Override
	public void onClick(ClickEvent event) {
		// default behavior
		ViewManager.get().dispatch(new ShowViewRequest(getViewInitializer()));
	}
	*/
	
	private HandlerRegistration clickRegiseration;
	
	/**
	 * Sets the handler for the button click. 
	 * @param handler
	 */
	public void setClickHandler(ClickHandler handler) {
		if(clickRegiseration != null) clickRegiseration.removeHandler();
		clickRegiseration = html.addClickHandler(handler);
	}
}
