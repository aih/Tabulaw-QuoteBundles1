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
import com.tabulaw.client.mvc.view.IViewInitializer;

/**
 * Button like widget intended for use in a nav panel.
 * @author jpk
 */
public abstract class AbstractNavButton extends Composite {

	public static class Styles {

		/**
		 * The button's *primary* sytle.
		 */
		public static final String NAV = "navButton";

		/**
		 * Wraps the button text in a span with this style.
		 */
		public static final String TEXT = "text";
		
		/**
		 * A '+' icon displays to the right of the text.
		 */
		public static final String PLUS = "plus";
	}

	private final HTML html;

	/**
	 * Constructor - No ui content is set.
	 */
	public AbstractNavButton() {
		this(null, null);
	}

	/**
	 * Constructor
	 * @param buttonText
	 * @param buttonSecondaryStyle E.G: "arrow", "plus" or <code>null</code>
	 */
	public AbstractNavButton(String buttonText, String buttonSecondaryStyle) {
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
		html.setHTML("<span class=\"" + Styles.TEXT + "\">" + (buttonText == null ? "" : buttonText) + "</span>");
		html.setStylePrimaryName(Styles.NAV);
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

	/**
	 * @return The view init to which this nav button points.
	 */
	protected abstract IViewInitializer getViewInitializer();

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		IViewInitializer vi = getViewInitializer();
		result = prime * result + ((vi == null) ? 0 : vi.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		AbstractNavButton other = (AbstractNavButton) obj;
		IViewInitializer vi = getViewInitializer(), otherVi = other.getViewInitializer();
		if(vi == null && otherVi != null) return false;
		if(vi != null && !vi.equals(otherVi)) return false;
		return true;
	}

}
