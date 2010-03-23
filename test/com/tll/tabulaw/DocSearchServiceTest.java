/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2010
 */
package com.tll.tabulaw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.tll.tabulaw.common.data.rpc.DocSearchResult;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tll.tabulaw.server.rpc.DocSearchService;

/**
 * @author jpk
 */
@Test
public class DocSearchServiceTest {
	
	public void testParseGoogleScholarResponse() throws Exception {
		int numResults = 2;
		String searchTerm = "temp";
		URL url = new URL("http://scholar.google.com/scholar?as_q="+searchTerm+"&num="+numResults+"&btnG=Search+Scholar&as_epq=&as_oq=&as_eq=&as_occt=any&as_sauthors=&as_publication=&as_ylo=&as_yhi=&as_sdt=2&as_sdts=5&hl=en");

		Reader input = DocSearchService.createReader(url);
		Document doc = DocSearchService.digestHttpContent(input);
		
		List<DocSearchResult> results = DocSearchService.parseResponse(DocDataProvider.GOOGLE_SCHOLAR, doc);
		Assert.assertTrue(results.size() == numResults);
		
		for(int i = 0; i < results.size(); i++) {
			DocSearchResult dsr = results.get(i);
			System.out.println(dsr);
		}
	}

	@Test(enabled = false)
	public void testGetUrlContent() throws Exception {
		URL url = new URL("http://scholar.google.com/scholar?as_q=term&num=10&btnG=Search+Scholar&as_epq=&as_oq=&as_eq=&as_occt=any&as_sauthors=&as_publication=&as_ylo=&as_yhi=&as_sdt=2&as_sdts=5&hl=en");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-agent", "Mozilla/4.0");

		InputStream is = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		BufferedWriter bw = new BufferedWriter(new StringWriter(1024 * 20));

		String line;
		while((line = br.readLine()) != null) {
			System.out.println(line);
			bw.write(line);
			bw.newLine();
		}

		br.close();

		// String s = bw.toString();
	}
}
