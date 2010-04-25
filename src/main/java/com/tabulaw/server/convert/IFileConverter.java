/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.server.convert;

import java.io.File;

/**
 * Converts a file of a particular type to a another file of a different type.
 * @author jpk
 */
public interface IFileConverter {

	public static enum FileType {
		HTML("html"),
		DOC("doc"),
		DOCX("docx"),
		RTF("rtf"),
		TEXT("txt"),
		PDF("pdf");

		private final String suffix;

		private FileType(String suffix) {
			this.suffix = suffix;
		}

		public String getSuffix() {
			return suffix;
		}
	}

	/**
	 * Attempts to convert the given file to a new file of a given type.
	 * @param input input file
	 * @param outputFileType the desired file type to which to convert
	 * @return ref to the converted file or <code>null</code> if no conversion
	 *         took place
	 * @throws Exception When the conversion is attempted but is unsuccessful for
	 *         any reason
	 */
	File convert(File input, FileType outputFileType) throws Exception;
}
