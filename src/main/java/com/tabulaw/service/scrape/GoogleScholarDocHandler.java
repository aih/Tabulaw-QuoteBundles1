/**
 * The Logic Lab
 * @author jpk
 * @since Mar 26, 2010
 */
package com.tabulaw.service.scrape;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.tabulaw.common.data.dto.CaseDocData;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.util.StringUtil;

/**
 * Handler for <code>scholar.google.com</code> searches.
 * @author jpk
 */
public class GoogleScholarDocHandler extends AbstractDocHandler {

	@Override
	public DocDataProvider getDocDataType() {
		return DocDataProvider.GOOGLE_SCHOLAR;
	}

	@Override
	public boolean isSupportedUrl(String surl) {
		return surl.indexOf("scholar.google.com") > 0;
	}

	/*
	 * FORMAT:
	 * http://scholar.google.com/scholar?
	 *   as_q=<searchToken>
	 *   &num=<page size>
	 *   &start=<offset>
	 *   &as_epq=
	 *   &as_oq=
	 *   &as_eq=
	 *   &as_occt=title|any
	 *   &as_sauthors=
	 *   &as_publication=
	 *   &as_ylo=
	 *   &as_yhi=
	 *   &as_sdt=2
	 *   &as_sdts=5
	 *   &hl=en
	 *   &as_vis=1
	 */
	@Override
	public String createSearchUrlString(DocSearchRequest request) {
		String searchToken = request.getSearchToken();
		if(StringUtil.isEmpty(searchToken)) {
			throw new IllegalArgumentException("No search token specified.");
		}
		String surl;
		StringBuilder sb = new StringBuilder(1024);
		sb.append("http://scholar.google.com/scholar?");
		sb.append("as_q=");
		try {
			sb.append(URLEncoder.encode(searchToken, "UTF-8"));
		}
		catch(UnsupportedEncodingException e1) {
			throw new IllegalStateException();
		}

		// page size
		sb.append("&num=");
		sb.append(request.getNumResults());

		// offset
		if(request.getOffset() > 0) {
			sb.append("&start=");
			sb.append(request.getOffset());
		}

		sb.append("&as_epq=");
		sb.append("&as_oq=");

		// full text search or just by title?
		sb.append("&as_occt=");
		sb.append(request.isFullTextSearch() ? "any" : "title");

		sb.append("&as_sauthors=");
		sb.append("&as_publication=");
		sb.append("&as_ylo=");
		sb.append("&as_yhi=");

		// presume this means search all state/fed cases
		sb.append("&as_sdt=2");
		sb.append("&as_sdts=5");

		sb.append("&hl=en");

		// ???
		sb.append("&as_vis=1");

		surl = sb.toString();
		return surl;
	}

	/**
	 * Responsible for parsing raw html doc response to individual html tokens
	 * each representing a search result.
	 * <p>
	 * GOOGLE_SCHOLAR format: <br>
	 * 
	 * <pre>
	 * <div class="gs_r">
	 * 		  <h3>
	 * 		    <a class="yC7" href="/scholar_case?case=9143673394692693115&amp;q=term&amp;hl=en&amp;as_sdt=2002">
	 * 		      Connally v. General Constr. Co.
	 * 		    </a>
	 * 		  </h3>
	 * 		  - 
	 * 		  <a href="/scholar_case?about=9143673394692693115&amp;q=term&amp;hl=en&amp;as_sdt=2002" class="gs_fl">
	 * 		    How cited
	 * 		  </a>
	 * 		  <font size="-1">
	 * 		    <br>
	 * 		    <span class="gs_a">269 US 385, 46 S. Ct. 126, 70 L. Ed. 322 - Supreme Court, 1926 - Google Scholar</span>
	 * 		    <br>
	 * 		    <b>...</b> 
	 * 		    process of law, in violation of the Fourteenth Amendment to the federal Constitution; that they<br>
	 * 		    contain no ascertainable standard of guilt; that it cannot be determined with any degree of certainty<br>
	 * 		    what sum constitutes a current wage in any locality; and that the <b>term</b> "locality" itself <b>...</b> <br>
	 * 		    <span class="gs_fl">
	 * 		      <a href="/scholar?cites=9143673394692693115&amp;hl=en&amp;as_sdt=2002">Cited by 4084</a>
	 * 		      - 
	 * 		      <a href="/scholar?q=related:exzZ2IHa5H4J:scholar.google.com/&amp;hl=en&amp;as_sdt=2002">
	 * 		        Related articles
	 * 		      </a>
	 * 		      - 
	 * 		      <a href="/scholar?cluster=9143673394692693115&amp;hl=en&amp;as_sdt=2002">
	 * 		        All 4 versions
	 * 		      </a>
	 * 		    </span>
	 * 		  </font>
	 * 		</div>
	 * </pre>
	 * @param rawHtml
	 * @return array of html strings
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseDocSearchResult> parseSearchResults(String rawHtml) {
		ArrayList<CaseDocSearchResult> results = new ArrayList<CaseDocSearchResult>();

		HtmlCleaner cleaner = new HtmlCleaner();

		TagNode root;
		try {
			root = cleaner.clean(rawHtml);
		}
		catch(IOException e) {
			throw new IllegalArgumentException(e);
		}
		
		// isolate all the raw search result html fragments
		TagNode[] nlist = root.getElementsByAttValue("class", "gs_r", true, false);
		ArrayList<TagNode> rawResults = new ArrayList<TagNode>();
		for(int i = 0; i < nlist.length; i++) {
			TagNode node = nlist[i];
			rawResults.add(node);
		}

		TagNode[] tnarr;
		TagNode tn;
		String cname, docTitle, docTitleHtml, docUrl, citation, docSummary;
		String href;

		for(TagNode rawElm : rawResults) {
			cname = docTitle = docTitleHtml = docUrl = citation = docSummary = "";

			// parse the child h3 element
			tnarr = rawElm.getElementsByName("h3", true);
			if(tnarr != null && tnarr.length > 0) {
				tn = (TagNode) tnarr[0].getChildTagList().get(0);
				href = (String) tn.getAttributes().get("href");
				if(href == null) href = "";
				if(href.indexOf("http") < 0) href = "http://scholar.google.com" + href;
				docUrl = href;
				docTitleHtml = cleaner.getInnerHtml(tn);
				// strip out html tags
				//docTitle = docTitleHtml.replaceAll("\\<.*?\\>", "");
				docTitle = StringEscapeUtils.unescapeHtml(docTitle);
			}
			else {
				System.out.println("WARN: No h3 tag found in search result entry");
			}

			// parse the child font element
			tn = rawElm.getElementsByName("font", true)[0];
			List<Object> children = tn.getChildren();
			for(Object child : children) {
				if(child instanceof TagNode) {
					tn = (TagNode) child;
					cname = tn.getAttributeByName("class");
					if("gs_a".equals(cname)) {
						citation = cleaner.getInnerHtml(tn);
						int j = citation.indexOf(" - Google Scholar");
						if(j > 0) citation = citation.substring(0, j);
					}
				}
				else {
					String txt = child.toString();
					// String txt = tn.getText().toString().trim();
					if(txt.length() > 0 && !"null".equals(txt)) {
						docSummary += txt;
					}
				}
			}

			docSummary = docSummary.trim().replaceAll("\n", "") + "...";

			// TODO fix and not use current date rather extract it
			Date docDate = new Date();

			results.add(new CaseDocSearchResult(docTitle, docDate, docUrl, citation, docTitleHtml, docSummary));
		}

		return results;
	}

	@Override
	public CaseDocData parseSingleDocument(String rawHtml) {
		String reftoken = "", dlcy = "", parties = "", docLoc = "", court = "", syear = "", docTitle = "", htmlContent = "";

		try {
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode root = cleaner.clean(rawHtml);
			TagNode[] tnarr;

			// extract citation/doc title
			tnarr = root.getElementsByAttValue("id", "gsl_reference", true, false);
			if(tnarr == null || tnarr.length < 1) {
				// fall back on gsl_title
				tnarr = root.getElementsByAttValue("class", "gsl_title", true, false);
				if(tnarr == null || tnarr.length < 1) throw new IllegalArgumentException("No gsl_reference nor gsl_title tags found");
				if(tnarr.length > 0) {
					reftoken = tnarr[0].getText().toString().replace(" - Google Scholar", "");
				}
			}
			reftoken = tnarr[0].getText().toString().trim();
			reftoken = reftoken.replace("\r", "").replace("\n", "");
			while(reftoken.indexOf("  ") >= 0) reftoken = reftoken.replace("  ", " ");

			String[] sarr = reftoken.split(",");
			if(sarr.length > 2) {
				// re-join the split parts after the first element
				String[] nsarr = new String[2];
				nsarr[0] = sarr[0];
				String two = "";
				for(int i = 1; i < sarr.length; i++) {
					two += ',' + sarr[i];
				}
				nsarr[1] = two.substring(1);
				sarr = nsarr;
			}
			if(sarr.length == 2) {
				parties = sarr[0].trim();
				dlcy = sarr[1].trim();
				
				// parse citation into constituent parts
				sarr = dlcy.split("-");
				if(sarr.length == 2) {
					docLoc = sarr[0].trim(); // i.e. "216 SE 2d 199"
					court = sarr[1].trim(); // i.e. "Va: Supreme Court 1975"
					//int colonIndex = court.indexOf(':');
					//if(colonIndex >= 0) {
						//court = court.substring(colonIndex + 1).trim();
						
						int courtlen = court.length();
						if(courtlen > 4) {
							String sub = court.substring(courtlen - 4);
							if(sub.startsWith("19") || sub.startsWith("20")) {
								syear = sub;
								court = court.substring(0, courtlen - 4).trim();
							}
						}

					//}
				}

				docTitle = parties;
			}
			else {
				docTitle = reftoken;
			}

			if(syear.length() == 0) {
				int dtlen = reftoken.length();
				if(dtlen > 4) {
					String sub = reftoken.substring(dtlen - 4);
					if(sub.startsWith("19") || sub.startsWith("20")) {
						syear = sub;
					}
				}
			}

			// extract #gsl_opinion div (docContent)
			TagNode[] tags = root.getElementsByAttValue("id", "gsl_opinion", true, false);
			if(tags == null || tags.length < 1) throw new IllegalArgumentException("No gsl_opinion tag found");
			TagNode tagOpinion = tags[0];

			// remove any nested script tags
			TagNode[] stags = tagOpinion.getElementsByName("script", true);
			if(stags != null) {
				for(TagNode sn : stags) {
					tagOpinion.removeChild(sn);
				}
			}
			
			// tag root node as google scholar type doc
			root = tags[0];
			root.getChildTags()[0].addAttribute("class", "googlescholar");

			htmlContent = cleaner.getInnerHtml(root);

			// absolutize local hrefs
			htmlContent = htmlContent.replace("/scholar_case", "http://scholar.google.com/scholar_case");
		}
		catch(IOException e) {
			throw new IllegalArgumentException(e);
		}

		int year = Integer.parseInt(syear);
		
		CaseDocData doc = new CaseDocData(docTitle, reftoken, parties, docLoc, court, null, year, htmlContent);
		
		return doc;
	}
}
