/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.server.convert;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.tabulaw.server.convert.IFileConverter;
import com.tabulaw.server.convert.OpenOfficeFileConverter;
import com.tabulaw.server.convert.IFileConverter.FileType;

/**
 * @author jpk
 */
@Test
public class OpenOfficeFileConverterTest {

	public void testDocToHtmlConversion() throws Exception {
		URL url = getClass().getResource("");
		String canonicalname = url.getPath() + "test.doc";
		File fin = new File(canonicalname);
		IFileConverter fc = OpenOfficeFileConverter.create();
		File fout = fc.convert(fin, FileType.HTML);
		Assert.assertNotNull(fout);
	}
	
	public void testDocxToHtmlConversion() throws Exception {
		
	}
}
