package com.tabulaw.server.rpc;

import java.util.ArrayList;
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

import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.common.data.rpc.IGoogleDocsService;

@SuppressWarnings("serial")
public class GoogleDocsServiceRpc extends RpcServlet implements
		IGoogleDocsService {

	private final static Log log = LogFactory
			.getLog(GoogleDocsServiceRpc.class);

	private final HttpClient client = new HttpClient();

	@Override
	public String getAuthKey() {
		GetMethod get = new GetMethod(
				"https://www.google.com/accounts/ClientLogin?Email=gtabulaw@olesiak.biz&Passwd=tabulaw&accountType=HOSTED_OR_GOOGLE&service=writely");
		try {
			client.executeMethod(get);
			if (get.getStatusCode() == 200) {
				return parseAuthKey(get.getResponseBodyAsString());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	@Override
	public List<GoogleDocument> getDocuments(String authKey) {
		GetMethod get = new GetMethod(
				"https://docs.google.com/feeds/default/private/full/-/document");
		get.addRequestHeader("Authorization", "GoogleLogin auth=" + authKey);
		get.addRequestHeader("GData-Version", "3.0");
		try {
			client.executeMethod(get);
			if (get.getStatusCode() == 200) {
				return parseDocuments(get.getResponseBodyAsString());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	private String parseAuthKey(String s) {
		String p = "Auth=";
		int i = s.indexOf(p);
		if (i >= 0) {
			return s.substring(i + p.length()).replace("\n", "");
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<GoogleDocument> parseDocuments(String s) {
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
					//IXMLElement email = author.get(0).getChildAtIndex(1);
					doc.setAuthor(name.getContent());
				}
				list.add(doc);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return list;
	}
}
