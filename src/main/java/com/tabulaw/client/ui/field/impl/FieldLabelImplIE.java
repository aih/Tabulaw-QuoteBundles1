/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Nov 25, 2007
 */
package com.tabulaw.client.ui.field.impl;

import com.google.gwt.dom.client.Element;

class FieldLabelImplIE extends FieldLabelImpl {

	@Override
	public void setFor(Element elm, String fldId) {
		super.setFor(elm, fldId);
		elm.setAttribute("htmlFor", fldId);
	}

}