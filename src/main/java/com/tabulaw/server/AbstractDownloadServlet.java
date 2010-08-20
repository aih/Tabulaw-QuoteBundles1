package com.tabulaw.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.model.User;
import com.tabulaw.service.convert.DataConverterDelegate;
import com.tabulaw.util.StringUtil;

public abstract class AbstractDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 842994896967308571L;
	protected static final String EXPORT_TEMPLATE_PATH = "export-templates/";

	protected static final Log log = LogFactory.getLog(DocDownloadServlet.class);

	public static final String defaultMimeType = "text/html";

	protected PersistContext pc;
	protected WebAppContext wc;
	protected UserContext userContext;
	protected String mimeType;

	protected abstract String getDownloadSource(HttpServletRequest req) throws ServletException, IOException;
	
	protected abstract String getSourceName(HttpServletRequest req); 

	@Override
	public void init() throws ServletException {
		super.init();
		wc = (WebAppContext) getServletContext().getAttribute(WebAppContext.KEY);
		pc = (PersistContext) getServletContext().getAttribute(PersistContext.KEY);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if(session == null) throw new ServletException("No user session");

		mimeType = req.getParameter("mimeType");
		if(StringUtil.isEmpty(mimeType)) mimeType = defaultMimeType;
		
		userContext = (UserContext) session.getAttribute(UserContext.KEY);
		if(userContext == null) throw new ServletException("No user context in user session");
		User user = userContext.getUser();
		if(user == null) throw new ServletException("No user in user context");

		boolean responseWritten = false;
		try {

			// convert the doc
			String sourceHtml = getDownloadSource(req);
			DataConverterDelegate fcd = (DataConverterDelegate) getServletContext().getAttribute(DataConverterDelegate.KEY);
			
			byte[] sourceBytes = sourceHtml.getBytes();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			String extension = fcd.convert(new ByteArrayInputStream(sourceBytes), "text/html", output, mimeType);

			resp.setContentType(mimeType);
			resp.setHeader("Content-disposition", "attachment; filename=" + getSourceName(req) + "." + extension);
			output.writeTo(resp.getOutputStream());
			responseWritten = true;
		}
		catch(Exception e) {
			String emsg = "Unable to convert:  due to error: " + e.getMessage();
			if(!responseWritten) resp.getWriter().write(emsg);
			throw new ServletException(emsg, e);
		}
		finally {
			resp.flushBuffer();
		}
	}

}
