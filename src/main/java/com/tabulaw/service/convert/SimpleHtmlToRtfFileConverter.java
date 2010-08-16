/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 15, 2010
 */
package com.tabulaw.service.convert;

import com.tabulaw.service.convert.simplehtmlconverter.Constants;
import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.RtfDocumentContext;

/**
 * HTML to RTF via simplehtmlconverter api.
 * @author jopaki
 */
public class SimpleHtmlToRtfFileConverter extends AbstractSimpleHtmlConvertor {


	@Override
	protected String getFileExtension() {
		return "rtf";
	}

	@Override
	public String getTargetMimeType() {
		return Constants.RTF_MIME_TYPE;
	}

        @Override
        IDocumentContext createDocumentContext() {
                return new RtfDocumentContext();
        }
}
