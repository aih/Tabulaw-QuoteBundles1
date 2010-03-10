/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tll.tabulaw.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.tll.util.ClassUtil;

/**
 * DocViewServlet
 * @author jpk
 */
public class DocViewServlet extends HttpServlet {

	private static final long serialVersionUID = -2462373423575492047L;
	
	private static final String jsScriptCallbackBlock;
	
	private static final String cssHighightStylesBlock;
	
	static {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<script type=\"text/javascript\">");
		sb.append("window.onload = function(){window.parent.onFrameLoaded(document);}");
		sb.append("</script>");
		sb.append("</body>");
		jsScriptCallbackBlock = sb.toString();
		
		sb.setLength(0);
		sb.append("<style type=\"text/css\">.highlight{background-color:yellow;}</style>");
		sb.append("</head>");
		cssHighightStylesBlock = sb.toString();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// get the doc id
		String docId = req.getParameter("docId");
		String fname;
		switch(Integer.parseInt(docId)) {
			case 1: // Buckley v. Valeo
				fname = "Buckley-v-Valeo.htm";
				break;
			case 2:
				fname = "CitizensUnited-v-FEC.htm";
				break;
			case 3:
				fname = "FirstNatl-v-Belotti.htm";
				break;
			case 4:
				fname = "McConnell-v-FEC.htm";
				break;
			case 5:
				fname = "Times-v-Sullivan.htm";
				break;
			default:
				throw new IllegalStateException();
		}
		
		File f;
		try {
			f = new File(ClassUtil.getResource(fname).toURI());
		}
		catch(Exception e) {
			throw new ServletException("Unable to read doc file: " + e.getMessage(), e);
		}
		String fstr = FileUtils.readFileToString(f);
		// inject js callback hook in window.onload
		fstr = fstr.replace("</head>", cssHighightStylesBlock).replace("</body>", jsScriptCallbackBlock);
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		writer.write(fstr);
		writer.flush();
	}

}
