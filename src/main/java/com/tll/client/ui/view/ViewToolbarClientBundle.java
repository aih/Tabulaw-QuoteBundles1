package com.tll.client.ui.view;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * ViewToolbarClientBundle
 * @author jpk
 */
public interface ViewToolbarClientBundle extends ClientBundle {

	/**
	 * arrow_sm_right
	 * @return the image prototype
	 */
	@Source(value = "com/tll/public/images/arrow_sm_right.gif")
	ImageResource arrow_sm_right();

	/**
	 * arrow_sm_down
	 * @return the image prototype
	 */
	@Source(value = "com/tll/public/images/arrow_sm_down.gif")
	ImageResource arrow_sm_down();

	/**
	 * close
	 * @return the image prototype
	 */
	@Source(value = "com/tll/public/images/close.gif")
	ImageResource close();

	/**
	 * external (11x11)
	 * @return the image prototype
	 */
	@Source(value = "com/tll/public/images/external.gif")
	ImageResource external();

	/**
	 * permalink (11x11)
	 * @return the image prototype
	 */
	@Source(value = "com/tll/public/images/permalink.gif")
	ImageResource permalink();

	/**
	 * refresh
	 * @return the image prototype
	 */
	@Source(value = "com/tll/public/images/refresh.gif")
	ImageResource refresh();
}