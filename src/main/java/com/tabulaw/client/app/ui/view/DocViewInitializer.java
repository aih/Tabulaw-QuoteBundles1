/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.tabulaw.client.mvc.view.AbstractDynamicViewInitializer;
import com.tabulaw.common.model.DocKey;

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
	protected int getViewId() {
		return documentKey.hashCode();
	}

	public DocKey getDocumentKey() {
		return documentKey;
	}
}
