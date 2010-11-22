/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 15, 2010
 */
package com.tabulaw.service.convert;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.tabulaw.service.DocUtils;

/**
 * Converts text files to html files.
 * @author jpk
 */
public class TextToHtmlManualConverter extends AbstractDataConverter {

	@Override
	public String getTargetFileExtension() {
		return "html";
	}

	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {

		String fcontent = IOUtils.toString(input, "UTF-8");
		if(fcontent == null) fcontent = "";

		StringBuilder sb = new StringBuilder(fcontent);
		DocUtils.htmlizeText(sb, null);

		IOUtils.write(sb.toString(), output);
	}

	@Override
	public String getSourceMimeType() {
		return "text/plain";
	}
}
