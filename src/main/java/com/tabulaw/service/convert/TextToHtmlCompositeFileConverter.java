/**
 * The Logic Lab
 * @author jpk
 * @since Jun 10, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;

/**
 * Composite file converter for text to html conversion employing (in listed
 * order) the following delegates: <br>
 * <ul>
 * <li>{@link TextToHtmlOpenOfficeFileConverter}
 * <li>{@link TextToHtmlManualConverter}
 * </ul>
 * @author jpk
 */
public class TextToHtmlCompositeFileConverter implements IFileConverter {
	
	private static final Log log = LogFactory.getLog(TextToHtmlCompositeFileConverter.class);
	
	private final TextToHtmlOpenOfficeFileConverter openOffice;
	
	private final TextToHtmlManualConverter manual;

	/**
	 * Constructor
	 * @param ooc optional
	 */
	public TextToHtmlCompositeFileConverter(OpenOfficeConnection ooc) {
		super();
		this.openOffice = ooc == null ? null : new TextToHtmlOpenOfficeFileConverter(ooc);
		this.manual = new TextToHtmlManualConverter();
	}

	@Override
	public File convert(File input) throws Exception {
		if(openOffice != null) {
			try {
				return openOffice.convert(input);
			}
			catch(Exception e) {
				if(log.isInfoEnabled()) log.info("Unable to convert text to html via open office due to error: " + e.getMessage() + ".  Now trying manual conversion..");
				// fall through
			}
		}
		return manual.convert(input);
	}

	@Override
	public String getTargetMimeType() {
		return manual.getTargetMimeType();
	}

	@Override
	public boolean isFileConvertable(File f) {
		return manual.isFileConvertable(f);
	}

}
