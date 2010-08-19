/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.tabulaw.service.DocUtils;

/**
 * Converts *.doc files to HTML files using the open office api.
 * @see "http://www.artofsolving.com/opensource/jodconverter"
 * @author jpk
 */
public class DocToHtmlConverter extends AbstractFileConverter {

	/**
	 * Constructor
	 */
	public DocToHtmlConverter() {
		super();
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isMsWordFileByExtension(f.getName());
	}

	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {
		throw new UnsupportedOperationException("Currently not implemented");		
	}

	@Override
	public String getSourceMimeType() {
		return "application/msword";
	}
	
	@Override
	public String getTargetFileExtension() {
		return "html";
	}
}
