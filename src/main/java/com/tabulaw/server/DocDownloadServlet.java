/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.util.StringUtil;

/**
 * Streams doc content for download.
 * <p>
 * The doc may be converted to a desired format before download.
 * @author jpk
 */
public class DocDownloadServlet extends AbstractDownloadServlet {

	private static final long serialVersionUID = 7225115223788909217L;

	@Override
	protected String getDownloadSource(HttpServletRequest req) throws ServletException, IOException {
		String docId = req.getParameter("docId");
		if(StringUtil.isEmpty(docId)) throw new ServletException("No doc id specified");
		if(log.isInfoEnabled()) log.info("Processing doc download request: docId: " + docId + ", mime-type: " + mimeType);

		DocContent doc = pc.getUserDataService().getDocContent(docId);
		
		return doc.getHtmlContent();
	}

	@Override
	protected String getSourceName(HttpServletRequest req) throws ServletException {
		String docId = req.getParameter("docId");
		if(StringUtil.isEmpty(docId)) throw new ServletException("No doc id specified");
		DocRef doc = pc.getUserDataService().getDoc(docId);
		return doc.getTitle();
	}	
}
