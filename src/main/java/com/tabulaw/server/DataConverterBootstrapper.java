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
import com.tabulaw.service.convert.DataConverterDelegate;
import com.tabulaw.service.convert.IDataConverter;
import com.tabulaw.service.convert.SimpleHtmlToDocxFileConverter;
import com.tabulaw.service.convert.SimpleHtmlToRtfFileConverter;
import com.tabulaw.service.convert.TextToHtmlManualConverter;
import com.tabulaw.service.convert.HtmlPassThroughConverter;

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

		// out of commission due to removal of open office api
		// TODO fix or just support *.docx ???
		// doc to html converter
		//DocToHtmlConverter oofc = new DocToHtmlConverter();
		//converters.add(oofc);
		
		// html to docx
		converters.add(new SimpleHtmlToDocxFileConverter());

		// html to rtf
		converters.add(new SimpleHtmlToRtfFileConverter());

		DataConverterDelegate converterDelegate = converters.size() == 0 ? null : new DataConverterDelegate(converters
				.toArray(new IDataConverter[converters.size()]));
		servletContext.setAttribute(DataConverterDelegate.KEY, converterDelegate);
		
		log.debug("File converter bootstrapping complete");
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		servletContext.removeAttribute(DataConverterDelegate.KEY);
	}
}
