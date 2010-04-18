/**
 * The Logic Lab
 * @author jpk
 * Nov 25, 2007
 */
package com.tll.client.ui.field.impl;

import com.google.gwt.dom.client.Element;

public class FieldLabelImpl {

	public void setFor(Element elm, String fldId) {
		elm.setAttribute("for", fldId);
	}
}