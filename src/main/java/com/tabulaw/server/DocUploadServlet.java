/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.server.convert.IFileConverter;
import com.tabulaw.server.convert.IFileConverter.FileType;
import com.tabulaw.util.StringUtil;

/**
 * Saves uploaded documents.
 * @author jpk
 */
public class DocUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 4089402890142022345L;

	private static final Log log = LogFactory.getLog(DocUploadServlet.class);

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
				IFileConverter fconverter =
						(IFileConverter) req.getSession().getServletContext().getAttribute(
								FileConverterBootstrapper.FILE_CONVERTER_KEY);
				if(fconverter == null) {
					throw new Exception("No file converters found.");
				}

				StringBuilder sb = new StringBuilder();

				List<FileItem> items = upload.parseRequest(req);

				File f = null;
				int numSuccessful = 0;
				for(FileItem item : items) {
					if(!item.isFormField()) {
						String filename = item.getName();
						if(StringUtil.isEmpty(filename)) continue;
						filename = FilenameUtils.getName(filename);
						f = DocUtils.getDocRef(filename);
						try {
							item.write(f);
						}
						catch(Exception e) {
							throw new Exception("Unable to write uploaded file to disk: " + e.getMessage(), e);
						}
						numSuccessful++;

						// convert to html
						File fconverted = fconverter.convert(f, FileType.HTML);
						
						// serialize the just created html doc file
						// NOTE: the doc hash for uploaded docs is just hash of the non-path filename given
						String docHash = fconverted.getName();
						String docTitle = docHash; // for now
						Date docDate = new Date();
						DocRef mDoc = EntityFactory.get().buildDoc(docTitle, docHash, docDate);
						String sdoc = DocUtils.serializeDocument(mDoc);

						// localize converted doc html
						{
							String htmlContent = FileUtils.readFileToString(fconverted, "UTF-8");
							StringBuilder docsb = new StringBuilder(htmlContent);
							DocUtils.localizeDoc(docsb, docTitle);
							htmlContent = docsb.toString();
							
							// write to disk
							if(!fconverted.delete()) {
								throw new Exception("Unable to delete converted html file");
							}
							FileUtils.writeStringToFile(fconverted, sdoc + htmlContent);
							fconverted = null;
						}
						
						if(sb.length() == 0) {
							sb.append("[START]");
						}
						if(numSuccessful > 1) {
							sb.append(',');
						}
						sb.append(sdoc);
					}
				}
				if(sb.length() > 0) {
					// we have at least one uploaded doc (success)
					sb.append("[END]");
				}

				String response = sb.toString();

				resp.setStatus(HttpServletResponse.SC_CREATED);
				resp.getWriter().print(response);
				resp.flushBuffer();
			}
			catch(Exception e) {
				String emsg = "Unable to digest uploaded files: " + e.getMessage();
				log.error(emsg, e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, emsg);
			}
		}
		else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported content type");
		}
	}

}