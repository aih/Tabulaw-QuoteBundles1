/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Sole {@link IFileConverter} for use by clients.
 * <p>
 * Aggregates multiple converters under one roof allowing for a simple client
 * API.
 * @author jpk
 */
public class FileConverterDelegate implements IFileConverter {

	private final IFileConverter[] converters;

	/**
	 * Constructor
	 * @param converters the required list of converters to employ <br>
	 *        <b>NOTE: </b>The order of this argument is important as these
	 *        converters are iterated starting at the beginning of the array
	 */
	public FileConverterDelegate(IFileConverter... converters) {
		super();
		if(converters == null || converters.length < 1) throw new IllegalArgumentException("No converters given");
		this.converters = converters;
	}

	@Override
	public boolean supportsContentType(String contentType) {
		for(IFileConverter c : converters) {
			if(c.supportsContentType(contentType)) return true;
		}
		return false;
	}

	@Override
	public File convert(File input, String contentType) throws Exception {

		// get supported converters
		List<IFileConverter> scs = getSupportedConverters(contentType);
		if(scs.size() < 1) throw new Exception("No converter found for content-type: " + contentType);

		// now iterate over these supported converters where ones closer to head
		// are given prioroty over ones that are not
		File fout = null;
		Exception ex = null;
		for(IFileConverter c : scs) {
			try {
				fout = c.convert(input, contentType);
				if(fout != null) break;
			}
			catch(Exception e) {
				ex = e;
			}
		}

		if(fout == null) {
			if(ex == null) {
				ex = new Exception("Can't convert file: " + input);
			}
			throw ex;
		}

		return fout;
	}

	/**
	 * Extracts the supported converters from the held list of all converters
	 * based on a content-type.
	 * @param contentType
	 * @return the supported converters which may be empty
	 */
	private List<IFileConverter> getSupportedConverters(String contentType) {
		ArrayList<IFileConverter> supportedConverters = new ArrayList<IFileConverter>(converters.length);
		for(IFileConverter c : converters) {
			if(c.supportsContentType(contentType)) {
				supportedConverters.add(c);
			}
		}
		return supportedConverters;
	}

}
