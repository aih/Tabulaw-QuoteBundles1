/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.server;

import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Injector;
import com.tabulaw.service.convert.ConverterHttpClient;
import com.tabulaw.service.convert.DataConverterDelegate;
import com.tabulaw.service.convert.ExternalConverter;
import com.tabulaw.service.convert.HtmlPassThroughConverter;
import com.tabulaw.service.convert.IDataConverter;
import com.tabulaw.service.convert.SimpleHtmlToDocxFileConverter;
import com.tabulaw.service.convert.SimpleHtmlToRtfFileConverter;
import com.tabulaw.service.convert.TextToHtmlManualConverter;

/**
 * Bootstraps file converter impls.
 * 
 * @author jpk
 */
public class DataConverterBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(DataConverterBootstrapper.class);

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.debug("Starting up file converter bootstrapping..");

		ArrayList<IDataConverter> converters = new ArrayList<IDataConverter>();

		// html pass through converter
		converters.add(new HtmlPassThroughConverter());

		// text to html converter
		converters.add(new TextToHtmlManualConverter());

		ConverterHttpClient httpclient = injector.getInstance(ConverterHttpClient.class);

		// doc to html 
		converters.add(new ExternalConverter("application/msword", "html", httpclient));
		
		// docx to html 
		converters.add(new ExternalConverter("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "html", httpclient));

		// html to docx
		converters.add(new SimpleHtmlToDocxFileConverter());
		//converters.add(new ExternalConverter("text/html", "docx", httpclient));  //until buying of Aspose license

		// html to doc
		converters.add(new ExternalConverter("text/html", "doc", httpclient));

		// html to rtf
		converters.add(new SimpleHtmlToRtfFileConverter());
		//converters.add(new ExternalConverter("text/html", "rtf", httpclient)); //until buying of Aspose license

		DataConverterDelegate converterDelegate = converters.size() == 0 ? null : new DataConverterDelegate(converters
				.toArray(new IDataConverter[converters.size()]));
		servletContext.setAttribute(DataConverterDelegate.KEY, converterDelegate);

		servletContext.setAttribute(ConverterHttpClient.KEY, httpclient);

		log.debug("File converter bootstrapping complete");
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		servletContext.removeAttribute(DataConverterDelegate.KEY);
		ConverterHttpClient httpclient = (ConverterHttpClient) servletContext.getAttribute(ConverterHttpClient.KEY);
		httpclient.shutdown();
	}
}
