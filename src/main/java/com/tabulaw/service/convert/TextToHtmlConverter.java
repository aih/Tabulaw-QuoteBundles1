/**
 * The Logic Lab
 * @author jpk
 * @since May 15, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.tabulaw.service.DocUtils;

/**
 * Converts text files to html files.
 * @author jpk
 */
public class TextToHtmlConverter extends AbstractFileConverter {

	@Override
	public String getTargetMimeType() {
		return "text/html";
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isTextFileByExtension(f.getName());
	}

	@Override
	public File convert(File input) throws Exception {
		// generate doc title
		String fname = input.getName();
		if(fname == null) fname = "";

		String fcontent = FileUtils.readFileToString(input, "UTF-8");
		if(fcontent == null) fcontent = "";

		StringBuilder sb = new StringBuilder(fcontent);
		DocUtils.htmlizeText(sb, fname);

		File fout = createSiblingFile(input, "html");
		FileUtils.writeStringToFile(fout, sb.toString(), "UTF-8");
		return fout;
	}
}
