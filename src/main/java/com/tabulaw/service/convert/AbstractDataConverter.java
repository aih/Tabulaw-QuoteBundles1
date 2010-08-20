/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tabulaw.service.convert;

import com.tabulaw.service.DocUtils;

/**
 * @author jpk
 */
abstract class AbstractDataConverter implements IDataConverter {
	
	@Override
	public String getTargetMimeType() {
		return DocUtils.getMimeTypeFromFileExt(getTargetFileExtension());
	}
}
