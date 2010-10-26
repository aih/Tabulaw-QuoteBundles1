package com.tabulaw.server.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.common.data.rpc.IGoogleDocsService;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.server.bean.AnonymousGoogleOAuthParametersProvider;
import com.tabulaw.server.bean.IGoogleOAuthParametersProvider;
import com.tabulaw.service.entity.UserDataService;

@SuppressWarnings("serial")
public class GoogleDocsServiceRpc extends RpcServlet implements
		IGoogleDocsService {

	private final static Log log = LogFactory
			.getLog(GoogleDocsServiceRpc.class);

	private final HttpClient client = new HttpClient();

	private IGoogleOAuthParametersProvider authParametersProvider = new AnonymousGoogleOAuthParametersProvider();

	@Override
	public List<GoogleDocument> getDocuments() {
		String path = "/feeds/default/private/full/-/document";
		try {
			GetMethod get = createGetMethod(path);
			client.executeMethod(get);
			if (get.getStatusCode() == 200) {
				return parseDocuments(get.getResponseBodyAsString());
			} else {
				log.error(get.getStatusText());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	@Override
	public List<DocRef> download(Collection<GoogleDocument> documents) {
		List<DocRef> downloaded = new ArrayList<DocRef>();
		for (GoogleDocument document : documents) {
			try {
				DocRef doc = download(document);
				if (doc != null) {
					downloaded.add(doc);
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return downloaded;
	}

	private DocRef download(GoogleDocument document) {
		String pattern = "document:";
		String resourceId = document.getResourceId();
		int k = resourceId.indexOf(pattern);
		if (k >= 0) {
			resourceId = resourceId.substring(pattern.length());
		}
		try {
			GetMethod get = createGetMethod("/feeds/download/documents/Export?docID="
					+ resourceId + "&exportFormat=html");
			client.executeMethod(get);
			if (get.getStatusCode() == 200) {
				return saveDocument(document, get.getResponseBodyAsString());
			} else {
				log.error("Unable to download google document: "
						+ get.getStatusText());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<GoogleDocument> parseDocuments(String s) {
		log.debug("Parsing XML response of google documents list");
		List<GoogleDocument> list = new ArrayList<GoogleDocument>();
		try {
			IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader reader = StdXMLReader.stringReader(s);
			parser.setReader(reader);
			IXMLElement xml = (IXMLElement) parser.parse();
			Vector<IXMLElement> entries = xml.getChildrenNamed("entry");
			for (IXMLElement entry : entries) {
				Vector<IXMLElement> resourceId = entry
						.getChildrenNamed("gd:resourceId");
				Vector<IXMLElement> title = entry.getChildrenNamed("title");
				Vector<IXMLElement> updated = entry.getChildrenNamed("updated");
				Vector<IXMLElement> author = entry.getChildrenNamed("author");
				if (resourceId == null || resourceId.isEmpty()
						|| resourceId.get(0) == null) {
					continue;
				}
				if (title == null || title.isEmpty() || title.get(0) == null) {
					continue;
				}
				GoogleDocument doc = new GoogleDocument();
				doc.setResourceId(resourceId.get(0).getContent());
				doc.setTitle(title.get(0).getContent());
				if (updated != null && !updated.isEmpty()
						&& updated.get(0) != null) {
					String date = updated.get(0).getContent();
					int i = date.indexOf("T");
					if (i > 0) {
						date = date.substring(0, i);
					}
					doc.setDate(date);
				}
				if (author != null && !author.isEmpty()
						&& author.get(0) != null) {
					IXMLElement name = author.get(0).getChildAtIndex(0);
					// IXMLElement email = author.get(0).getChildAtIndex(1);
					doc.setAuthor(name.getContent());
				}
				list.add(doc);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		log.debug("Returning google documents list");
		return list;
	}

	private GetMethod createGetMethod(String path) throws OAuthException {
		authParametersProvider.setHttpServletRequest(getThreadLocalRequest());
		GoogleOAuthParameters oauthParameters = authParametersProvider
				.getGoogleDocumentsOAuthParameters();
		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
				new OAuthHmacSha1Signer());
		// TODO if no token then throw RPC exception to redirect user to Google
		// Authorization page and create token
		String header = oauthHelper.getAuthorizationHeader(getUrl(path), "GET",
				oauthParameters);
		GetMethod get = new GetMethod(getUrl(path));
		get.addRequestHeader("Authorization", header);
		get.addRequestHeader("GData-Version", "3.0");
		return get;
	}

	private DocRef saveDocument(GoogleDocument document, String htmlContent) {
		final PersistContext pc = (PersistContext) getThreadLocalRequest()
				.getSession(false).getServletContext()
				.getAttribute(PersistContext.KEY);
		final UserContext uc = (UserContext) getThreadLocalRequest()
				.getSession(false).getAttribute(UserContext.KEY);
		UserDataService uds = pc.getUserDataService();
		User user = uc.getUser();
		Date docDate = new Date();
		DocRef mDoc = EntityFactory.get().buildDoc(
				"GDocs " + document.getTitle(), docDate, false);
		mDoc = uds.saveDoc(mDoc);
		DocContent docContent = EntityFactory.get().buildDocContent(
				mDoc.getId(), htmlContent);
		uds.addDocUserBinding(user.getId(), mDoc.getId());
		uds.saveDocContent(docContent);
		return mDoc;
	}

	private String getUrl(String path) {
		return "https://docs.google.com" + path;
	}
}
