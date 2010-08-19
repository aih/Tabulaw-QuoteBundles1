/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.tabulaw.service.DocUtils;

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
	
	@Override
	public String getTargetMimeType() {
		return DocUtils.getMimeTypeFromFileExt(getTargetFileExtension());
	}

	@Override
	public File convert(File inputFile) throws Exception {
		File newFile = createSiblingFile(inputFile, getTargetFileExtension());
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(inputFile)); 
			output = new BufferedOutputStream(new FileOutputStream(newFile));		
			convert(input, output);
			return newFile;
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}
	
	

}
