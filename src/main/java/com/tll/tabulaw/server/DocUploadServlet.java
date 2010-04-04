/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tll.tabulaw.server;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.tll.tabulaw.server.convert.DocToHtmlFileConverter;
import com.tll.tabulaw.server.convert.IFileConverter;
import com.tll.tabulaw.server.convert.PassthoughFileConverter;
import com.tll.tabulaw.server.convert.TextToHtmlFileConverter;

/**
 * Saves uploaded documents.
 * @author jpk
 */
public class DocUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 4089402890142022345L;

	static IFileConverter resolveConverter(List<FileItem> items) {
		// for now, just do it by filename
		// also, assume we have a single file item
		FileItem item = items.get(0);
		String filename = item.getName();

		if(filename.endsWith(".doc")) {
			return new DocToHtmlFileConverter();
		}
		else if(filename.endsWith(".txt") || filename.endsWith(".rtf")) {
			return new TextToHtmlFileConverter();
		}

		// as a fall-back, provide a pass through converter
		return new PassthoughFileConverter();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if(ServletFileUpload.isMultipartContent(req)) {

			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(req);
				IFileConverter converter = resolveConverter(items);

				File f = null;
				for(FileItem item : items) {
					if(!item.isFormField()) {
						String filename = item.getName();
						if(filename != null) {
							filename = FilenameUtils.getName(filename);
						}
						File cproot = new File(getClass().getClassLoader().getResource("").toURI());
						f = new File(cproot.getPath() + File.separator + filename);
						try {
							item.write(f);
						}
						catch(Exception e) {
							throw new ServletException("Unable to write uploaded file to disk: " + e.getMessage(), e);
						}
						break;
					}
				}
				if(f == null) throw new ServletException("No uploaded file content detected.");
				File converted = converter.convert(f);
				resp.setStatus(HttpServletResponse.SC_CREATED);
				
				StringBuilder sb = new StringBuilder();
				sb.append("docTitle:");
				sb.append("TODO");
				sb.append("|docDate:");
				sb.append(DocUtils.dateAsString(new Date()));
				sb.append("|docHash:");
				sb.append(converted.getName());
				
				resp.getWriter().print(sb.toString());
				resp.flushBuffer();
			}
			catch(Exception e) {
				//throw new ServletException(, e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to digest uploaded file: " + e.getMessage());
			}
		}
		else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported content type");
		}
	}

}
