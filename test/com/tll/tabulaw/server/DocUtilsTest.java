/**
 * The Logic Lab
 * @author jpk
 * @since Mar 26, 2010
 */
package com.tll.tabulaw.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link DocUtils} methods.
 * @author jpk
 */
@Test
public class DocUtilsTest {

	/**
	 * Tests the localizing of a doc string
	 * @throws Exception
	 */
	@Test(enabled = true)
	public void localizeDocTest() throws Exception {
		String html = "<p>This is test html.</p>";

		StringBuilder sb = new StringBuilder(html);
		DocUtils.localizeDoc(sb, "test title");
		String localized = sb.toString();

		int htmlIndex = localized.indexOf("<html>");
		int endHtmlIndex = localized.indexOf("</html>");

		int headIndex = localized.indexOf("<head>");
		int endHeadIndex = localized.indexOf("</head>");

		int bodyIndex = localized.indexOf("<body>");
		int endBodyIndex = localized.indexOf("</body>");
		
		int titleIndex = localized.indexOf("<title>test title</title>");
		
		Assert.assertTrue(titleIndex > 0);
		
		Assert.assertTrue(htmlIndex >= 0);
		Assert.assertTrue(endHtmlIndex > htmlIndex);

		Assert.assertTrue(headIndex > htmlIndex);
		Assert.assertTrue(endHeadIndex > headIndex);

		Assert.assertTrue(bodyIndex > headIndex);
		Assert.assertTrue(endBodyIndex > bodyIndex);

		int localCssIndex = localized.indexOf(DocUtils.cssHighightStylesBlock);
		int localJsIndex = localized.indexOf(DocUtils.jsScriptCallbackBlock);

		Assert.assertTrue(localCssIndex >= 0);
		Assert.assertTrue(localJsIndex >= 0);
		Assert.assertTrue(localCssIndex < localJsIndex);

		Assert.assertTrue(localJsIndex < endHeadIndex);
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
