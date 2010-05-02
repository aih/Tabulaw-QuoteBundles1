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

	@Source("../../public/images/permalink.gif")
	ImageResource permalink();

	@Source("../../public/images/note-icon.png")
	ImageResource searchImage();

	@Source("../../public/images/undo.gif")
	ImageResource undo();
	
	// TODO point to a real save icon
	@Source("../../public/images/pencil.gif")
	ImageResource save();
	
	@Source("../../public/images/pencil.gif")
	ImageResource pencil();
	
	@Source("../../public/images/delete.gif")
	ImageResource delete();

	@Source("../../public/images/x-button.png")
	ImageResource XButton();

	@Source("../../public/images/document-icon.png")
	ImageResource documentIcon();

	@Source("../../public/images/quote-bundle-icon.png")
	ImageResource quoteBundleIcon();
}
