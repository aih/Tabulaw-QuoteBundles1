/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tll.tabulaw.server;

import java.net.ConnectException;
import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.tll.tabulaw.server.convert.FileConverterDelegate;
import com.tll.tabulaw.server.convert.IFileConverter;
import com.tll.tabulaw.server.convert.OpenOfficeFileConverter;

/**
 * @author jpk
 */
public class FileConverterBootstrapper implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(FileConverterBootstrapper.class);
	
	public static final String FILE_CONVERTER_KEY = Integer.toString(IFileConverter.class.getName().hashCode());
	
	private static final String OPEN_OFFICE_CONNECTION_KEY = Integer.toString("OpenOfficeConnection".hashCode());
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		ArrayList<IFileConverter> converters = new ArrayList<IFileConverter>();
		
		try {
			OpenOfficeFileConverter oofc = OpenOfficeFileConverter.create();
			event.getServletContext().setAttribute(OPEN_OFFICE_CONNECTION_KEY, oofc.getOpenOfficeConnection());
			converters.add(oofc);
		}
		catch(ConnectException e) {
			log.error("Unable to create open office file converter: " + e.getMessage(), e);
		}
		
		IFileConverter converterDelegate = converters.size() == 0 ? null : new FileConverterDelegate(converters);
		event.getServletContext().setAttribute(FILE_CONVERTER_KEY, converterDelegate); 
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		event.getServletContext().removeAttribute(FILE_CONVERTER_KEY);
		
		OpenOfficeConnection ooc = (OpenOfficeConnection) event.getServletContext().getAttribute(OPEN_OFFICE_CONNECTION_KEY);
		if(ooc != null) {
			ooc.disconnect();
			event.getServletContext().removeAttribute(OPEN_OFFICE_CONNECTION_KEY);
		}
	}

}
