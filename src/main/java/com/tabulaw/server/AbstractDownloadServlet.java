package com.tabulaw.server;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.common.model.User;
import com.tabulaw.service.convert.FileConverterDelegate;
import com.tabulaw.util.StringUtil;
import eu.medsea.mimeutil.MimeUtil;

public abstract class AbstractDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 842994896967308571L;
	protected static final String EXPORT_TEMPLATE_PATH = "export-templates/";

	protected static final Log log = LogFactory
			.getLog(DocDownloadServlet.class);

	public static final String defaultMimeType = "text/html";

	protected PersistContext pc;
	protected UserContext userContext;
	protected String mimeType;

	protected abstract File getContentFile(HttpServletRequest req)
			throws ServletException, IOException;

	private String getMimeType(File f) {
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(f);
		return (mimeTypes.toString());

	}

	@Override
	public void init() throws ServletException {
		super.init();
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("No user session");

		mimeType = req.getParameter("mimeType");

		if (StringUtil.isEmpty(mimeType))
			mimeType = defaultMimeType;
		FileConverterDelegate fcd = (FileConverterDelegate) getServletContext()
				.getAttribute(FileConverterDelegate.KEY);

		pc = (PersistContext) getServletContext().getAttribute(
				PersistContext.KEY);

		userContext = (UserContext) session.getAttribute(UserContext.KEY);
		if (userContext == null)
			throw new ServletException("No user context in user session");
		User user = userContext.getUser();
		if (user == null)
			throw new ServletException("No user in user context");

		File fdoc, fconverted;
		fdoc = fconverted = null;
		boolean responseWritten = false;
		try {

			// convert the doc
			fdoc = getContentFile(req);
			fconverted = fcd.convert(fdoc, mimeType);

			// stream the doc contents
			ServletOutputStream sos = resp.getOutputStream();
			byte[] fbytes = FileUtils.readFileToByteArray(fconverted);
			if (fbytes == null || fbytes.length < 1)
				throw new Exception("No doc content read");
			//resp.setContentLength(fbytes.length);
			resp.setContentType(getMimeType(fconverted));
			resp.setHeader("Content-disposition", "attachment; filename="
					+ fconverted.getName());
			sos.write(fbytes);
			responseWritten = true;
		} catch (Exception e) {
			String emsg = "Unable to convert:  due to error: " + e.getMessage();
			if (!responseWritten)
				resp.getWriter().write(emsg);
			throw new ServletException(emsg, e);
		} finally {
			resp.flushBuffer();

			// clean up
			if (fdoc != null)
				fdoc.delete();
			if (fconverted != null)
				fconverted.delete();
		}
	}

}
