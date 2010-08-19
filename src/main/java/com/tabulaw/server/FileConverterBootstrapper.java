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
import com.tabulaw.service.convert.FileConverterDelegate;
import com.tabulaw.service.convert.IFileConverter;
import com.tabulaw.service.convert.SimpleHtmlToDocxFileConverter;
import com.tabulaw.service.convert.SimpleHtmlToRtfFileConverter;
import com.tabulaw.service.convert.TextToHtmlManualConverter;
import com.tabulaw.service.convert.ToHtmlPassThroughConverter;

/**
 * Bootstraps file converter impls.
 * 
 * @author jpk
 */
public class FileConverterBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(FileConverterBootstrapper.class);

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		log.debug("Starting up file converter bootstrapping..");
		
		ArrayList<IFileConverter> converters = new ArrayList<IFileConverter>();

		// html pass through converter
		converters.add(new ToHtmlPassThroughConverter());

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

		FileConverterDelegate converterDelegate = converters.size() == 0 ? null : new FileConverterDelegate(converters
				.toArray(new IFileConverter[converters.size()]));
		servletContext.setAttribute(FileConverterDelegate.KEY, converterDelegate);
		
		log.debug("File converter bootstrapping complete");
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		servletContext.removeAttribute(FileConverterDelegate.KEY);
	}
}
