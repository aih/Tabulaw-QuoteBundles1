/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tll.tabulaw.server.convert;

import java.io.File;

/**
 * @author jpk
 */
abstract class AbstractFileConverter implements IFileConverter {

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
