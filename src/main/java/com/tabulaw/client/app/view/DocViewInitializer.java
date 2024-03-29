/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.view;

import com.tabulaw.client.view.AbstractDynamicViewInitializer;
import com.tabulaw.model.DocKey;

/**
 * DocViewInitializer
 * @author jpk
 */
public class DocViewInitializer extends AbstractDynamicViewInitializer {

	private final DocKey documentKey;

	/**
	 * Constructor
	 * @param documentKey
	 */
	public DocViewInitializer(DocKey documentKey) {
		super(DocView.klas);
		this.documentKey = documentKey;
	}

	@Override
	protected String getInstanceToken() {
		return documentKey.getId();
	}

	public DocKey getDocumentKey() {
		return documentKey;
	}
}
