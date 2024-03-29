/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jopaki
 * @since Aug 15, 2010
 */
package com.tabulaw.service.convert;

import com.tabulaw.service.convert.simplehtmlconverter.Constants;
import com.tabulaw.service.convert.simplehtmlconverter.writer.Docx4jDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

/**
 * HTML to docx via simplehtmlconverter api.
 * @author jopaki
 */
public class SimpleHtmlToDocxFileConverter extends AbstractSimpleHtmlConvertor {

	@Override
	public String getTargetFileExtension() {
		return "docx";
	}

	@Override
	public String getTargetMimeType() {
		return Constants.DOCX_MIME_TYPE;
	}

	@Override
	protected IDocumentContext createDocumentContext() {
		return new Docx4jDocumentContext();
	}
}
