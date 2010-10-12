package com.tabulaw.rest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;

import com.tabulaw.common.data.dto.CaseDocData;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.rest.AuthorizationRequired;
import com.tabulaw.rest.dto.DocDetails;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.DocUtils;
import com.tabulaw.service.convert.DataConverterDelegate;
import com.tabulaw.service.scrape.DocHandlerResolver;
import com.tabulaw.service.scrape.IDocHandler;

/**
 * 
 * REST resource to manage DocRef and DocContent entities
 * 
 * @author yuri
 *
 */
@AuthorizationRequired
@Path("/docrefs")
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
public class DocumentResource extends BaseResource {
	
	private DocRef fetchDocument(String remoteUrl) {
		UserContext userContext = (UserContext) httpRequest.getSession().getAttribute(REST_USER_KEY);
		// TODO verify this check for a valid user in a valid user context
		if(userContext == null || userContext.getUser() == null) throw new IllegalStateException();
		
		String fcontents;
		try {
			fcontents = DocUtils.fetch(new URL(remoteUrl));
		}
		catch(MalformedURLException ex) {
			throw new WebApplicationException(ex, Status.INTERNAL_SERVER_ERROR);
		}
		catch(IOException ex) {
			throw new WebApplicationException(ex, Status.INTERNAL_SERVER_ERROR);
		}

		// resolve the doc handler
		IDocHandler handler;
		try {
			handler = DocHandlerResolver.resolveHandlerFromRemoteUrl(remoteUrl);
		}
		catch(IllegalArgumentException e) {
			throw new WebApplicationException(Status.NOT_ACCEPTABLE);
		}
		
		// parse fetched doc data
		CaseDocData cdd = handler.parseSingleDocument(fcontents);
		
		int lastPageNumber = cdd.getContent().getFirstPageNumber() + cdd.getContent().getPagesXPath().size();
		DocRef doc = EntityFactory.get().buildCaseDoc(cdd.getTitle(), new Date(), cdd.getParties(), cdd.getReftoken(), cdd.getDocLoc(), cdd.getCourt(), remoteUrl, cdd.getYear(), 
					cdd.getContent().getFirstPageNumber(), lastPageNumber);

		// persist the doc ref and doc/user binding
		doc = getDataService().saveDoc(doc);
		getDataService().addDocUserBinding(userContext.getUser().getId(), doc.getId());

		String htmlContent = cdd.getContent().getHtmlContent();
		
		// localize doc content
//		StringBuilder sb = new StringBuilder(htmlContent.length() + 1024);
//		sb.append(htmlContent);
//		DocUtils.localizeDoc(sb, doc.getId(), doc.getTitle());
//		htmlContent = sb.toString();

		// persist doc content
		DocContent docContent = EntityFactory.get().buildDocContent(doc.getId(), htmlContent, cdd.getContent().getPagesXPath(), cdd.getContent().getFirstPageNumber());
		getDataService().saveDocContent(docContent);

		return doc;
	}
	
	private DocRef scrapeDocument(String remoteUrl) {
		if (StringUtils.isEmpty(remoteUrl)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		DocRef document = null;
		try {
			document = getDataService().
						findCaseDocByRemoteUrl(remoteUrl);
		} catch (EntityNotFoundException ex) {
			document = fetchDocument(remoteUrl);
		}
		UserContext user = (UserContext) httpRequest.getSession().getAttribute(REST_USER_KEY);
		try {
			getDataService().addDocUserBinding(user.getUser().getId(), document.getId());
		} catch (EntityExistsException ex) {
			// it's ok
		}
		return document;		
	}
	
	private String downloadDocument(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US)");
			
			return createHtmlContent(conn.getInputStream(), conn.getContentType());
		} catch (Exception ex) {
			throw new WebApplicationException(ex);			 
		}		
	}
	
	private String createHtmlContent(InputStream inpust, String contentType) throws Exception {
		if (contentType == null) {
			contentType = "text/html";
		}
		int separatorIndex = contentType.indexOf(";");
		if (separatorIndex != -1) {
			contentType = contentType.substring(0, separatorIndex);
		}
		
		DataConverterDelegate converterDelegate =
			(DataConverterDelegate) servletContext.getAttribute(DataConverterDelegate.KEY);			
		ByteArrayOutputStream output = new ByteArrayOutputStream();			
		converterDelegate.convert(inpust, contentType, output, "text/html");
		
		return output.toString("utf-8");
	}
	
	private DocRef createGenericDocument(String title, String parties, String docLoc, 
			String court, String year, String source, String docContent) {
		if (source == null || title == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		
		int yearNum = 0;
		if (! StringUtils.isEmpty(year)) {
			yearNum = Integer.parseInt(year);
		}
		
		StringBuilder refToken = new StringBuilder();
		if (! StringUtils.isEmpty(title)) {
			refToken.append(title);
		}
		if (! StringUtils.isEmpty(docLoc)) {
			if (refToken.length() != 0) {
				refToken.append(", ");				
			}
			refToken.append(docLoc);			
		}
		if (yearNum != 0 || ! StringUtils.isEmpty(court)) {
			if (refToken.length() != 0) {
				refToken.append(" ");
			}
			refToken.append("(");
			if (! StringUtils.isEmpty(court)) {
				refToken.append(court);
				if (yearNum != 0) {
					refToken.append(" ");
					refToken.append(year);
				}				
			} else {
				refToken.append(year);
			}
			refToken.append(")");
		}
		DocRef document;
		if (refToken.length() == 0) {
			document = EntityFactory.get().buildDoc(title, new Date());
		} else {
			document = EntityFactory.get().buildCaseDoc(title, new Date(), parties, refToken.toString(), 
							docLoc, court, null, yearNum, 0, 0);
		}
		source = source.toLowerCase();
		if (! "upload".equals(source)) {
			String htmlContent = "html".equals(source) ? docContent : downloadDocument(source);
			document.getCaseRef().setUrl(source);
			document = getDataService().saveDoc(document);
			
			DocContent content = EntityFactory.get().buildDocContent(document.getId(), htmlContent);
			getDataService().saveDocContent(content);
			getDataService().addDocUserBinding(getUserId(), document.getId());
		} else {
			httpRequest.getSession().setAttribute("documentToUpload", document);
			return null;
		}
		return document;
	}
	
	@POST
	public DocRef create(
			@FormParam("remoteUrl") String remoteUrl,
			@FormParam("title") String title,
			@FormParam("parties") String parties,
			@FormParam("docLoc") String docLoc,
			@FormParam("court") String court,
			@FormParam("year") String year,
			@FormParam("source") String source,
			@FormParam("docContent") String docContent) {
		if (remoteUrl != null) {
			return scrapeDocument(remoteUrl);
		}
		return createGenericDocument(title, parties, docLoc, court, year, source, docContent);
	}
	
	@POST
	@Path("/upload")
	public DocRef upload() {
		DocRef document = (DocRef) httpRequest.getSession().getAttribute("documentToUpload");
		InputStream input = null;
		String contentType = null;
		try {
			if (ServletFileUpload.isMultipartContent(httpRequest)) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				
				List<FileItem> items = upload.parseRequest(httpRequest);
				Map<String, String> parameters = new HashMap<String, String>();
				for (FileItem item : items) {
					if (! item.isFormField()) {
						input = item.getInputStream();
						contentType = item.getContentType();
					} else {
						parameters.put(item.getFieldName(), item.getString());
					}
				}
				if (document == null && parameters.get("title") != null) {
					createGenericDocument(
							parameters.get("title"),
							parameters.get("parties"),
							parameters.get("docLoc"),
							parameters.get("court"),
							parameters.get("year"),
							"upload",
							null
					);					
					document = (DocRef) httpRequest.getSession().getAttribute("documentToUpload");
				}
			} else {
				input = httpRequest.getInputStream();
				contentType = httpRequest.getContentType();
			}
			if (input == null || document == null) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
			String htmlContent = createHtmlContent(input, contentType);
			document = getDataService().saveDoc(document);
			
			DocContent content = EntityFactory.get().buildDocContent(document.getId(), htmlContent);
			getDataService().saveDocContent(content);
			getDataService().addDocUserBinding(getUserId(), document.getId());
			httpRequest.getSession().setAttribute("documentToUpload", null);
			return document;
		} catch (Exception ex) {
			if (ex instanceof WebApplicationException) {
				throw (WebApplicationException) ex;
			}
			throw new WebApplicationException(ex);
		}
	}
	
	@GET
	public List<DocRef> list() {
		return getDataService().getDocsForUser(getUserId());
	}
	
	@GET
	@Path("/{id}")
	public DocDetails byId(@PathParam("id") String id) {
		if (! getDataService().isDocAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}		
		return new DocDetails(
					getDataService().getDoc(id), 
					getDataService().getDocContent(id)
				);
	}
	
	@DELETE
	@Path("/{id}")	
	public void remove(@PathParam("id") String id) {
		if (! getDataService().isDocAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		getDataService().removeDocUserBinding(getUserId(), id);
		if (getDataService().getDocUserBindingsForDoc(id).isEmpty()) {
			getDataService().deleteDoc(id);
		}
	}
}
