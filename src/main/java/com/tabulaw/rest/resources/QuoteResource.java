package com.tabulaw.rest.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.tabulaw.common.model.DocContent;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.rest.AuthorizationRequired;
import com.tabulaw.rest.dto.QuoteCreationData;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.util.HtmlUtils;
import com.tabulaw.util.HtmlUtils.QuotePosition;

/**
 * 
 * REST resource to manage Quote entity 
 * 
 * @author yuri
 *
 */
@AuthorizationRequired
@Path("/quotes")
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
public class QuoteResource extends BaseResource {
	
	@GET
	@SuppressWarnings("unchecked")
	public List<Quote> list(
			@QueryParam("docRefIds") String docRefIds,
			@QueryParam("quoteBundleIds") String quoteBundleIds,
			@QueryParam("fetchDocRef") String fetchDocRef) {
		boolean fetch = "true".equals(StringUtils.lowerCase(fetchDocRef));
		UserDataService service = getDataService();
		String userId = getUserId();
		
		Set<Quote> documentQuotes = null;
		Set<Quote> bundleQuotes = null;
		
		// find quotes of the specified docRef 
		if (docRefIds != null) {
			String[] documents = docRefIds.split("\\s*,\\s*");
			documentQuotes = new HashSet<Quote>();
			for (String document : documents) {
				if (! service.isDocAvailableForUser(userId, document)) {
					throw new WebApplicationException(Response.status(Status.FORBIDDEN).
							entity("<error>Document '" + document + "' doesn't " +
									"available for the current user</error>").
							build());
				}				
				documentQuotes.addAll(service.findQuotesByDocForUser(document, userId));
			}
		}		
		
		// find quotes of the specified quoteBundle
		if (quoteBundleIds != null) {
			String[] bundles = quoteBundleIds.split("\\s*,\\s*");
			bundleQuotes = new HashSet<Quote>();
			for (String bundle : bundles) {
				if (! service.isBundleAvailableForUser(userId, bundle)) {
					throw new WebApplicationException(Response.status(Status.FORBIDDEN).
							entity("<error>QuoteBundle '" + bundle + "' doesn't " +
									"available for the current user</error>").
							build());					
				}
				bundleQuotes.addAll(service.getQuoteBundle(bundle).getQuotes());
			}
		}		
		
		List<Quote> result;
		// if both parameters (docRefId and quoteBundleId) are specified intersect the result 
		if (documentQuotes != null && bundleQuotes != null) {
			result = new ArrayList<Quote>(CollectionUtils.
					intersection(documentQuotes, bundleQuotes));
		} else {
			// if specified just one parameter return exact result
			result = new ArrayList<Quote>();
			if (documentQuotes != null) {
				result.addAll(documentQuotes);
			} else if (bundleQuotes != null) {
				result.addAll(bundleQuotes);
			} else {
				// if no parameters are specified return all quotes for the current user
				result = service.findQuotesForUser(userId);
			}
		}
		
		// if we won't send docRef of every quote, clone quote and clean this property 
		if (! fetch) {
			for (int i = 0; i < result.size(); i++) {
				Quote quote = (Quote) result.get(i).clone();
				quote.setDocument(null);
				result.set(i, quote);
			}
		}
		
		return result;
	}
	
	private String positionToMark(int[] position) {
		if (position.length == 0) {
			return "[]";
		}
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < position.length -1; i++) {
			builder.append(position[i]);
			builder.append(",");
		}
		builder.append(position[position.length - 1]);
		builder.append("]");
		return builder.toString();
	}
	
	@POST
	@Consumes("text/plain")
	public Quote create(
			@QueryParam("docRefId") String docRefId,
			@QueryParam("quoteBundleId") String bundleId,			
			String quoteText) {
		if (docRefId == null || StringUtils.isEmpty(quoteText)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		UserDataService service = getDataService();		
		
		if (! service.isDocAvailableForUser(getUserId(), docRefId)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		
		// create serialized mark for the specified quote
		String serializedMark = "|" + quoteText + "|";
		DocRef docRef = service.getDoc(docRefId);
		try {
			// find quote in the documents html
			DocContent content = service.getDocContent(docRefId);
			QuotePosition position = HtmlUtils.findQuoteInHtml(quoteText, content.getHtmlContent());
			if (position == null) {
				throw new WebApplicationException();
			}
			// if it exists in the doc add position into mark
			serializedMark += positionToMark(position.startPosition);
			serializedMark += "|" + position.startOffset + "|";
			serializedMark += positionToMark(position.endPosition);
			serializedMark += "|" + position.endOffset;
		} catch (IOException ex) {
			throw new WebApplicationException(ex);
		}
		if (bundleId != null) {
			if (! service.isBundleAvailableForUser(getUserId(), bundleId)) {
				throw new WebApplicationException(Status.FORBIDDEN);
			}
		} else {
			QuoteBundle bundle = service.getOrphanedQuoteBundleForUser(getUserId());
			bundleId = bundle.getId(); 
		}
		// create quote 
		Quote quote = EntityFactory.get().buildQuote(quoteText, docRef, serializedMark);
		quote = service.addQuoteToBundle(getUserId(), bundleId, quote);
		
		// get id of created quote and add this id into mark property of the quote
		quote.setSerializedMark("mark_" + quote.getId() + quote.getSerializedMark());
		// update mark property of the quote
		service.updateQuote(quote);
		return quote;
	}
	
	@POST
	@Consumes({"text/xml", "application/xml"}) 
	public Quote createFromXml(QuoteCreationData data) {		
		return create(data.getDocRefId(), data.getQuoteBundleId(), data.getQuoteText());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED) 
	public Quote createFromForm(
			@FormParam("docRefId") String docRefId,
			@FormParam("quoteBundleId") String bundleId,
			@FormParam("quoteText") String quoteText) {		
		return create(docRefId, bundleId, quoteText);
	}
	
	
	@GET
	@Path("/{id}")
	public Quote byId(@PathParam("id") String id) {
		if (! getDataService().isQuoteAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		return getDataService().getQuote(id);
	}
	
	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") String id) {
		if (! getDataService().isQuoteAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		getDataService().deleteQuote(getUserId(), id);
	}
}
