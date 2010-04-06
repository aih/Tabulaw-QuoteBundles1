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
	 * Converts the given file to a new file of a prescribed type.
	 * @param input input file
	 * @param outputFileType the desired converted file type
	 * @return ref to the converted file
	 * @throws Exception When the conversion is unsuccessful for any reason
	 */
	File convert(File input, FileType outputFileType) throws Exception;
}
