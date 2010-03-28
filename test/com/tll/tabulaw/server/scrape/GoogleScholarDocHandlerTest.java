/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tll.tabulaw.server.scrape;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tll.common.model.Model;
import com.tll.tabulaw.common.data.rpc.CaseDocSearchResult;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tll.tabulaw.server.DocUtils;
import com.tll.util.ClassUtil;

/**
 * Tests {@link GoogleScholarDocHandler}
 * @author jpk
 */
@Test
public class GoogleScholarDocHandlerTest {
	
	@Test(enabled = true)
	public void testParseStaticSearchResultsResponse() throws Exception {
		
		String raw;
		try {
			URL url = ClassUtil.getResource("com/tll/tabulaw/server/scrape/testGoogleScholarSearchResults.htm");
			File f = new File(url.toURI());
			raw = FileUtils.readFileToString(f);
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
			CaseDocSearchResult dsr = results.get(i);
			System.out.println(dsr);
		}
	}

	@Test(enabled = true)
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
			CaseDocSearchResult dsr = results.get(i);
			System.out.println(dsr);
		}
	}
	
	@Test(enabled = true)
	public void testParseStaticDocResponse() throws Exception {
		
		String raw;
		try {
			URL url = getClass().getClassLoader().getResource("gsdoc.htm");
			File f = new File(url.toURI());
			raw = FileUtils.readFileToString(f);
		}
		catch(IOException e) {
			throw new IllegalStateException(e);
		}
		catch(URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		
		GoogleScholarDocHandler docHandler = new GoogleScholarDocHandler();
		
		Model caseDoc = docHandler.parseSingleDocument(raw);
		System.out.println(caseDoc);
	}

	@Test(enabled = true)
	public void testParseLiveDocResponse() throws Exception {
		IDocHandler docHandler = new GoogleScholarDocHandler();
		
		// build search url
		String surl = "http://scholar.google.com/scholar_case?case=8264893826744299362&q=allintitle:+rowe&hl=en&as_sdt=2002";
		URL url = new URL(surl);
		
		String rawHtml = DocUtils.fetch(url);
		
		Model caseDoc = docHandler.parseSingleDocument(rawHtml);
		System.out.println(caseDoc);
	}
}
