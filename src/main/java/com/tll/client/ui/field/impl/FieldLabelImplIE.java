/**
 * The Logic Lab
 * @author jpk
 * Nov 25, 2007
 */
package com.tll.client.ui.field.impl;

import com.google.gwt.dom.client.Element;

class FieldLabelImplIE extends FieldLabelImpl {

	@Override
	public void setFor(Element elm, String fldId) {
		super.setFor(elm, fldId);
		elm.setAttribute("htmlFor", fldId);
	}

}