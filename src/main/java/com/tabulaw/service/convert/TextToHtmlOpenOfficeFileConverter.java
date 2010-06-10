/**
 * The Logic Lab
 * @author jpk
 * @since Jun 10, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.tabulaw.service.DocUtils;


/**
 * 
 * @author jpk
 */
public class TextToHtmlOpenOfficeFileConverter extends AbstractOpenOfficeFileConverter {

	/**
	 * Constructor
	 * @param ooc
	 */
	public TextToHtmlOpenOfficeFileConverter(OpenOfficeConnection ooc) {
		super(ooc);
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
		return "text/html";
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isTextFileByExtension(f.getName());
	}

}
