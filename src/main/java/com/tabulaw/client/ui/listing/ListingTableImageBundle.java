/**
 * The Logic Lab
 * @author jpk
 * Aug 28, 2007
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * ListingTableImageBundle
 * @author jpk
 */
public interface ListingTableImageBundle extends ClientBundle {

	/**
	 * sort_asc
	 * @return the image prototype
	 */
	@Source(value = "../../../public/images/sort_asc.gif")
	ImageResource sort_asc();

	/**
	 * sort_desc
	 * @return the image prototype
	 */
	@Source(value = "../../../public/images/sort_desc.gif")
	ImageResource sort_desc();
}
