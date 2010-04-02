/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tll.tabulaw.server.convert;

import java.io.File;

/**
 * A no-op file converter that simply returns the input.
 * @author jpk
 */
public class PassthoughFileConverter implements IFileConverter {

	@Override
	public File convert(File input) {
		return input;
	}

}
