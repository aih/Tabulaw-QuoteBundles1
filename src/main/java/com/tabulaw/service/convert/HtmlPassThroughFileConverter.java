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
public class HtmlPassThroughFileConverter extends AbstractFileConverter {

	@Override
	public File convert(File input, FileType outputFileType) throws Exception {
		if(isHtmlFileBasedOnFileExtension(input)) {
			return input;
		}
		return null;
	}

}
