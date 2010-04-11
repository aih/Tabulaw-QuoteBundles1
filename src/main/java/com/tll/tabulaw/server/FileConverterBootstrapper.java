/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tll.tabulaw.server;

import java.net.ConnectException;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.google.inject.Injector;
import com.tll.server.IBootstrapHandler;
import com.tll.tabulaw.server.convert.FileConverterDelegate;
import com.tll.tabulaw.server.convert.IFileConverter;
import com.tll.tabulaw.server.convert.OpenOfficeFileConverter;

/**
 * Boots up needed resources to do uploaded doc to html conversions.
 * @author jpk
 */
public class FileConverterBootstrapper implements IBootstrapHandler {

	private static final Log log = LogFactory.getLog(FileConverterBootstrapper.class);

	public static final String FILE_CONVERTER_KEY = Integer.toString(IFileConverter.class.getName().hashCode());

	private static final String OPEN_OFFICE_CONNECTION_KEY = Integer.toString("OpenOfficeConnection".hashCode());

	@Override
	public void startup(Injector injector, ServletContext servletContext) {
		ArrayList<IFileConverter> converters = new ArrayList<IFileConverter>();

		try {
			OpenOfficeFileConverter oofc = OpenOfficeFileConverter.create();
			servletContext.setAttribute(OPEN_OFFICE_CONNECTION_KEY, oofc.getOpenOfficeConnection());
			converters.add(oofc);
		}
		catch(ConnectException e) {
			log.error("Unable to create open office file converter: " + e.getMessage(), e);
		}

		IFileConverter converterDelegate = converters.size() == 0 ? null : new FileConverterDelegate(converters);
		servletContext.setAttribute(FILE_CONVERTER_KEY, converterDelegate);
	}

	@Override
	public void shutdown(ServletContext servletContext) {
		servletContext.removeAttribute(FILE_CONVERTER_KEY);

		OpenOfficeConnection ooc = (OpenOfficeConnection) servletContext.getAttribute(OPEN_OFFICE_CONNECTION_KEY);
		if(ooc != null) {
			ooc.disconnect();
			servletContext.removeAttribute(OPEN_OFFICE_CONNECTION_KEY);
		}
	}

}
