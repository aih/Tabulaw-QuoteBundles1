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
import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.rpc.DocRefListPayload;
import com.tabulaw.common.data.rpc.GoogleDocumentListPayload;
import com.tabulaw.common.data.rpc.IGoogleDocsService;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.User;
import com.tabulaw.oauth.GoogleAnonymousOAuthParametersProvider;
import com.tabulaw.oauth.OAuthParametersProvider;
import com.tabulaw.oauth.OAuthParameters;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserDataService;

@SuppressWarnings("serial")
public class GoogleDocsServiceRpc extends RpcServlet implements
		IGoogleDocsService {

	private final static Log log = LogFactory
			.getLog(GoogleDocsServiceRpc.class);

	private final HttpClient client = new HttpClient();

	private OAuthParametersProvider authParametersProvider = new GoogleAnonymousOAuthParametersProvider();

	@Override
	public GoogleDocumentListPayload getDocuments() {
		log.debug("Creating GoogleDocumentListPayload list");
		GoogleDocumentListPayload list = new GoogleDocumentListPayload();
		String path = "/feeds/default/private/full/-/document";
		try {
			GetMethod get = createGetMethod(path);
			log.debug("Executing HttpClient GetMethod with URL=" + path);
			client.executeMethod(get);
			log.debug("HttpClient execute status code is: "
					+ get.getStatusCode());
			if (get.getStatusCode() == 200) {
				list.setDocuments(parseDocuments(get.getResponseBodyAsString()));
			} else {
				log.debug("HttpClient execute status message is: "
						+ get.getStatusText());
				log.error(get.getStatusText());
				return new GoogleDocumentListPayload(new Status(new Msg(
						"Google Docs rejected request.", MsgLevel.ERROR)));
			}
		} catch (Exception e) {
			log.error("", e);
			return new GoogleDocumentListPayload(
					new Status(
							new Msg(
									"Problem occured when connecting to the Google Docs server.",
									MsgLevel.ERROR)));
		}
		log.debug("Returning GoogleDocumentListPayload list");
		return list;
	}

	@Override
	public DocRefListPayload download(Collection<GoogleDocument> documents) {
		DocRefListPayload docs = new DocRefListPayload();
		log.debug("Iterating google documents to download");
		for (GoogleDocument document : documents) {
			try {
				DocRef doc = download(document);
				if (doc != null) {
					docs.getDocRefs().add(doc);
					log.debug("Google document has been downloaded: "
							+ doc.getName() + " / " + doc.getTitle());
				} else {
					log.error("Unable to download google document: "
							+ document.getTitle());
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
		log.debug("Returning download google documents");
		return docs;
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
		GoogleOAuthParameters oauthParameters = new OAuthParameters(
				authParametersProvider.getGoogleDocumentsOAuthParameters());
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
