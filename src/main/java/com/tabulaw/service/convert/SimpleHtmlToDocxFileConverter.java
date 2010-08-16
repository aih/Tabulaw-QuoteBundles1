/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 15, 2010
 */
package com.tabulaw.service.convert;

import com.tabulaw.service.convert.simplehtmlconverter.Constants;
import com.tabulaw.service.convert.simplehtmlconverter.writer.Docx4jDocumentContext;

/**
 * HTML to docx via simplehtmlconverter api.
 * @author jopaki
 */
public class SimpleHtmlToDocxFileConverter extends AbstractSimpleHtmlConvertor {

	/**
	 * Constructor
	 */
	public SimpleHtmlToDocxFileConverter() {
		super(new Docx4jDocumentContext());
	}

	@Override
	protected String getFileExtension() {
		return "docx";
	}

	@Override
	public String getTargetMimeType() {
		return Constants.DOCX_MIME_TYPE;
	}
}
