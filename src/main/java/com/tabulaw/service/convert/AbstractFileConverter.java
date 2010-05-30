/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

/**
 * @author jpk
 */
abstract class AbstractFileConverter implements IFileConverter {
	
	/**
	 * Is the file named such that is implies it has html content?
	 * @param f
	 * @return true/false
	 */
	static boolean isHtmlFileBasedOnFileExtension(File f) {
		String fname = f.getName();
		if(fname.endsWith(".htm") || fname.endsWith(".html")) return true;
		return false;
	}

	/**
	 * Is the file named such that is implies it has textual content?
	 * @param f
	 * @return true/false
	 */
	static boolean isTextFileBasedOnFileExtension(File f) {
		String fname = f.getName();
		if(fname.endsWith(".txt") || fname.endsWith(".text")) return true;
		return false;
	}

	/**
	 * Creates a file ref with the local name having the given suffix that is
	 * sibling to the given file ref.
	 * @param f
	 * @param suffix
	 * @return file ref
	 */
	static File createSiblingFile(File f, String suffix) {
		String root = f.getParent();
		String fname = f.getName();
		int ix = fname.indexOf('.');
		String baseName = ix > 0 ? fname.substring(0, ix) : fname;
		return new File(root + File.separator + baseName + '.' + suffix);
	}

}
