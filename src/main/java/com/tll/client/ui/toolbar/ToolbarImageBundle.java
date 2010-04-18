package com.tll.client.ui.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * ToolbarImageBundle - Images for {@link Toolbar} related UI artifacts.
 * @author jpk
 */
public interface ToolbarImageBundle extends ClientBundle {

	/**
	 * The message level image bundle instance.
	 */
	static final ToolbarImageBundle INSTANCE = (ToolbarImageBundle) GWT.create(ToolbarImageBundle.class);

	/**
	 * split
	 * @return the split prototype
	 */
	@Source(value = "com/tll/public/images/split.gif")
	ImageResource split();
}