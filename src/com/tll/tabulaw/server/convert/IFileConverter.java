/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tll.tabulaw.server.convert;

import java.io.File;


/**
 * Converts a file of a particular type to a another file of a different type.
 * @author jpk
 */
public interface IFileConverter {

	/**
	 * Converts the given file to a new file of a prescribed type.
	 * @param input input file
	 * @return new converted file
	 */
	File convert(File input);
}
