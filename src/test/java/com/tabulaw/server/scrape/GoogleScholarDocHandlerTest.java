/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tabulaw.server.scrape;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tabulaw.common.data.dto.CaseDoc;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.server.DocUtils;
import com.tabulaw.util.ClassUtil;

/**
 * Tests {@link GoogleScholarDocHandler}
 * @author jpk
 */
@Test(groups = "server")
public class GoogleScholarDocHandlerTest {
	
	@Test(enabled = false)
	public void testParseStaticSearchResultsResponse() throws Exception {
		
		String raw;
		try {
			URL url = ClassUtil.getResource("com/tabulaw/server/scrape/testGoogleScholarSearchResults.htm");
			File f = new File(url.toURI());
			raw = FileUtils.readFileToString(f, "UTF-8");
		}
		catch(IOException e) {
			throw new IllegalStateException(e);
		}
		catch(URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		
		IDocHandler docHandler = new GoogleScholarDocHandler();
		
		List<CaseDocSearchResult> results = docHandler.parseSearchResults(raw);
		Assert.assertEquals(results.size(), 3);
		
		for(int i = 0; i < results.size(); i++) {
			CaseDoc dsr = results.get(i);
			System.out.println(dsr);
		}
	}

	@Test(enabled = false)
	public void testParseLiveSearchResultsResponse() throws Exception {
		IDocHandler docHandler = new GoogleScholarDocHandler();
		
		// build search url
		String searchTerm = "rowe";
		int offset = 0;
		int numResults = 4;
		DocSearchRequest sr = new DocSearchRequest(DocDataProvider.GOOGLE_SCHOLAR, searchTerm, offset, numResults);
		String surl = docHandler.createSearchUrlString(sr);
		URL url = new URL(surl);
		
		String rawHtml = DocUtils.fetch(url);
		
		List<CaseDocSearchResult> results = docHandler.parseSearchResults(rawHtml);
		Assert.assertTrue(results.size() == numResults);
		
		for(int i = 0; i < results.size(); i++) {
			CaseDoc dsr = results.get(i);
			System.out.println(dsr);
		}
	}
	
	@Test(enabled = true)
	public void testParseStaticDocResponse() throws Exception {
		
		String raw;
		try {
			URL url = getClass().getClassLoader().getResource("com/tabulaw/server/scrape/gsdoc.htm");
			File f = new File(url.toURI());
			raw = FileUtils.readFileToString(f, "UTF-8");
		}
		catch(IOException e) {
			throw new IllegalStateException(e);
		}
		catch(URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		
		GoogleScholarDocHandler docHandler = new GoogleScholarDocHandler();
		
		DocRef caseDoc = docHandler.parseSingleDocument(raw);
		CaseRef caseRef = caseDoc.getCaseRef();
		
		String docTitle = caseDoc.getTitle();
		String caseCitation = caseRef.getCitation();
		int caseYear = caseRef.getYear();
		String caseParties = caseRef.getParties();
		
		Assert.assertEquals(docTitle, "Board of Supervisors of James City Cty. v. Rowe");
		Assert.assertEquals(caseParties, "Board of Supervisors of James City Cty. v. Rowe");
		Assert.assertEquals(caseCitation, "Board of Supervisors of James City Cty. v. Rowe, 216 SE 2d 199 - Va: Supreme Court 1975");
		Assert.assertEquals(caseYear, "1975");
	}

	@Test(enabled = false)
	public void testParseLiveDocResponse() throws Exception {
		IDocHandler docHandler = new GoogleScholarDocHandler();
		
		// build search url
		String surl = "http://scholar.google.com/scholar_case?case=8264893826744299362&q=allintitle:+rowe&hl=en&as_sdt=2002";
		URL url = new URL(surl);
		
		String rawHtml = DocUtils.fetch(url);
		
		DocRef caseDoc = docHandler.parseSingleDocument(rawHtml);
		System.out.println(caseDoc);
	}
}