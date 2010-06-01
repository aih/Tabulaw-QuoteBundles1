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

/**
 * Converts HTML files to doc files using the open office api.
 * @see "http://www.artofsolving.com/opensource/jodconverter"
 * @author jpk
 */
public class HtmlToDocConverter extends AbstractFileConverter {

	private final OpenOfficeConnection ooc;

	/**
	 * Constructor
	 * @param ooc required
	 */
	public HtmlToDocConverter(OpenOfficeConnection ooc) {
		super();
		if(ooc == null) throw new NullPointerException();
		this.ooc = ooc;
	}

	@Override
	public boolean supportsContentType(String contentType) {
		return "text/html".equals(contentType);
	}

	@Override
	public File convert(File input, String contentType) {
		DocumentConverter dc = new OpenOfficeDocumentConverter(ooc);
		File fout = createSiblingFile(input, "doc");
		dc.convert(input, fout);
		return fout;
	}
}
