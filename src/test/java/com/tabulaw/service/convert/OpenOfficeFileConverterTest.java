/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.testng.annotations.Test;

/**
 * @author jpk
 */
@Test
public class OpenOfficeFileConverterTest {

	public void testDocToHtmlConversion() throws Exception {
		URL url = getClass().getResource("");
		String canonicalname = url.getPath() + "test.doc";
		File fin = new File(canonicalname);
		IToHtmlConverter fc = OpenOfficeFileConverter.create();
		File fout = fc.convert(fin, null);
		Assert.assertNotNull(fout);
	}
	
	public void testDocxToHtmlConversion() throws Exception {
		
	}
}
