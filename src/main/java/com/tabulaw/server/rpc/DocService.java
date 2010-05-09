/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.server.rpc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocFetchPayload;
import com.tabulaw.common.data.rpc.DocListingPayload;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.IDocService;
import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.User.Role;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.server.DocUtils;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.server.scrape.GoogleScholarDocHandler;
import com.tabulaw.server.scrape.IDocHandler;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.util.StringUtil;

/**
 * Back-end support for rpc document tasks.
 * @author jpk
 */
public class DocService extends RpcServlet implements IDocService {

	private static final long serialVersionUID = -7006228091616946540L;

	private static final Set<IDocHandler> handlers;

	static {
		handlers = new HashSet<IDocHandler>();
		handlers.add(new GoogleScholarDocHandler());
	}

	static IDocHandler resolveHandler(String surl) {
		for(IDocHandler handler : handlers) {
			if(handler.isSupportedUrl(surl)) return handler;
		}
		return null;
	}

	static IDocHandler resolveHandler(DocDataProvider dataProviderType) {
		for(IDocHandler handler : handlers) {
			if(handler.getDocDataType() == dataProviderType) return handler;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DocListingPayload getCachedDocs() {
		Status status = new Status();
		
		final List<DocRef> docList;

		// get user doc bindings
		PersistContext pc = getPersistContext();
		UserDataService uds = pc.getUserDataService();
		UserContext uc = getUserContext();
		User user = uc.getUser();
		if(user.inRole(Role.ADMINISTRATOR)) {
			// admins can see all docs
			docList = new ArrayList<DocRef>();
			try {
				File docDir = DocUtils.getDocDirRef();
				Iterator<File> itr = FileUtils.iterateFiles(docDir, new String[] {
					"htm", "html"
				}, false);
				while(itr.hasNext()) {
					File f = itr.next();
					DocRef mDoc = DocUtils.deserializeDocument(f);
					if(mDoc != null) docList.add(mDoc);
				}
			}
			catch(Exception e) {
				RpcServlet.exceptionToStatus(e, status);
			}
		}
		else {
			// non-admin users only see docs they have previously seen
			docList = uds.getDocsForUser(user.getId());
		}

		DocListingPayload payload = new DocListingPayload(status, docList);

		return payload;
	}

	@Override
	public DocSearchPayload search(DocSearchRequest request) {
		Status status = new Status();
		DocSearchPayload payload = new DocSearchPayload(status);

		IDocHandler handler = resolveHandler(request.getDataProvider());
		if(handler == null) {
			status.addMsg("Unable to resove a doc handler for the given request", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag);
			return payload;
		}

		try {
			// build the search url
			String surl = handler.createSearchUrlString(request);
			URL url = new URL(surl);

			// fetch
			String content = DocUtils.fetch(url);

			// parse
			List<CaseDocSearchResult> results = handler.parseSearchResults(content);
			payload.setResults(results);
		}
		catch(IllegalArgumentException e) {
			RpcServlet.exceptionToStatus(e, status);
		}
		catch(IOException e) {
			if(e instanceof UnknownHostException) {
				String emsg =
						"Can't connect to document provider: "
								+ StringUtil.enumStyleToPresentation(handler.getDocDataType().name());
				RpcServlet.addError(emsg, status);
			}
			else {
				RpcServlet.exceptionToStatus(e, status);
			}
		}

		return payload;
	}

	@Override
	public DocFetchPayload fetch(String remoteDocUrl) {
		Status status = new Status();
		DocFetchPayload payload = new DocFetchPayload(status);
		payload.setRemoteUrl(remoteDocUrl);

		if(StringUtil.isEmpty(remoteDocUrl)) {
			status.addMsg("No remote doc url specified.", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag | MsgAttr.STATUS.flag);
			return payload;
		}

		IDocHandler handler = resolveHandler(remoteDocUrl);
		if(handler == null) {
			status.addMsg("Unable to resove a doc handler for the doc url: " + remoteDocUrl, MsgLevel.ERROR,
					MsgAttr.EXCEPTION.flag | MsgAttr.STATUS.flag);
			return payload;
		}

		final int remoteDocHashcode = DocUtils.docHash(remoteDocUrl);

		// NOTE: this is the doc hash from the perspective of the DocRef entity
		final String docHash = DocUtils.localDocFilename(remoteDocHashcode);
		DocRef mDoc = null;
		try {
			File f = DocUtils.getDocRef(docHash);
			if(!f.exists()) {
				// fetch
				String fcontents = DocUtils.fetch(new URL(remoteDocUrl));

				// parse
				mDoc = handler.parseSingleDocument(fcontents);
				CaseRef caseRef = mDoc.getCaseRef();
				caseRef.setUrl(remoteDocUrl);
				mDoc.setHash(docHash);
				String htmlContent = mDoc.getHtmlContent();
				mDoc.setHtmlContent(null); // clear it out

				// cache (write to disk)
				String sdoc = DocUtils.serializeDocument(mDoc);
				FileUtils.writeStringToFile(f, sdoc + htmlContent);
			}
			else {
				mDoc = DocUtils.deserializeDocument(f);
			}
			payload.setDocHash(docHash);

			// persist the doc user binding
			PersistContext pc = getPersistContext();
			UserContext uc = getUserContext();
			UserDataService uds = pc.getUserDataService();
			uds.saveDocForUser(uc.getUser().getId(), mDoc);
		}
		catch(MalformedURLException e) {
			e.printStackTrace();
			RpcServlet.exceptionToStatus(e, status);
		}
		catch(IOException e) {
			e.printStackTrace();
			RpcServlet.exceptionToStatus(e, status);
		}

		if(!status.hasErrors()) {
			payload.setRemoteUrl(remoteDocUrl);
		}

		return payload;
	}
}
