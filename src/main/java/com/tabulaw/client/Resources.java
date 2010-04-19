/**
 * The Logic Lab
 * @author jpk
 * @since Feb 17, 2010
 */
package com.tabulaw.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Poc app resources.
 * @author jpk
 */
public interface Resources extends ClientBundle {

	Resources INSTANCE = GWT.create(Resources.class);

	@Source("../public/images/note-icon.png")
	ImageResource searchImage();

	@Source("../public/images/pencil.gif")
	ImageResource pencil();
}
