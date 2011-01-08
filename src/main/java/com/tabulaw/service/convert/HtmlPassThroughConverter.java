/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 24, 2010
 */
package com.tabulaw.service.convert;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author jpk
 */
public class HtmlPassThroughConverter extends AbstractDataConverter {

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
}
