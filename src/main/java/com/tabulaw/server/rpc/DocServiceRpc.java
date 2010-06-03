/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.server.rpc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.IRemoteDocService;
import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocContent;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.model.User;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.DocUtils;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.scrape.GoogleScholarDocHandler;
import com.tabulaw.service.scrape.IDocHandler;
import com.tabulaw.util.StringUtil;

/**
 * Back-end support for rpc document tasks.
 * @author jpk
 */
public class DocServiceRpc extends RpcServlet implements IRemoteDocService {

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
	public DocPayload fetch(String remoteDocUrl) {
		Status status = new Status();
		DocPayload payload = new DocPayload(status);

		if(StringUtil.isEmpty(remoteDocUrl)) {
			status.addMsg("No remote doc url specified.", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag | MsgAttr.STATUS.flag);
			return payload;
		}

		PersistContext pc = getPersistContext();
		UserContext uc = getUserContext();
		UserDataService uds = pc.getUserDataService();
		User user = uc.getUser();
		
		// first attempt to find case doc in db by remote url
		DocRef doc;
		try {
			doc = uds.findCaseDocByRemoteUrl(remoteDocUrl);
		}
		catch(EntityNotFoundException e) {
			// ok - need to actually fetch it
			
			// fetch doc data
			String fcontents;
			try {
				fcontents = DocUtils.fetch(new URL(remoteDocUrl));
			}
			catch(MalformedURLException e1) {
				RpcServlet.exceptionToStatus(e, status);
				return payload;
			}
			catch(IOException e1) {
				RpcServlet.exceptionToStatus(e, status);
				return payload;
			}

			// resolve the doc handler
			IDocHandler handler = resolveHandler(remoteDocUrl);
			if(handler == null) {
				status.addMsg("Unable to resove a doc handler for the doc url: " + remoteDocUrl, MsgLevel.ERROR,
						MsgAttr.EXCEPTION.flag | MsgAttr.STATUS.flag);
				return payload;
			}
			
			// parse fetched doc data
			doc = handler.parseSingleDocument(fcontents);
			CaseRef caseRef = doc.getCaseRef();
			caseRef.setUrl(remoteDocUrl);
			
			// persist the doc ref and doc/user binding
			doc = uds.saveDoc(doc);
			uds.addDocUserBinding(user.getId(), doc.getId());
			
			// localize doc content
			String htmlContent = doc.getHtmlContent();
			doc.setHtmlContent(null);
			StringBuilder sb = new StringBuilder(htmlContent.length() + 1024);
			sb.append(htmlContent);
			DocUtils.localizeDoc(sb, doc.getId(), doc.getTitle());
			htmlContent = sb.toString();

			// persist doc content
			DocContent docContent = EntityFactory.get().buildDocContent(doc.getId(), htmlContent);
			uds.saveDocContent(docContent);
		}

		payload.setDocRef(doc);

		return payload;
	}
}
