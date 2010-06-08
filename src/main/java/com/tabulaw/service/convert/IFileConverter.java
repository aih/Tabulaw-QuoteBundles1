/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

/**
 * Converts a file of a particular type to a another file of a different type.
 * @author jpk
 */
public interface IFileConverter {

	/**
	 * @return The mime-type to which this converter converts.
	 */
	String getTargetMimeType();

	/**
	 * Is the given file ref convertable by this converter?
	 * @param f file to test
	 * @return true/false
	 */
	boolean isFileConvertable(File f);

	/**
	 * Convert the given input file creating a new file.
	 * @param input input file
	 * @return ref to the converted file
	 * @throws Exception When the conversion fails
	 */
	File convert(File input) throws Exception;
}
