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
public class HtmlToDocConverterTest {

	public void testHtmlToDocConversion() throws Exception {
		URL url = getClass().getResource("htmldoc.html");
		File fin = new File(url.toURI());
		HtmlToDocManualFileConverter fc = new HtmlToDocManualFileConverter();
		File fout = fc.convert(fin);
		Assert.assertNotNull(fout);
	}
}
