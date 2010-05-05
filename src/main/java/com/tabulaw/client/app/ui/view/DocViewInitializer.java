/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.tabulaw.client.mvc.view.AbstractDynamicViewInitializer;
import com.tabulaw.common.model.ModelKey;

/**
 * DocViewInitializer
 * @author jpk
 */
public class DocViewInitializer extends AbstractDynamicViewInitializer {

	private final ModelKey documentKey;

	/**
	 * Constructor
	 * @param documentKey
	 */
	public DocViewInitializer(ModelKey documentKey) {
		super(DocView.klas);
		this.documentKey = documentKey;
	}

	@Override
	protected int getViewId() {
		return documentKey.hashCode();
	}

	public ModelKey getDocumentKey() {
		return documentKey;
	}
}
