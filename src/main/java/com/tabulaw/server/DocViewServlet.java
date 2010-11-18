/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tabulaw.model.DocContent;

/**
 * DocViewServlet
 * @author jpk
 */
public class DocViewServlet extends HttpServlet {

	private static final long serialVersionUID = -2462373423575492047L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String docId = req.getParameter("id");
		if(docId == null) throw new ServletException("No doc id specified.");

		PersistContext pc = (PersistContext) getServletContext().getAttribute(PersistContext.KEY);
		DocContent doc = pc.getUserDataService().getDocContent(docId);
		String htmlContent = doc.getHtmlContent();

		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		writer.write(htmlContent);
		writer.flush();
	}

}
