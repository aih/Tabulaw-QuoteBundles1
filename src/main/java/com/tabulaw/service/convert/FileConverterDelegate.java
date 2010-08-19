/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Sole {@link IFileConverter} for use by clients.
 * <p>
 * Aggregates multiple converters under one roof allowing for a simple client
 * API.
 * @author jpk
 */
public class FileConverterDelegate {
	private static final Log log = LogFactory.getLog(FileConverterDelegate.class);
	
	public static final String KEY = Integer.toString(FileConverterDelegate.class.getName().hashCode());

	private final IDataConverter[] converters;

	/**
	 * Constructor
	 * @param converters the required list of converters to employ <br>
	 *        <b>NOTE: </b>The order of this argument is important as these
	 *        converters are iterated starting at the beginning of the array
	 */
	public FileConverterDelegate(IDataConverter... converters) {
		super();
		if(converters == null || converters.length < 1) throw new IllegalArgumentException("No converters given");
		this.converters = converters;
	}

	/**
	 * Converts the given input file to a newly created file of type dictated by
	 * the given mime-type.
	 * @param input
	 * @param targetMimeType the type to which to convert
	 * @return new converted file ref
	 * @throws Exception When the conversion fails
	 */
	public File convert(File input, String targetMimeType) throws Exception {
		if(input == null || targetMimeType == null) throw new NullPointerException();

		// get supported converters
		List<IFileConverter> scs = getSupportedConverters(input, targetMimeType);
		if(scs.size() < 1) throw new Exception("No converter(s) found for input file: " + input.getName());

		// now iterate over these supported converters where ones closer to head
		// are given priority over ones that are not
		File fout = null;
		Exception ex = null;
		for(IFileConverter c : scs) {
			try {
				fout = c.convert(input);
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
	 * Converts the given input stream to the given output stream file of type dictated by
	 * the given mime-types.
	 * @param input the input stream with content in sourceMimeType
	 * @param sourceMimeType the source mime-type
	 * @param output output stream to write conversion result
	 * @param targetMimeType the type to which to convert
	 * @return file extension for converted data
	 * @throws Exception When the conversion fails
	 */
	public String convert(InputStream input, String sourceMimeType, OutputStream output, String targetMimeType) throws Exception {
		if(input == null || sourceMimeType == null || output == null || targetMimeType == null) throw new NullPointerException();

		// get supported converters
		List<IDataConverter> scs = getSupportedConverters(sourceMimeType, targetMimeType);
		if(scs.isEmpty()) throw new Exception("No converter(s) found for mime-types: " + sourceMimeType + ", " + targetMimeType);
		
		for (IDataConverter converter : scs) {
			try {
				converter.convert(input, output);
				return converter.getTargetFileExtension();
			} catch (Exception ex) {
				log.warn("Conversion problem", ex);
			}			
		}
		return null;
	}

	/**
	 * Extracts the supported converters from the held list of all converters
	 * based on a content-type.
	 * @param f file ref for which to get the supported file converters
	 * @param targetMimeType the mime-type to which to convert
	 * @return the supported converters which may be empty
	 */
	private List<IFileConverter> getSupportedConverters(File f, String targetMimeType) {
		ArrayList<IFileConverter> supportedConverters = new ArrayList<IFileConverter>(converters.length);
		for(IDataConverter dataConverter : converters) {
			if (! (dataConverter instanceof IFileConverter)) {
				continue;
			}
			IFileConverter c = (IFileConverter) dataConverter;
			if(c.isFileConvertable(f) && c.getTargetMimeType().equals(targetMimeType)) {
				supportedConverters.add(c);
			}
		}
		return supportedConverters;
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
			if(converter.getSourceMimeType().equals(sourceMimeType) && 
					converter.getTargetMimeType().equals(targetMimeType)) {
				supportedConverters.add(converter);
			}
		}
		return supportedConverters;
	}
}
