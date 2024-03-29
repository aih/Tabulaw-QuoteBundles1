/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 26, 2010
 */
package com.tabulaw.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.tabulaw.model.CaseRef;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;

/**
 * Tests {@link DocUtils} methods.
 * @author jpk
 */
@Test(groups = "server")
public class DocUtilsTest {
	
	@Test(enabled = true)
	public void testSerializeCaseDocModel() throws Exception {
		DocRef m = EntityFactory.get().buildCaseDoc("docTitle", new Date(), false, "parties", "reftoken", "docLoc", "court", "url", 1975, 1, 20);
		String s = DocUtils.serializeDocument(m);
		
		Calendar c = Calendar.getInstance();
		String snow = c.get(Calendar.MONTH) + 1 + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR);  
		
		String expected = "[casedoc]id::|title::docTitle|date::" + snow + "|parties::parties|reftoken::reftoken|docLoc::docLoc|court::court|url::url|year::1975|firstPageNumber::1|lastPageNumber::20\n";
		Assert.assertEquals(s, expected);
	}

	@Test(enabled = true)
	public void testDeserializeCaseDocModel() throws Exception {
		Calendar c = Calendar.getInstance();
		c.clear(Calendar.HOUR_OF_DAY);
		c.clear(Calendar.HOUR);
		c.clear(Calendar.AM_PM);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		Date date = c.getTime();
		
		String snow = c.get(Calendar.MONTH) + 1 + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR);  
		String sized = "[casedoc]id::|title::docTitle|date::" + snow + "|parties::parties|reftoken::reftoken|docLoc::docLoc|court::court|url::url|year::1975|firstPageNumber::1|lastPageNumber::20\n";
		
		DocRef m = DocUtils.deserializeDocToken(sized);
		
		Assert.assertEquals(m.getTitle(), "docTitle");
		Assert.assertEquals(m.getDate(), date);
		
		// case related
	/*	CaseReference caseRef = m.getCaseRef();
		Assert.assertEquals(caseRef.getParties(), "parties");
		Assert.assertEquals(caseRef.getReftoken(), "reftoken");
		Assert.assertEquals(caseRef.getDocLoc(), "docLoc");
		Assert.assertEquals(caseRef.getCourt(), "court");
		Assert.assertEquals(caseRef.getUrl(), "url");
		Assert.assertEquals(caseRef.getYear(), 1975);
		Assert.assertEquals(caseRef.getFirstPageNumber(), 1);
		Assert.assertEquals(caseRef.getLastPageNuber(), 20);*/
	}
	
	/**
	 * Tests the fetching of html content from a doc search related url.
	 * @throws Exception
	 */
	@Test(enabled = false)
	public void fetchTest() throws Exception {
		URL url =
				new URL(
						"http://scholar.google.com/scholar?as_q=term&num=10&btnG=Search+Scholar&as_epq=&as_oq=&as_eq=&as_occt=any&as_sauthors=&as_publication=&as_ylo=&as_yhi=&as_sdt=2&as_sdts=5&hl=en");

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
	}
}
