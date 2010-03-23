/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tll.tabulaw.server.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.tll.common.data.Status;
import com.tll.server.rpc.RpcServlet;
import com.tll.tabulaw.common.data.rpc.DocSearchPayload;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest;
import com.tll.tabulaw.common.data.rpc.DocSearchResult;
import com.tll.tabulaw.common.data.rpc.IDocSearchService;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tll.util.StringUtil;

/**
 * DocSearchServlet
 * @author jpk
 */
public class DocSearchService extends RpcServlet implements IDocSearchService {

	private static final long serialVersionUID = -7006228091616946540L;

	@Override
	public DocSearchPayload search(DocSearchRequest request) {
		Status status = new Status();
		DocSearchPayload payload = new DocSearchPayload(status);

		// assemble the search url
		Document doc = null;
		List<DocSearchResult> results;
		try {
			URL searchUrl = createSearchUrl(request);
			Reader input = createReader(searchUrl);
			try {
				doc = digestHttpContent(input);
			}
			finally {
				input.close();
			}
		}
		catch(IllegalArgumentException e) {
			RpcServlet.exceptionToStatus(e, status);
		}
		catch(IOException e) {
			RpcServlet.exceptionToStatus(e, status);
		}

		// parse the response
		if(!status.hasErrors()) {
			results = parseResponse(request.getDataProvider(), doc);
			payload.setResults(results);
		}
		
		return payload;
	}
	
	/**
	 * Convert the http input data to a w3c document.
	 * @param input the http data
	 * @return newly created w3c doc
	 */
	public static Document digestHttpContent(Reader input) {
		// digest via jtidy
		Tidy tidy = new Tidy();
		tidy.setXHTML(false);
		tidy.setQuiet(true);
		Document doc = tidy.parseDOM(input, null);
		return doc;
	}

	/**
	 * Responsible for parsing raw html doc response to individual html tokens
	 * each representing a search result.
	 * <p>
	 * GOOGLE_SCHOLAR format: <br>
	 * 
	 * <pre>
		<div class="gs_r">
		  <h3>
		    <a class="yC7" href="/scholar_case?case=9143673394692693115&amp;q=term&amp;hl=en&amp;as_sdt=2002">
		      Connally v. General Constr. Co.
		    </a>
		  </h3>
		  - 
		  <a href="/scholar_case?about=9143673394692693115&amp;q=term&amp;hl=en&amp;as_sdt=2002" class="gs_fl">
		    How cited
		  </a>
		  <font size="-1">
		    <br>
		    <span class="gs_a">269 US 385, 46 S. Ct. 126, 70 L. Ed. 322 - Supreme Court, 1926 - Google Scholar</span>
		    <br>
		    <b>...</b> 
		    process of law, in violation of the Fourteenth Amendment to the federal Constitution; that they<br>
		    contain no ascertainable standard of guilt; that it cannot be determined with any degree of certainty<br>
		    what sum constitutes a current wage in any locality; and that the <b>term</b> "locality" itself <b>...</b> <br>
		    <span class="gs_fl">
		      <a href="/scholar?cites=9143673394692693115&amp;hl=en&amp;as_sdt=2002">Cited by 4084</a>
		      - 
		      <a href="/scholar?q=related:exzZ2IHa5H4J:scholar.google.com/&amp;hl=en&amp;as_sdt=2002">
		        Related articles
		      </a>
		      - 
		      <a href="/scholar?cluster=9143673394692693115&amp;hl=en&amp;as_sdt=2002">
		        All 4 versions
		      </a>
		    </span>
		  </font>
		</div>
	 * </pre>
	 * @param dataProvider the search data provider so the response content format
	 *        is known
	 * @param doc dom doc
	 * @return array of html strings
	 */
	public static List<DocSearchResult> parseResponse(DocDataProvider dataProvider, Document doc) {
		ArrayList<DocSearchResult> results = new ArrayList<DocSearchResult>();

		if(dataProvider == DocDataProvider.GOOGLE_SCHOLAR) {
			// isolate all the raw search result html fragments
			NodeList nlist = doc.getElementsByTagName("div");
			ArrayList<Element> rawResults = new ArrayList<Element>();
			for(int i = 0; i < nlist.getLength(); i++) {
				Node node = nlist.item(i);
				Element elm = (Element) node;
				String cname = elm.getAttribute("class").trim().toLowerCase();
				if("gs_r".equals(cname)) {
					rawResults.add(elm);
				}
			}

			NodeList nl;
			Element elm;
			Node n;
			String cname, docTitle, docUrl, citation, docSummary;
			
			for(Element rawElm : rawResults) {
				cname = docTitle = docUrl = citation = docSummary = "";

				// parse the child h3 element
				nl = rawElm.getElementsByTagName("h3");
				elm = (Element) nl.item(0).getChildNodes().item(0);
				docUrl = "http://scholar.google.com" + elm.getAttribute("href");
				docTitle = elm.getChildNodes().item(0).getNodeValue();

				// parse the child font element
				elm = (Element) rawElm.getElementsByTagName("font").item(0);
				nl = elm.getChildNodes();
				for(int i = 0; i < nl.getLength(); i++) {
					n = nl.item(i);
					if(n instanceof Element) {
						elm = (Element) n;
						cname = elm.getAttribute("class").trim().toLowerCase();
						if("gs_a".equals(cname)) {
							citation = elm.getChildNodes().item(0).getNodeValue();
							int j = citation.indexOf(" - Google Scholar");
							if(j > 0) citation.substring(0, j);
						}
					}
					else if(n.getNodeType() == 3) {
						docSummary += n.getNodeValue();
					}
				}

				results.add(new DocSearchResult(docTitle, docUrl, citation, docSummary));
			}
		}

		return results;
	}
	
	public static Reader createReader(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// IMPT: we must set the user-agent otherwise a possible 403 response
		conn.setRequestProperty("User-agent", "Mozilla/4.0");

		InputStream is = conn.getInputStream();
		Reader input = new BufferedReader(new InputStreamReader(is));
		return input;
	}

	/**
	 * Creates a doc search {@link URL} from a request.
	 * @param request
	 * @return newly created url
	 * @throws IllegalArgumentException
	 */
	public static URL createSearchUrl(DocSearchRequest request) throws IllegalArgumentException {
		String searchToken = request.getSearchToken();
		if(StringUtil.isEmpty(searchToken)) {
			throw new IllegalArgumentException("No search token specified.");
		}
		String surl;
		switch(request.getDataProvider()) {
			case GOOGLE_SCHOLAR:
				// FORMAT:
				// http://scholar.google.com/scholar?as_q=term&num=10&btnG=Search+Scholar&as_epq=&as_oq=&as_eq=&as_occt=any&as_sauthors=&as_publication=&as_ylo=&as_yhi=&as_sdt=2&as_sdts=5&hl=en
				/*
				 * http://scholar.google.com/scholar?
				 *   as_q=<searchToken>
				 *   num=10 (page size)
				 *   as_epq=
				 *   &as_oq=
				 *   &as_eq=
				 *   &as_occt=title
				 *   &as_sauthors=
				 *   &as_publication=
				 *   &as_ylo=
				 *   &as_yhi=
				 *   &as_sdt=2
				 *   &as_sdts=5
				 *   &hl=en
				 *   &as_vis=1
				 */
				StringBuilder sb = new StringBuilder(1024);
				sb.append("http://scholar.google.com/scholar?");
				sb.append("as_q=");
				try {
					sb.append(URLEncoder.encode(searchToken, "UTF-8"));
				}
				catch(UnsupportedEncodingException e1) {
					throw new IllegalStateException();
				}
				sb.append("&num=");
				sb.append(request.getNumResults());
				sb.append("&as_epq=");
				sb.append("&as_oq=");
				sb.append("&as_occt=title");
				sb.append("&as_sauthors=");
				sb.append("&as_publication=");
				sb.append("&as_ylo=");
				sb.append("&as_yhi=");
				sb.append("&as_sdt=2");
				sb.append("&as_sdts=5");
				sb.append("&hl=en");
				sb.append("&as_vis=1");
				surl = sb.toString();
				break;
			default:
				throw new IllegalArgumentException("Unhandled doc search data provider: " + request.getDataProvider());
		}

		assert surl != null;
		try {
			URL url = new URL(surl);
			return url;
		}
		catch(MalformedURLException e) {
			throw new IllegalStateException("Unable to assemble doc search url: " + e.getMessage(), e);
		}
	}
}
