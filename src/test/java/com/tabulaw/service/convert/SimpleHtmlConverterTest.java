package com.tabulaw.service.convert;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.testng.annotations.Test;

@Test
public class SimpleHtmlConverterTest {

	public void testHtmlToDocConversion() throws Exception {
		URL url = getClass().getResource("85.html");
		File fin = new File(url.toURI());
		AbstractSimpleHtmlConvertor html2DocConverter = new SimpleHtmlToDocxFileConverter();
		File fout = html2DocConverter.convert(fin);
		Assert.assertNotNull(fout);
	}

}
