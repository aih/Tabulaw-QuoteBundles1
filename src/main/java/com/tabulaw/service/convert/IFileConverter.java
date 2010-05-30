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
	 * Does this converter support the given input file content type?
	 * <p>
	 * The given content type is expected to conform to the mime-type naming
	 * standard.
	 * @param contentType the content (mime) type to check
	 * @return true/false
	 */
	boolean supportsContentType(String contentType);

	/**
	 * Attempts to convert the given file to a new file of a given type.
	 * @param input input file
	 * @param contentType the content type of the given input file
	 * @return ref to the converted file or <code>null</code> if no conversion
	 *         took place
	 * @throws Exception When the conversion is attempted but is unsuccessful for
	 *         any reason
	 */
	File convert(File input, String contentType) throws Exception;
}
