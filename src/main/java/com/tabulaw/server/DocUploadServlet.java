/**
 * The Logic Lab
 * @author jpk
 * @since Feb 27, 2010
 */
package com.tabulaw.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.User;
import com.tabulaw.service.DocUtils;
import com.tabulaw.service.convert.DataConverterDelegate;
import com.tabulaw.service.entity.UserDataService;
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

			final PersistContext pc =
					(PersistContext) req.getSession(false).getServletContext().getAttribute(PersistContext.KEY);
			final UserContext uc = (UserContext) req.getSession(false).getAttribute(UserContext.KEY);
			UserDataService uds = pc.getUserDataService();
			User user = uc.getUser();

			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				DataConverterDelegate converterDelegate =
						(DataConverterDelegate) getServletContext().getAttribute(DataConverterDelegate.KEY);
				if(converterDelegate == null) throw new Exception("No file converter delegate found.");

				StringBuilder sb = new StringBuilder();

				List<FileItem> items = upload.parseRequest(req);

				int numSuccessful = 0;
				for(FileItem item : items) {
					if(!item.isFormField()) {
						
						String filename = item.getName();
						if(StringUtil.isEmpty(filename)) continue;
						filename = FilenameUtils.getName(filename);

						InputStream is = item.getInputStream();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
						String sourceMimeType = item.getContentType();
						if(sourceMimeType == null) {
							throw new ServletException("Unknown content type of uploaded file: " + filename);
						}
						converterDelegate.convert(is, sourceMimeType, baos, "text/html");
						byte[] barr = baos.toByteArray();
						
						numSuccessful++;

						String docTitle = filename; // for now
						
						// create doc ref
						Date docDate = new Date();
						DocRef mDoc = EntityFactory.get().buildDoc(docTitle, docDate);

						// save doc ref
						mDoc = uds.saveDoc(mDoc);

						// localize converted doc html content
						String htmlContent = new String(barr, "UTF-8");
						StringBuilder docsb = new StringBuilder(htmlContent);
						DocUtils.localizeDoc(docsb, mDoc.getId(), docTitle);
						htmlContent = docsb.toString();

						// save the doc in the db and create a doc/user binding
						uds.addDocUserBinding(user.getId(), mDoc.getId());

						// save doc content
						DocContent docContent = EntityFactory.get().buildDocContent(mDoc.getId(), htmlContent);
						uds.saveDocContent(docContent);

						String sdoc = DocUtils.serializeDocument(mDoc);

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
