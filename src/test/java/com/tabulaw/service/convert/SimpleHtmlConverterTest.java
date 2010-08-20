package com.tabulaw.service.convert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.tabulaw.util.StringUtil;

@Test
public class SimpleHtmlConverterTest {

	public void testHtmlToDocConversion() throws Exception {
		URL url = getClass().getResource("85.html");
		File fin = new File(url.toURI());
		AbstractSimpleHtmlConvertor html2DocConverter = new SimpleHtmlToDocxFileConverter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		html2DocConverter.convert(new FileInputStream(fin), baos);
		String sconverted = new String(baos.toByteArray(), "UTF-8");
		
		Assert.assertFalse(StringUtil.isEmpty(sconverted));
		// TODO more thorough testing
	}

}
