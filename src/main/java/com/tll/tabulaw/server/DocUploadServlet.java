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

import com.tll.tabulaw.server.convert.IFileConverter;
import com.tll.tabulaw.server.convert.IFileConverter.FileType;

/**
 * Saves uploaded documents.
 * @author jpk
 */
public class DocUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 4089402890142022345L;

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
				
				// convert to html
				IFileConverter fconverter =
						(IFileConverter) req.getSession().getServletContext().getAttribute(
								FileConverterBootstrapper.FILE_CONVERTER_KEY);
				File fout = fconverter.convert(f, FileType.HTML);
				
				resp.setStatus(HttpServletResponse.SC_CREATED);
				
				StringBuilder sb = new StringBuilder();
				sb.append("docTitle:");
				// TODO ideally, get the title by looking inside file contents
				sb.append(fout.getName());
				sb.append("|docDate:");
				sb.append(DocUtils.dateAsString(new Date()));
				sb.append("|docHash:");
				sb.append(fout.getName());
				
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
