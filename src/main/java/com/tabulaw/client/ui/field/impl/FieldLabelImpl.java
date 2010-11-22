/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Nov 25, 2007
 */
package com.tabulaw.client.ui.field.impl;

import com.google.gwt.dom.client.Element;

public class FieldLabelImpl {

	public void setFor(Element elm, String fldId) {
		elm.setAttribute("for", fldId);
	}
}