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
public class DocToHtmlConverterTest {

	public void testDocToHtmlConversion() throws Exception {
		URL url = getClass().getResource("test.doc");
		File fin = new File(url.toURI());
		DocToHtmlConverter fc = new DocToHtmlConverter();
		File fout = fc.convert(fin);
		Assert.assertNotNull(fout);
	}

	/**
	 * This conversion is not supported by open office api currently.
	 * @throws Exception
	 */
	@Test(enabled = false)
	public void testDocxToHtmlConversion() throws Exception {
		// TODO
	}
}
