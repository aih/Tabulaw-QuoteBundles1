/**
 * The Logic Lab
 * @author jpk
 * @since Feb 17, 2010
 */
package com.tabulaw.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Poc app resources.
 * @author jpk
 */
public interface Resources extends ClientBundle {

	Resources INSTANCE = GWT.create(Resources.class);

	@Source("../../public/images/magnifying_glass_alt_24x24.png")
	ImageResource magnifyingGlass();

	@Source("../../public/images/google-scholar.png")
	ImageResource googleScholarLogo();

	@Source("../../public/images/go-to-highlight2.png")
	ImageResource gotoHighlight();

	@Source("../../public/images/note-icon.png")
	ImageResource searchImage();

	@Source("../../public/images/edit-icon.png")
	ImageResource edit();
	
	@Source("../../public/images/delete-button.png")
	ImageResource delete();

	@Source("../../public/images/delete-button-larger.png")
	ImageResource deleteLarger();
	
	@Source("../../public/images/x-button.png")
	ImageResource XButton();

	@Source("../../public/images/document-icon.png")
	ImageResource documentIcon();

	@Source("../../public/images/quote-bundle-icon.png")
	ImageResource quoteBundleIcon();
	
	@Source("../../public/images/plus.png")
	ImageResource plus();
}
