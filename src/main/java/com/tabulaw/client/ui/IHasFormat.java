/**
 * The Logic Lab
 * @author jpk Jan 22, 2008
 */
package com.tabulaw.client.ui;

import com.tabulaw.client.util.GlobalFormat;

/**
 * IHasFormat - Indicates formatting support.
 * @author jpk
 */
public interface IHasFormat {

	/**
	 * @return The format.
	 */
	GlobalFormat getFormat();

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	void setFormat(GlobalFormat format);
}
