/**
 * The Logic Lab
 * @author jpk
 * @since May 15, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.tabulaw.service.DocUtils;

/**
 * Converts text files to html files.
 * @author jpk
 */
public class TextToHtmlManualConverter extends AbstractFileConverter {

	private ThreadLocal<String> title = new ThreadLocal<String>();
	
	@Override
	public String getTargetFileExtension() {
		return "html";
	}
	
	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {

		String fcontent = IOUtils.toString(input, "UTF-8");
		if(fcontent == null) fcontent = "";

		StringBuilder sb = new StringBuilder(fcontent);
		DocUtils.htmlizeText(sb, title.get() == null ? "" : title.get());

		IOUtils.write(sb.toString(), output);
	}
	

	@Override
	public String getSourceMimeType() {
		return "text/plain";
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isTextFileByExtension(f.getName());
	}

	@Override
	public File convert(File input) throws Exception {
		title.set(input.getName());
		File result = super.convert(input);
		title.set(null);
		return result;
	}
}
