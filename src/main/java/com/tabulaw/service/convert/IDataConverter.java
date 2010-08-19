package com.tabulaw.service.convert;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Converts a data in one format to a another format (HTML -> DOCX, TXT -> HTML, etc)
 * @author ymakhno
 */
public interface IDataConverter {
	
	/**
	 * @return The mime-type to which this converter converts.
	 */
	String getTargetMimeType();

	/**
	 * @return The mime-type of source
	 */
	String getSourceMimeType();
	
	/**
	 * @return extension of created data if data would be saved in file  
	 */
	String getTargetFileExtension();
		
	/**
	 * Convert the given input stream in source mime type 
	 * to output stream in target mime type
	 * @param input 
	 * @param output
	 * @throws Exception When the conversion fails
	 */
	void convert(InputStream input, OutputStream output) throws Exception;

}
