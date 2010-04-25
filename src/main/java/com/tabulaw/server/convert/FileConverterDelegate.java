/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.server.convert;

import java.io.File;

/**
 * Sole {@link IFileConverter} for use by clients.
 * @author jpk
 */
public class FileConverterDelegate implements IFileConverter {

	private final IFileConverter[] converters;

	/**
	 * Constructor
	 * @param converters the required list of converters to employ
	 */
	public FileConverterDelegate(IFileConverter... converters) {
		super();
		if(converters == null || converters.length < 1) throw new IllegalArgumentException("No converters given");
		this.converters = converters;
	}

	@Override
	public File convert(File input, FileType outputFileType) throws Exception {
		File fout = null;
		Exception ex = null;
		for(IFileConverter converter : converters) {
			try {
				fout = converter.convert(input, outputFileType);
				if(fout != null) break;
			}
			catch(Exception e) {
				ex = e;
			}
		}

		if(ex != null)
			throw new Exception("Can' convert input file: '" + input + "'.  Error: " + ex.getMessage(), ex);
		else if(fout == null) 
			throw new Exception("Can't convert input file: " + input);

		return fout;
	}

}
