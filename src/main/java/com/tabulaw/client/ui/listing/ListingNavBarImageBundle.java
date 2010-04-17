/**
 * The Logic Lab
 * @author jpk
 * Aug 28, 2007
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * ListingNavBarImageBundle
 * @author jpk
 */
public interface ListingNavBarImageBundle extends ClientBundle {

	/**
	 * split
	 * @return the image prototype
	 */
	@Source(value = "../../split.gif")
	ImageResource split();

	/**
	 * split
	 * @return the image prototype
	 */
	@Source(value = "../../refresh.gif")
	ImageResource refresh();

	/**
	 * page_first
	 * @return the image prototype
	 */
	@Source(value = "../../page-first.gif")
	ImageResource page_first();

	/**
	 * page_first_disabled
	 * @return the image prototype
	 */
	@Source(value = "../../page-first-disabled.gif")
	ImageResource page_first_disabled();

	/**
	 * page_last
	 * @return the image prototype
	 */
	@Source(value = "../../page-last.gif")
	ImageResource page_last();

	/**
	 * page_last_disabled
	 * @return the image prototype
	 */
	@Source(value = "../../page-last-disabled.gif")
	ImageResource page_last_disabled();

	/**
	 * page_next
	 * @return the image prototype
	 */
	@Source(value = "../../page-next.gif")
	ImageResource page_next();

	/**
	 * page_next_disabled
	 * @return the image prototype
	 */
	@Source(value = "../../page-next-disabled.gif")
	ImageResource page_next_disabled();

	/**
	 * page_prev
	 * @return the image prototype
	 */
	@Source(value = "../../page-prev.gif")
	ImageResource page_prev();

	/**
	 * page_prev_disabled
	 * @return the image prototype
	 */
	@Source(value = "../../page-prev-disabled.gif")
	ImageResource page_prev_disabled();
}
