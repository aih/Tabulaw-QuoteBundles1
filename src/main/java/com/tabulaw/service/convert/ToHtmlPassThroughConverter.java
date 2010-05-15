/**
 * The Logic Lab
 * @author jpk
 * @since Apr 24, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

/**
 * @author jpk
 */
public class ToHtmlPassThroughConverter extends AbstractToHtmlConverter {

	@Override
	public boolean supportsContentType(String contentType) {
		return false;
	}

	@Override
	public File convert(File input, String contentType) throws Exception {
		if(isHtmlFileBasedOnFileExtension(input)) {
			return input;
		}
		throw new IllegalArgumentException("Non-html file based on file extension");
	}

}
