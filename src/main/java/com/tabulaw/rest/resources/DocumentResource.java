package com.tabulaw.rest.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

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
		
		DocRef doc = EntityFactory.get().buildCaseDoc(cdd.getTitle(), new Date(), cdd.getParties(), cdd.getReftoken(), cdd.getDocLoc(), cdd.getCourt(), remoteUrl, cdd.getYear());

		// persist the doc ref and doc/user binding
		doc = getDataService().saveDoc(doc);
		getDataService().addDocUserBinding(userContext.getUser().getId(), doc.getId());

		String htmlContent = cdd.getHtmlContent();
		
		// localize doc content
//		StringBuilder sb = new StringBuilder(htmlContent.length() + 1024);
//		sb.append(htmlContent);
//		DocUtils.localizeDoc(sb, doc.getId(), doc.getTitle());
//		htmlContent = sb.toString();

		// persist doc content
		DocContent docContent = EntityFactory.get().buildDocContent(doc.getId(), htmlContent);
		getDataService().saveDocContent(docContent);

		return doc;
	}
	
	@POST
	public DocRef create(@FormParam("remoteUrl") String remoteUrl) {
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
