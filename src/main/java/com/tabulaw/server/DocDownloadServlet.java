/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.common.model.DocContent;
import com.tabulaw.service.DocUtils;
import com.tabulaw.util.StringUtil;

/**
 * Streams doc content for download.
 * <p>
 * The doc may be converted to a desired format before download.
 * @author jpk
 */
public class DocDownloadServlet extends AbstractDownloadServlet {

	private static final long serialVersionUID = 7225115223788909217L;

	private static final Log log = LogFactory.getLog(DocDownloadServlet.class);

	//private static final String EMAIL_TEMPLATE_DOC_EXPORT = "doc-export";

	public static final String defaultMimeType = "text/html";

	@Override
	protected File getContentFile(HttpServletRequest req) throws ServletException, IOException {
		String docId = req.getParameter("docId");
		if(StringUtil.isEmpty(docId)) throw new ServletException("No doc id specified");
		if(log.isInfoEnabled()) log.info("Processing doc download request: docId: " + docId + ", mime-type: " + mimeType);

		DocContent doc = pc.getUserDataService().getDocContent(docId);
		File fdoc = DocUtils.docContentsToFile(doc);
		// email the doc
		/*
		final MailManager mailManager = pc.getMailManager();
		final MailRouting mr = mailManager.buildAppSenderMailRouting(user.getEmailAddress());
		final IMailContext mailContext = mailManager.buildTextTemplateContext(mr, EMAIL_TEMPLATE_DOC_EXPORT, null);
		FileDataSource fds = new FileDataSource(fconverted);
		mailContext.addAttachment(fconverted.getName(), fds);
		mailManager.sendEmail(mailContext);
		// status.addMsg("Document emailed.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		*/
		
		return fdoc ;
	}
}
