/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.service.convert;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;

/**
 * Converts *.doc files to HTML files using the open office api.
 * @see "http://www.artofsolving.com/opensource/jodconverter"
 * @author jpk
 */
public abstract class AbstractOpenOfficeFileConverter extends AbstractFileConverter {

	protected final OpenOfficeConnection ooc;

	/**
	 * Constructor
	 * @param ooc required
	 */
	public AbstractOpenOfficeFileConverter(OpenOfficeConnection ooc) {
		super();
		if(ooc == null) throw new NullPointerException();
		this.ooc = ooc;
	}
}
