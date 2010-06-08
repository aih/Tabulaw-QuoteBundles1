/**
 * The Logic Lab
 * @author jpk
 * @since Jun 7, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;


/**
 * 
 * @author jpk
 */
public class HtmlToDocCompositeFileConverter implements IFileConverter {
	
	private static final Log log = LogFactory.getLog(HtmlToDocCompositeFileConverter.class);
	
	private final HtmlToDocByOpenOfficeFileConverter openOfficeConverter;
	
	private final HtmlToDocManualFileConverter manualConverter;

	/**
	 * Constructor
	 * @param ooc optional
	 */
	public HtmlToDocCompositeFileConverter(OpenOfficeConnection ooc) {
		super();
		openOfficeConverter = ooc == null ? null : new HtmlToDocByOpenOfficeFileConverter(ooc);
		manualConverter = new HtmlToDocManualFileConverter();
	}

	@Override
	public File convert(File input) throws Exception {
		// try the open office converter first
		try {
			return openOfficeConverter.convert(input);
		}
		catch(Exception e) {
			log.info("Unable to convert html to doc via open office due to error: " + e.getMessage() + ".  Now trying manual conversion..");
			// fall back on manual conversion
			return manualConverter.convert(input);
		}
	}

	@Override
	public String getTargetMimeType() {
		// NOTE: either one is suitable to delegating to
		return openOfficeConverter.getTargetMimeType();
	}

	@Override
	public boolean isFileConvertable(File f) {
		// NOTE: either one is suitable to delegating to
		return openOfficeConverter.isFileConvertable(f);
	}
}
