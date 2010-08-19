/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.tabulaw.service.DocUtils;

/**
 * Converts HTML files to doc files by manually manipulating the html markup.
 * @author jpk
 */
public class HtmlToDocManualFileConverter extends AbstractFileConverter {

	@Override
	public String getSourceMimeType() {
		return "text/html";
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}

	@Override
	public String getTargetFileExtension() {
		return "doc";
	}

	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {
		String htmlContent = IOUtils.toString(input);

		// use HTMLCleaner to help us out
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode root;
		try {
			root = cleaner.clean(htmlContent);
		}
		catch(IOException e) {
			throw new IllegalArgumentException(e);
		}
		
		TagNode body = root.getElementsByName("body", true)[0];
		String docContent = cleaner.getInnerHtml(body);
		IOUtils.write(docContent, output);		
	}
}
