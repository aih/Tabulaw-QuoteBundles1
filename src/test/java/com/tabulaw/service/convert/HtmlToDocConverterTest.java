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

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;

/**
 * @author jpk
 */
@Test
public class HtmlToDocConverterTest {

	public void testHtmlToDocConversion() throws Exception {
		URL url = getClass().getResource("htmldoc.html");
		File fin = new File(url.toURI());
		HtmlToDocConverter fc = new HtmlToDocConverter(makeOpenOfficeConnection());
		File fout = fc.convert(fin, null);
		Assert.assertNotNull(fout);
	}

	private OpenOfficeConnection makeOpenOfficeConnection() throws Exception {
		SocketOpenOfficeConnection ooc = new SocketOpenOfficeConnection();
		ooc.connect();
		return ooc;
	}
}
