/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.tabulaw.service.DocUtils;

/**
 * Converts *.doc files to HTML files using the open office api.
 * @see "http://www.artofsolving.com/opensource/jodconverter"
 * @author jpk
 */
public class DocToHtmlConverter extends AbstractOpenOfficeFileConverter {

	/**
	 * Constructor
	 * @param ooc required
	 */
	public DocToHtmlConverter(OpenOfficeConnection ooc) {
		super(ooc);
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isMsWordFileByExtension(f.getName());
	}

	@Override
	public File convert(File input) throws Exception {
		DocumentConverter dc = new OpenOfficeDocumentConverter(ooc);
		File fout = createSiblingFile(input, "html");
		dc.convert(input, fout);
		return fout;
	}

	@Override
	public String getTargetMimeType() {
		return "application/msword";
	}
}
