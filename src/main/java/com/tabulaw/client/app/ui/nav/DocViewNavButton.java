/**
 * The Logic Lab
 * @author jpk
 * @since Feb 26, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.mvc.view.IViewInitializer;
import com.tabulaw.common.model.DocKey;
import com.tabulaw.common.model.IModelKeyProvider;
import com.tabulaw.common.model.ModelKey;

/**
 * Routes to an open document when clicked.
 * @author jpk
 */
public class DocViewNavButton extends AbstractNavButton implements IModelKeyProvider {

	private final DocKey documentKey;

	/**
	 * Constructor
	 * @param docKey The document model key
	 */
	public DocViewNavButton(DocKey docKey) {
		super();
		this.documentKey = docKey;
		setTitle(documentKey.descriptor());
		setDisplay(docKey.getName(), "doc", Resources.INSTANCE.XButton());
	}

	@Override
	public void setDisplay(String text, String buttonSecondaryStyle, ImageResource imageResource) {
		super.setDisplay(text, buttonSecondaryStyle, imageResource);
		// image goes last
		DOM.appendChild(getElement(), img.getElement());
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return new DocViewInitializer(documentKey);
	}

	@Override
	public ModelKey getModelKey() {
		return documentKey;
	}
}
