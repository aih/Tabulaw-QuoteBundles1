/**
 * The Logic Lab
 * @author jpk
 * @since Apr 24, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.tabulaw.service.DocUtils;

/**
 * @author jpk
 */
public class ToHtmlPassThroughConverter extends AbstractFileConverter {

	@Override
	public String getTargetFileExtension() {
		return "html";
	}

	@Override
	public String getSourceMimeType() {
		return "text/html";
	}
	
	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {
		IOUtils.copy(input, output);
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}

	@Override
	public File convert(File input) throws Exception {
		return input;
	}
}
