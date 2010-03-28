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
	
	static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	static final String htmlPrefixBlock, htmlSuffixBlock;

	static final String jsScriptCallbackBlock, cssHighightStylesBlock;

	static {
		htmlPrefixBlock = 
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + 
			"<html><head>" +
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
			"<title>${title}</title></head><body>";

		htmlSuffixBlock = "</body></html>";
		
		jsScriptCallbackBlock = 
			"<script type=\"text/javascript\">"
			+ "window.onload = function(){window.parent.onFrameLoaded(document);}"
			+ "</script>";
		
		cssHighightStylesBlock = 
			"<style type=\"text/css\">.highlight{background-color:yellow;}</style>";
	}
	
	public static String serializeCaseDocModel(Model caseDoc) {
		StringBuilder sb = new StringBuilder(1024);
		sb.append("citation:");
		sb.append(caseDoc.asString("case.citation"));
		sb.append("|hash:");
		sb.append(caseDoc.asString("hash"));
		sb.append("|url:");
		sb.append(caseDoc.asString("case.url"));
		sb.append("|date:");
		String sdate = dateFormat.format(caseDoc.getPropertyValue("case.date"));
		sb.append(sdate);
		sb.append("\n"); // newline
		return sb.toString();
	}

	public static Model deserializeCaseDocModel(String s) {
		if(s.startsWith("citation")) {
			// case doc
			String citation = null, hash = null, url = null;
			Date date = null;
			
			String firstline = s.substring(0, s.indexOf('\n'));
			String[] sarr1 = firstline.split("\\|");
			for(String sub : sarr1) {
				String[] sarr2 = sub.split(":");
				String name = sarr2[0], value = sarr2[1];
				if("citation".equals(name)) {
					citation = value;
				}
				else if("hash".equals(name)) {
					hash = value;
				}
				else if("url".equals(name)) {
					url = value;
				}
				else if("date".equals(name)) {
					try {
						date = dateFormat.parse(value);
					}
					catch(ParseException e) {
						throw new IllegalArgumentException("Un-parseable date string: " + value);
					}
				}
			}
			PocModelFactory.get().buildCaseDoc(citation, hash, null, citation, url, null, date);
		}
		
		// unhandled type
		return null;
	}

	public static int docHash(String remoteUrl) {
		return Math.abs(remoteUrl.hashCode());
	}
	
	public static String localDocFilename(int docHash) {
		return "doc_" + docHash + ".htm";
	}
	
	/**
	 * "Localizes" the odc by injecting local css and js blocks needed for client-side doc functionality if
	 * not already present.
	 * <p>Also, wraps the given html string with html, head, body tags if not present
	 * @param doc html content string
	 * @param docTitle
	 */
	public static void localizeDoc(StringBuilder doc, String docTitle) {
		if(doc.indexOf("<html>") == -1) {
			String rpl = docTitle == null ? "" : docTitle;
			doc.insert(0,htmlPrefixBlock.replace("${title}", rpl));
			doc.append(htmlSuffixBlock);
		}
		// inject js callback hook in window.onload
		if(doc.indexOf(cssHighightStylesBlock) == -1) {
			int index = doc.indexOf("</head>");
			assert index > 0;
			doc.insert(index, cssHighightStylesBlock);
			index = doc.indexOf("</head>");
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
		conn.setRequestProperty("User-agent", "Mozilla/4.0");

		return conn.getInputStream();
	}

	/**
	 * Fetches the content at a given http url
	 * @param anHttpUrl an http type url from which to fetch content
	 * @return the fetched content
	 * @throws IOException
	 */
	public static String fetch(URL anHttpUrl) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(openHttpUrl(anHttpUrl)));
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
