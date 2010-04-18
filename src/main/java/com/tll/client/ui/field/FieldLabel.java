package com.tll.client.ui.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.ui.IHasHoverHandlers;
import com.tll.client.ui.field.impl.FieldLabelImpl;
import com.tll.util.StringUtil;

/**
 * FieldLabel - the field label of an {@link AbstractField} impl.
 * @author jpk
 */
public final class FieldLabel extends Widget implements HasText, HasClickHandlers, IHasHoverHandlers {

	private static final String requiredToken = "<sup class=\"" + IFieldWidget.Styles.REQUIRED + "\">*</sup>";

	private static final FieldLabelImpl impl = (FieldLabelImpl) GWT.create(FieldLabelImpl.class);

	private boolean required;
	private String text;

	/**
	 * Constructor
	 * @param text The label text
	 * @param fldId The DOM element id of the associated form field. May be
	 *        <code>null</code>.
	 * @param required Is the associated field required?
	 */
	public FieldLabel(String text, String fldId, boolean required) {
		setElement(DOM.createLabel());
		this.required = required;
		setText(text);
		if(fldId != null) {
			setFor(fldId);
		}
	}

	/**
	 * Constructor
	 * @param text
	 */
	public FieldLabel(String text) {
		this(text, null, false);
	}

	/**
	 * Constructor
	 */
	public FieldLabel() {
		this(null, null, false);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * Sets the for attrubute.
	 * @param fldId The DOM element id of the associated form field.
	 */
	public void setFor(String fldId) {
		impl.setFor(getElement(), fldId);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if(StringUtil.isEmpty(text)) text = "";
		getElement().setInnerHTML(required ? text + requiredToken : text);
		this.text = text;
	}

	public void setRequired(boolean required) {
		if(this.required != required) {
			getElement().setInnerHTML(required ? text + requiredToken : text);
			this.required = required;
		}
	}
}