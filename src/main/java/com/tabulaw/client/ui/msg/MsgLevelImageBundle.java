package com.tabulaw.client.ui.msg;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.tabulaw.common.msg.Msg;

/**
 * MsgLevelImageBundle - Images for {@link Msg} related UI artifacts.
 * @author jpk
 */
public interface MsgLevelImageBundle extends ClientBundle {

	/**
	 * The message level image bundle instance.
	 */
	static final MsgLevelImageBundle INSTANCE = (MsgLevelImageBundle) GWT.create(MsgLevelImageBundle.class);

	/**
	 * info
	 * @return the image prototype
	 */
	@Source(value = "../../../public/images/info.gif")
	ImageResource info();

	/**
	 * warn
	 * @return the image prototype
	 */
	@Source(value = "../../../public/images/warn.gif")
	ImageResource warn();

	/**
	 * error
	 * @return the image prototype
	 */
	@Source(value = "../../../public/images/error.gif")
	ImageResource error();

	/**
	 * fatal
	 * @return the image prototype
	 */
	@Source(value = "../../../public/images/fatal.gif")
	ImageResource fatal();
}