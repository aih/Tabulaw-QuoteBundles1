/**
 * The Logic Lab
 * @author jpk
 * @since Mar 25, 2010
 */
package com.tabulaw.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocContent;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;

/**
 * @author jpk
 */
public class DocUtils {

	static final DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

	static final String htmlPrefixBlock, htmlSuffixBlock;

	static final String jsScriptCallbackBlock, cssHighightStylesBlock;

	static final String docDirPath;

	static {
		htmlPrefixBlock =
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
						+ "<html><head>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
						+ "<title>${title}</title></head><body>";

		htmlSuffixBlock = "</body></html>";

		jsScriptCallbackBlock =
				"<script type=\"text/javascript\">window.onload=function(){window.parent.${onDocFrameLoaded}(document);}</script>";

		cssHighightStylesBlock = "<style type=\"text/css\">.highlight{background-color:yellow;}</style>";

		docDirPath = DocUtils.class.getClassLoader().getResource("").getPath() + "docs" + File.separator;
	}

	/**
	 * @return ref to the directory containing all cached docs on disk.
	 */
	public static File getDocDirRef() {
		File f = new File(docDirPath);
		return f;
	}

	/**
	 * Creates {@link File} instance given a non-path filename.
	 * @param filename the filename to employ
	 * @return new {@link File} instance pointing to the doc on disk
	 * @throws IllegalArgumentException when the op fails
	 */
	public static File getDocFileRef(String filename) throws IllegalArgumentException {
		if(StringUtils.isEmpty(filename)) throw new IllegalArgumentException();
		File f = new File(docDirPath + filename);
		return f;
	}

	/**
	 * Creates a file under the doc dir with the html content of the given doc
	 * written to it.
	 * @param doc the doc to write to disk
	 * @return The newly created file
	 * @throws IOException
	 */
	public static File docContentsToFile(DocContent doc) throws IOException {
		String fname = Integer.toString(Math.abs(doc.hashCode()));
		File f = DocUtils.getDocFileRef(fname);
		String htmlContent = doc.getHtmlContent();
		FileUtils.writeStringToFile(f, htmlContent, "UTF-8");
		return f;
	}

	/**
	 * Serializes the state of the given doc ref into a single token.
	 * @param doc doc to serialize
	 * @return serialized doc token
	 */
	public static String serializeDocument(DocRef doc) {
		StringBuilder sb = new StringBuilder(1024);

		String docId = doc.getId();
		sb.append("id::");
		sb.append(docId == null ? "" : docId);
		sb.append("|title::");
		sb.append(doc.getTitle());
		sb.append("|date::");
		sb.append(dateFormat.format(doc.getDate()));

		CaseRef caseRef = doc.getCaseRef();
		if(caseRef != null) {
			sb.insert(0, "[casedoc]");

			sb.append("|parties::");
			sb.append(caseRef.getParties());
			sb.append("|reftoken::");
			sb.append(caseRef.getReftoken());
			sb.append("|docLoc::");
			sb.append(caseRef.getDocLoc());
			sb.append("|court::");
			sb.append(caseRef.getCourt());
			sb.append("|url::");
			sb.append(caseRef.getUrl());
			sb.append("|year::");
			sb.append(caseRef.getYear());
		}
		else {
			// throw new IllegalStateException("Un-handled document model type");
			// for now, assume it is a contract doc
			sb.insert(0, "[doc]");
		}

		sb.append("\n"); // newline

		return sb.toString();
	}

	/**
	 * Creates a doc ref entity given the serialized doc token.
	 * @param stoken serialized doc token
	 * @return newly created {@link DocRef} entity or <code>null</code> if the
	 *         format is un-recognized.
	 */
	public static DocRef deserializeDocToken(String stoken) {
		// check to see if we have a serializer first line
		if(stoken.charAt(0) != '[') return null;

		// doc related
		String title = null;

		// case related
		String parties = null, reftoken = null, docLoc = null, court = null, url = null, year = null;
		Date date = null;

		int nli = stoken.indexOf('\n');
		String firstline = stoken.substring(0, nli);
		int eti = firstline.indexOf(']');

		String type = stoken.substring(1, eti);
		firstline = firstline.substring(eti + 1);

		String[] sarr1 = firstline.split("\\|");
		for(String sub : sarr1) {
			String[] sarr2 = sub.split("::");
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

			else if("casedoc".equals(type)) {
				// case related
				if("parties".equals(name)) {
					parties = value;
				}
				else if("reftoken".equals(name)) {
					reftoken = value;
				}
				else if("docLoc".equals(name)) {
					docLoc = value;
				}
				else if("court".equals(name)) {
					court = value;
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
			return EntityFactory.get().buildCaseDoc(title, date, parties, reftoken, docLoc, court, url, year);
		else if("doc".equals(type))
			return EntityFactory.get().buildDoc(title, date);
		else
			throw new IllegalArgumentException("Unhandled doc type: " + type);
	}

	/**
	 * "Localizes" doc html content by injecting local css and js blocks needed
	 * for client-side doc functionality if not already present.
	 * <p>
	 * Also, wraps the given html string with html, head, body tags if not present
	 * @param doc html content string
	 * @param docId required
	 * @param docTitle
	 * @throws IllegalArgumentException When the localization fails due to
	 *         mal-formed html tags in the given html string
	 */
	public static void localizeDoc(StringBuilder doc, String docId, String docTitle) throws IllegalArgumentException {
		if(doc == null || docId == null) throw new NullPointerException();

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

			String docFrameLoadedFnName = "onDocFrameLoaded_" + docId;
			String jsBlock = jsScriptCallbackBlock;
			jsBlock = jsBlock.replace("${onDocFrameLoaded}", docFrameLoadedFnName);

			doc.insert(index, jsBlock);
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
