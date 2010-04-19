/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.tabulaw.util.ClassUtil;

/**
 * DocViewServlet
 * @author jpk
 */
public class DocViewServlet extends HttpServlet {

	private static final long serialVersionUID = -2462373423575492047L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String docId = req.getParameter("id");
		if(docId == null) {
			throw new ServletException("No doc id specified.");
		}

		File f;
		try {
			f = new File(ClassUtil.getResource(docId).toURI());
		}
		catch(Exception e) {
			throw new ServletException("Unable to read doc file: " + e.getMessage(), e);
		}

		String fstr = FileUtils.readFileToString(f, "UTF-8");
		
		// strip out serialized first line
		int index = fstr.indexOf('\n');
		if(index != -1) fstr = fstr.substring(index + 1);
		
		StringBuilder sb = new StringBuilder(fstr);

		// inject js callback hook in window.onload
		DocUtils.localizeDoc(sb, null);
		
		fstr = sb.toString();

		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		writer.write(fstr);
		writer.flush();
	}

}
