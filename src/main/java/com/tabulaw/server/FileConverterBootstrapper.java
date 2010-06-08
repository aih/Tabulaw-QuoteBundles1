/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.server;

import java.net.ConnectException;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.google.inject.Injector;
import com.tabulaw.service.convert.DocToHtmlConverter;
import com.tabulaw.service.convert.FileConverterDelegate;
import com.tabulaw.service.convert.HtmlToDocCompositeFileConverter;
import com.tabulaw.service.convert.IFileConverter;
import com.tabulaw.service.convert.TextToHtmlConverter;
import com.tabulaw.service.convert.ToHtmlPassThroughConverter;

/**
 * Bootstraps file converter impls.
 * @author jpk
 */
public class FileConverterBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(FileConverterBootstrapper.class);

	private static final String OPEN_OFFICE_CONNECTION_KEY = Integer.toString("OpenOfficeConnection".hashCode());

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		ArrayList<IFileConverter> converters = new ArrayList<IFileConverter>();

		// make open office connection
		SocketOpenOfficeConnection ooc = null;
		try {
			ooc = new SocketOpenOfficeConnection();
			ooc.connect();
			servletContext.setAttribute(OPEN_OFFICE_CONNECTION_KEY, ooc);
		}
		catch(ConnectException e) {
			log.error("Unable to establish socket connection to OpenOffice API: " + e.getMessage(), e);
		}

		// html pass through converter
		converters.add(new ToHtmlPassThroughConverter());

		// text to html converter
		converters.add(new TextToHtmlConverter());

		if(ooc != null) {
			// doc to html converter
			DocToHtmlConverter oofc = new DocToHtmlConverter(ooc);
			converters.add(oofc);

			// html to doc converter
			HtmlToDocCompositeFileConverter html2DocConverter = new HtmlToDocCompositeFileConverter(ooc);
			converters.add(html2DocConverter);
		}

		FileConverterDelegate converterDelegate =
				converters.size() == 0 ? null : new FileConverterDelegate(converters.toArray(new IFileConverter[0]));
		servletContext.setAttribute(FileConverterDelegate.KEY, converterDelegate);
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		servletContext.removeAttribute(FileConverterDelegate.KEY);

		OpenOfficeConnection ooc = (OpenOfficeConnection) servletContext.getAttribute(OPEN_OFFICE_CONNECTION_KEY);
		if(ooc != null) {
			ooc.disconnect();
			servletContext.removeAttribute(OPEN_OFFICE_CONNECTION_KEY);
		}
	}

}
