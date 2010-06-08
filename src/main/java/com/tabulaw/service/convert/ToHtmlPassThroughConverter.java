/**
 * The Logic Lab
 * @author jpk
 * @since Apr 24, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

import com.tabulaw.service.DocUtils;

/**
 * @author jpk
 */
public class ToHtmlPassThroughConverter extends AbstractFileConverter {

	@Override
	public String getTargetMimeType() {
		return "text/html";
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
