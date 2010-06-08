/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.common.model.DocContent;
import com.tabulaw.common.model.User;
import com.tabulaw.service.DocUtils;
import com.tabulaw.service.convert.FileConverterDelegate;
import com.tabulaw.util.StringUtil;

/**
 * Streams doc content for download.
 * <p>
 * The doc may be converted to a desired format before download.
 * @author jpk
 */
public class DocDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 7225115223788909217L;

	private static final Log log = LogFactory.getLog(DocDownloadServlet.class);

	//private static final String EMAIL_TEMPLATE_DOC_EXPORT = "doc-export";

	public static final String defaultMimeType = "text/html";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if(session == null) throw new ServletException("No user session");

		String docId = req.getParameter("docId");
		String mimeType = req.getParameter("mimeType");

		if(StringUtil.isEmpty(docId)) throw new ServletException("No doc id specified");
		if(StringUtil.isEmpty(mimeType)) mimeType = defaultMimeType;
		
		if(log.isInfoEnabled()) log.info("Processing doc download request: docId: " + docId + ", mime-type: " + mimeType);

		FileConverterDelegate fcd =
				(FileConverterDelegate) getServletContext().getAttribute(FileConverterDelegate.KEY);
		PersistContext pc = (PersistContext) getServletContext().getAttribute(PersistContext.KEY);

		UserContext userContext = (UserContext) session.getAttribute(UserContext.KEY);
		if(userContext == null) throw new ServletException("No user context in user session");
		User user = userContext.getUser();
		if(user == null) throw new ServletException("No user in user context");

		// load the doc
		DocContent doc = pc.getUserDataService().getDocContent(docId);
		File fdoc, fconverted;
		fdoc = fconverted = null;
		boolean responseWritten = false;
		try {

			// convert the doc
			fdoc = DocUtils.docContentsToFile(doc);
			fconverted = fcd.convert(fdoc, mimeType);

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
			
			// stream the doc contents
			ServletOutputStream sos = resp.getOutputStream();
			byte[] fbytes = FileUtils.readFileToByteArray(fconverted);
			if(fbytes == null || fbytes.length < 1) throw new Exception("No doc content read");
			sos.write(fbytes);
			responseWritten = true;
			//resp.setContentLength(fbytes.length);
			//resp.setContentType(mimeType);
			resp.setHeader("Content-disposition", "attachment; filename=" + fconverted.getName());
		}
		catch(Exception e) {
			String emsg = "Unable to convert: " + doc + " due to error: " + e.getMessage();
			if(!responseWritten) resp.getWriter().write(emsg);
			throw new ServletException(emsg, e);
		}
		finally {
			resp.flushBuffer();

			// clean up
			if(fdoc != null) fdoc.delete();
			if(fconverted != null) fconverted.delete();
			
		}
	}
}
