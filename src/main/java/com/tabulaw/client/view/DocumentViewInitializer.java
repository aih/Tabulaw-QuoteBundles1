/**
 * The Logic Lab
 * @author jpk
 * @since Feb 13, 2010
 */
package com.tabulaw.client.view;

import com.tabulaw.common.model.ModelKey;
import com.tll.client.mvc.view.AbstractDynamicViewInitializer;

/**
 * DocumentViewInitializer
 * @author jpk
 */
public class DocumentViewInitializer extends AbstractDynamicViewInitializer {

	private final ModelKey documentKey;

	/**
	 * Constructor
	 * @param documentKey
	 */
	public DocumentViewInitializer(ModelKey documentKey) {
		super(DocumentView.klas);
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
