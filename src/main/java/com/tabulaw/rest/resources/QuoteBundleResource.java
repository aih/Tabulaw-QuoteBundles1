package com.tabulaw.rest.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.rest.AuthorizationRequired;
import com.tabulaw.rest.dto.DocRefWithQuotes;
import com.tabulaw.rest.dto.QuoteBundleWithDocRefs;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserDataService.BundleContainer;

/**
 * 
 * REST resource to manage QuoteBundle entity
 * 
 * @author yuri
 *
 */
@AuthorizationRequired
@Path("/quotebundles")
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
public class QuoteBundleResource extends BaseResource {

	@POST
	public QuoteBundle create(
			@FormParam("name") String name,
			@FormParam("description") String description) {
		if (name == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		QuoteBundle bundle = EntityFactory.get().buildBundle(name, description);
		bundle = getDataService().addBundleForUser(getUserId(), bundle);
		
		bundle.setQuotes(null);		
		return bundle;		
	}
	
	@PUT
	@Path("/{id}")
	public QuoteBundle update(
			@PathParam("id") String id,
			@FormParam("name") String name,
			@FormParam("description") String description) {
		if (name == null && description == null) {
			throw new WebApplicationException(Status.BAD_REQUEST); 
		}
		UserDataService service = getDataService();
		
		if (! service.isBundleAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		QuoteBundle bundle = service.getQuoteBundle(id);
		if (name != null) {
			bundle.setName(name);
		}
		if (description != null) {
			bundle.setDescription(description);
		}
		service.updateBundlePropsForUser(getUserId(), bundle);
		bundle = (QuoteBundle) bundle.clone();
		bundle.setQuotes(null);
		
		return bundle; 
	}
	
	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") String id) {
		if (! getDataService().isBundleAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		getDataService().deleteBundleForUser(getUserId(), id, false);
	}
	
	@GET
	@Path("/{id}")
	public QuoteBundle byId(@PathParam("id") String id) {
		if (! getDataService().isBundleAvailableForUser(getUserId(), id)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		return getDataService().getQuoteBundle(id);
	}	
	
	@GET
	public QuoteBundlesResult list(
			@QueryParam("fetchQuotes") String fetchQuotes,
			@QueryParam("inverseDocRef") String inverseDocRef) {
		boolean fetch = "true".equals(StringUtils.lowerCase(fetchQuotes));
		boolean inverse = "true".equals(StringUtils.lowerCase(inverseDocRef));
		if (inverse && !fetch) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		QuoteBundlesResult result = new QuoteBundlesResult();
		
		BundleContainer bundles = getDataService().getBundlesForUser(getUserId());
		List<QuoteBundle> resultBundles = bundles.getBundles();
		
		// if we won't send quotes of the bundle, clone every bundle and clear quotes list  		
		if (! fetch) {
			for (int i = 0; i < resultBundles.size(); i++) {
				QuoteBundle clone = (QuoteBundle) resultBundles.get(i).clone();
				clone.setQuotes(null);
				resultBundles.set(i, clone);
			}
		}
		// inverse objects hierarchy. 
		// the standart hierarchy is: QuoteBundle -> Quote -> DocRef
		// the inversed one: QuoteBundle -> DocRef -> Quote
		if (inverse) {
			List<QuoteBundleWithDocRefs> inversedResult = new ArrayList<QuoteBundleWithDocRefs>(resultBundles.size());
			Map<String, DocRefWithQuotes> documents = new HashMap<String, DocRefWithQuotes>();
			for (QuoteBundle bundle : resultBundles) {
				if (bundle.getQuotes() == null) {
					inversedResult.add(new QuoteBundleWithDocRefs(bundle));
					continue;
				}			
				
				QuoteBundleWithDocRefs inversedBundle = new QuoteBundleWithDocRefs(bundle);
				documents.clear();
				for (Quote quote : inversedBundle.getQuotes()) {
					String documentId = quote.getDocument().getId();					
					if (! documents.containsKey(documentId)) {
						DocRefWithQuotes newDoc = new DocRefWithQuotes(quote.getDocument());
						newDoc.setQuotes(new ArrayList<Quote>());
						quote.setDocument(null);
						newDoc.getQuotes().add(quote);
						documents.put(documentId, newDoc);						
					} else {
						quote.setDocument(null);
						documents.get(documentId).getQuotes().add(quote);
					}
				}				
				inversedBundle.setDocRefs(new ArrayList<DocRefWithQuotes>(documents.values()));
				inversedBundle.setQuotes(null);
				inversedResult.add(inversedBundle);
			}
			result.inversed = inversedResult;
		} else {
			result.result = resultBundles;
		}
		return result;
	}
	
	@XmlRootElement(name = "quoteBundles")
	static class QuoteBundlesResult {
		
		@XmlElement(name = "quoteBundle")
		List<QuoteBundle> result;
		
		@XmlElement(name = "quoteBundle")
		List<QuoteBundleWithDocRefs> inversed;
	}
}
