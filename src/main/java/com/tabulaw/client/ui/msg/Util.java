/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Mar 3, 2009
 */
package com.tabulaw.client.ui.msg;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.tabulaw.common.msg.Msg.MsgLevel;


/**
 * Util
 * @author jpk
 */
abstract class Util {

	/**
	 * Provides a new {@link Image} containing the associated msg level icon.
	 * @param level The message level
	 * @return Image
	 */
	public static Image getMsgLevelImage(MsgLevel level) {
		switch(level) {
			case WARN:
				return AbstractImagePrototype.create(MsgLevelImageBundle.INSTANCE.warn()).createImage();
			case ERROR:
				return AbstractImagePrototype.create(MsgLevelImageBundle.INSTANCE.error()).createImage();
			case FATAL:
				return AbstractImagePrototype.create(MsgLevelImageBundle.INSTANCE.fatal()).createImage();
			default:
			case INFO:
				return AbstractImagePrototype.create(MsgLevelImageBundle.INSTANCE.info()).createImage();
		}
	}
}
