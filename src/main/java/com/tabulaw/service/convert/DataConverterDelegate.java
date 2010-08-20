/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Delegates data conversion calls to the appropriate implementation.
 * <p>
 * Aggregates multiple converters under one roof allowing for a single point of
 * contact.
 * @author jpk
 */
public class DataConverterDelegate {

	private static final Log log = LogFactory.getLog(DataConverterDelegate.class);

	public static final String KEY = Integer.toString(DataConverterDelegate.class.getName().hashCode());

	private final IDataConverter[] converters;

	/**
	 * Constructor
	 * @param converters the required list of converters to employ <br>
	 *        <b>NOTE: </b>The order of this argument is important as these
	 *        converters are iterated starting at the beginning of the array
	 */
	public DataConverterDelegate(IDataConverter... converters) {
		super();
		if(converters == null || converters.length < 1) throw new IllegalArgumentException("No converters given");
		this.converters = converters;
	}
	/**
	 * Converts the given input stream to the given output stream file of type
	 * dictated by the given mime-types.
	 * @param input the input stream with content in sourceMimeType
	 * @param sourceMimeType the source mime-type
	 * @param output output stream to write conversion result
	 * @param targetMimeType the type to which to convert
	 * @return file extension for converted data
	 * @throws Exception When the conversion fails
	 */
	public String convert(InputStream input, String sourceMimeType, OutputStream output, String targetMimeType)
			throws Exception {
		if(input == null || sourceMimeType == null || output == null || targetMimeType == null)
			throw new NullPointerException();

		// get supported converters
		List<IDataConverter> scs = getSupportedConverters(sourceMimeType, targetMimeType);
		if(scs.isEmpty())
			throw new Exception("No converter(s) found for mime-types: " + sourceMimeType + ", " + targetMimeType);

		for(IDataConverter converter : scs) {
			try {
				converter.convert(input, output);
				return converter.getTargetFileExtension();
			}
			catch(Exception ex) {
				log.warn("Conversion problem", ex);
			}
		}
		return null;
	}
	
	/**
	 * Extracts the supported converters from the held list of all converters
	 * based on source and target content-types.
	 * @param sourceMimeType the source mime-type
	 * @param targetMimeType the mime-type to which to convert
	 * @return the supported converters which may be empty
	 */
	private List<IDataConverter> getSupportedConverters(String sourceMimeType, String targetMimeType) {
		ArrayList<IDataConverter> supportedConverters = new ArrayList<IDataConverter>();
		for(IDataConverter converter : converters) {
			if(converter.getSourceMimeType().equals(sourceMimeType) && converter.getTargetMimeType().equals(targetMimeType)) {
				supportedConverters.add(converter);
			}
		}
		return supportedConverters;
	}
}
