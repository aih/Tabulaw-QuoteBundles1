/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.server.rpc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.dto.CaseDocData;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.IRemoteDocService;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.DocUtils;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.scrape.DocHandlerResolver;
import com.tabulaw.service.scrape.IDocHandler;
import com.tabulaw.util.StringUtil;

/**
 * Fetches remote documents and remote doc search results.
 * <p>
 * Employs {@link IDocHandler} implementations for the actual data fetching.
 * @author jpk
 */
public class DocServiceRpc extends RpcServlet implements IRemoteDocService {

	private static final long serialVersionUID = -7006228091616946540L;

	@Override
	public DocSearchPayload search(DocSearchRequest request) {
		Status status = new Status();
		DocSearchPayload payload = new DocSearchPayload(status);

		IDocHandler handler;
		try {
			handler = DocHandlerResolver.resolveHandlerFromDataProvider(request.getDataProvider());
		}
		catch(IllegalArgumentException e) {
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
				handleException(e);
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
		DocRef doc = null;
		DocContent docContent = null;
		try {
			doc = uds.findCaseDocByRemoteUrl(remoteDocUrl);
			docContent = uds.getDocContent(doc.getId());
		}
		catch(EntityNotFoundException e) {
			// ok - need to actually fetch it

			// fetch doc data
			String fcontents;
			try {
				fcontents = DocUtils.fetch(new URL(remoteDocUrl));
			}
			catch(MalformedURLException ex) {
				RpcServlet.exceptionToStatus(ex, status);
				return payload;
			}
			catch(IOException ex) {
				handleException(e);
				RpcServlet.exceptionToStatus(ex, status);
				return payload;
			}

			// resolve the doc handler
			IDocHandler handler;
			try {
				handler = DocHandlerResolver.resolveHandlerFromRemoteUrl(remoteDocUrl);
			}
			catch(IllegalArgumentException ex) {
				status.addMsg("Unable to resove a doc handler for the doc url: " + remoteDocUrl, MsgLevel.ERROR,
						MsgAttr.EXCEPTION.flag | MsgAttr.STATUS.flag);
				return payload;
			}

			// parse fetched doc data
			CaseDocData cdd = handler.parseSingleDocument(fcontents);
			
			int lastPageNumber = cdd.getContent().getFirstPageNumber() + cdd.getContent().getPagesXPath().size();
			doc = EntityFactory.get().buildCaseDoc(cdd.getTitle(), new Date(), false, cdd.getParties(), 
					cdd.getReftoken(), cdd.getDocLoc(), cdd.getCourt(), remoteDocUrl, cdd.getYear(), 
					cdd.getContent().getFirstPageNumber(), lastPageNumber);

			// persist the doc ref and doc/user binding
			doc = uds.saveDoc(doc);
			uds.addDocUserBinding(user.getId(), doc.getId());

			String htmlContent = cdd.getContent().getHtmlContent();
			
			// localize doc content
//			StringBuilder sb = new StringBuilder(htmlContent.length() + 1024);
//			sb.append(htmlContent);
//			DocUtils.localizeDoc(sb, doc.getId(), doc.getTitle());
//			htmlContent = sb.toString();

			// persist doc content
			docContent = EntityFactory.get().buildDocContent(doc.getId(), htmlContent, cdd.getContent().getPagesXPath(), cdd.getContent().getFirstPageNumber());
			uds.saveDocContent(docContent);
		}

		payload.setDocRef(doc);
		payload.setDocContent(docContent);

		return payload;
	}
}
