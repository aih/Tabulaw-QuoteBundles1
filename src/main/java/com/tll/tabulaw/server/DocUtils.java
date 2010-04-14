/**
 * The Logic Lab
 * @author jpk
 * @since Mar 25, 2010
 */
package com.tll.tabulaw.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tll.common.model.Model;
import com.tll.tabulaw.common.model.PocModelFactory;

/**
 * @author jpk
 */
public class DocUtils {

	static final DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

	static final String htmlPrefixBlock, htmlSuffixBlock;

	static final String jsScriptCallbackBlock, cssHighightStylesBlock;

	static {
		htmlPrefixBlock =
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
						+ "<html><head>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
						+ "<title>${title}</title></head><body>";

		htmlSuffixBlock = "</body></html>";

		jsScriptCallbackBlock =
				"<script type=\"text/javascript\">" + "window.onload = function(){window.parent.onFrameLoaded(document);}"
						+ "</script>";

		cssHighightStylesBlock = "<style type=\"text/css\">.highlight{background-color:yellow;}</style>";
	}

	public static String dateAsString(Date date) {
		return dateFormat.format(date);
	}

	public static String serializeDocument(Model mDoc) {
		StringBuilder sb = new StringBuilder(1024);

		// doc: "title", "hash",
		sb.append("title:");
		sb.append(mDoc.asString("title"));
		sb.append("|date:");
		sb.append(dateFormat.format(mDoc.getPropertyValue("date")));
		sb.append("|hash:");
		sb.append(mDoc.asString("hash"));

		if(mDoc.propertyExists("caseRef")) {
			sb.insert(0, "[casedoc]");

			// case: "parties", "citation", "url", "year", "date"
			sb.append("|parties:");
			sb.append(mDoc.asString("caseRef.parties"));
			sb.append("|citation:");
			sb.append(mDoc.asString("caseRef.citation"));
			sb.append("|url:");
			sb.append(mDoc.asString("caseRef.url"));
			sb.append("|year:");
			sb.append(mDoc.asString("caseRef.year"));
		}
		else {
			// throw new IllegalStateException("Un-handled document model type");
			// for now, assume it is a contract doc
			sb.insert(0, "[doc]");
		}

		sb.append("\n"); // newline

		return sb.toString();
	}

	public static Model deserializeDocument(String s) {
		// check to see if we have a serializer first line
		if(s.charAt(0) != '[') return null;

		// doc related
		String title = null, hash = null;

		// case related
		String parties = null, citation = null, url = null, year = null;
		Date date = null;

		int nli = s.indexOf('\n');
		String firstline = s.substring(0, nli);
		int eti = firstline.indexOf(']');

		String type = s.substring(1, eti);
		firstline = firstline.substring(eti + 1);

		String[] sarr1 = firstline.split("\\|");
		for(String sub : sarr1) {
			String[] sarr2 = sub.split(":");
			String name = sarr2[0];
			String value = (sarr2.length == 2) ? sarr2[1] : "";

			// doc related
			if("title".equals(name)) {
				title = value;
			}
			else if("date".equals(name)) {
				try {
					date = dateFormat.parse(value);
				}
				catch(ParseException e) {
					throw new IllegalArgumentException("Un-parseable date string: " + value);
				}
			}
			else if("hash".equals(name)) {
				hash = value;
			}

			else if("casedoc".equals(type)) {
				// case related
				if("parties".equals(name)) {
					parties = value;
				}
				else if("citation".equals(name)) {
					citation = value;
				}
				else if("url".equals(name)) {
					url = value;
				}
				else if("year".equals(name)) {
					year = value;
				}
			}
		}

		if("casedoc".equals(type))
			return PocModelFactory.get().buildCaseDoc(title, hash, date, parties, citation, url, year);
		else if("doc".equals(type))
			return PocModelFactory.get().buildDoc(title, hash, date);
		else
			throw new IllegalArgumentException("Unhandled doc type: " + type);
	}

	public static int docHash(String remoteUrl) {
		return Math.abs(remoteUrl.hashCode());
	}

	public static String localDocFilename(int docHash) {
		return "doc_" + docHash + ".htm";
	}

	/**
	 * "Localizes" the odc by injecting local css and js blocks needed for
	 * client-side doc functionality if not already present.
	 * <p>
	 * Also, wraps the given html string with html, head, body tags if not present
	 * @param doc html content string
	 * @param docTitle
	 * @throws IllegalArgumentException When the localization fails due to
	 *         mal-formed html tags in the given html string
	 */
	public static void localizeDoc(StringBuilder doc, String docTitle) throws IllegalArgumentException {
		if(doc.indexOf("<html") == -1 && doc.indexOf("<HTML") == -1) {
			String rpl = docTitle == null ? "" : docTitle;
			doc.insert(0, htmlPrefixBlock.replace("${title}", rpl));
			doc.append(htmlSuffixBlock);
		}
		// inject js callback hook in window.onload
		if(doc.indexOf(cssHighightStylesBlock) == -1) {
			int index = doc.indexOf("</head>");
			if(index == -1) index = doc.indexOf("</HEAD>");
			if(index == -1) throw new IllegalArgumentException("No closing html head tag found.");
			doc.insert(index, cssHighightStylesBlock);
			index = doc.indexOf("</head>");
			if(index == -1) index = doc.indexOf("</HEAD>");
			doc.insert(index, jsScriptCallbackBlock);
		}
	}

	/**
	 * Opens a connection for the given http url setting the user-agent property
	 * to guard against possible 403 response.
	 * @param anHttpUrl
	 * @return the opened connection as an {@link InputStream}
	 * @throws IOException
	 */
	public static InputStream openHttpUrl(URL anHttpUrl) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) anHttpUrl.openConnection();

		// IMPT: we must set the user-agent otherwise a possible 403 response
		conn
				.setRequestProperty("User-agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.3 (KHTML, like Gecko) Chrome/5.0.360.0 Safari/533.3");
		conn.setRequestProperty("Accept", "text/html");
		conn.setRequestProperty("Accept-Encoding", "deflate");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
		conn.setRequestProperty("Accept-Language", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");

		return conn.getInputStream();
	}

	/**
	 * Fetches the content at a given http url
	 * @param anHttpUrl an http type url from which to fetch content
	 * @return the fetched content
	 * @throws IOException
	 */
	public static String fetch(URL anHttpUrl) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(openHttpUrl(anHttpUrl), "UTF-8"));
		StringBuilder sb = new StringBuilder(102400);
		String line;
		while((line = br.readLine()) != null) {
			// System.out.println(line);
			sb.append(line);
		}
		br.close();

		return sb.toString();
		// System.out.println(fcontents);
		// if(fcontents.indexOf("</html>") == -1) throw new IllegalStateException();
		// return fcontents;
	}

	private DocUtils() {
	}
}
