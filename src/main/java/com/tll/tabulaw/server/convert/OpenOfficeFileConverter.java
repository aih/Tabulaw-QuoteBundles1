/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tll.tabulaw.server.convert;

import java.io.File;
import java.net.ConnectException;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * Converts files using the open office api
 * @see "http://www.artofsolving.com/opensource/jodconverter"
 * @author jpk
 */
public class OpenOfficeFileConverter extends AbstractFileConverter {
	
	/**
	 * Factory method for creating instances.
	 * @return newly created instance having a separate open office connection.
	 * @throws ConnectException Upon failure to connect with open office. 
	 */
	public static OpenOfficeFileConverter create() throws ConnectException {
		// open office file converter
		SocketOpenOfficeConnection ooc = new SocketOpenOfficeConnection();
		ooc.connect();
		return new OpenOfficeFileConverter(ooc);
	}

	private final OpenOfficeConnection ooc;

	/**
	 * Constructor
	 * @param ooc required
	 */
	private OpenOfficeFileConverter(OpenOfficeConnection ooc) {
		super();
		if(ooc == null) throw new NullPointerException();
		this.ooc = ooc;
	}

	@Override
	public File convert(File input, FileType outputFileType) {
		DocumentConverter dc = new OpenOfficeDocumentConverter(ooc);
		File fout = createSiblingFile(input, outputFileType.getSuffix());
		dc.convert(input, fout);
		return fout;
	}

	public OpenOfficeConnection getOpenOfficeConnection() {
		return ooc;
	}
}
