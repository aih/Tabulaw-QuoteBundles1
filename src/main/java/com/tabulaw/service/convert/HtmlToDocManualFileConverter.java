/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.tabulaw.service.DocUtils;

/**
 * Converts HTML files to doc files by manually manipulating the html markup.
 * @author jpk
 */
public class HtmlToDocManualFileConverter extends AbstractFileConverter {

	@Override
	public String getTargetMimeType() {
		return "application/msword";
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}

	@Override
	public File convert(File input) throws Exception {
		String htmlContent = FileUtils.readFileToString(input);

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
		File fdoc = createSiblingFile(input, "doc");
		FileUtils.writeStringToFile(fdoc, docContent);
		return fdoc;
	}
}
