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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.tabulaw.model.CaseRef;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.util.IOUtils;

/**
 * @author jpk
 */
public class DocUtils {

	static final String htmlPrefixBlock, htmlSuffixBlock;

	static final String jsScriptCallbackBlock, cssHighightStylesBlock;

	static final String docDirPath;

	private static final DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

	static {
		htmlPrefixBlock =
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
						+ "<html><head>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
						+ "<title>${title}</title></head><body>";

		htmlSuffixBlock = "</body></html>";

		jsScriptCallbackBlock =
				"<script type=\"text/javascript\">window.onload=function(){window.parent.${onDocFrameLoaded}(document);}</script>";

		cssHighightStylesBlock = 
			"<style type=\"text/css\">" +
				".highlight{background-color:yellow;} " +
				
				// google scholar specific
				"body{margin-left:40px} " + 
				"a.gsl_pagenum,a.gsl_pagenum2{font-family: Arial, Helvetica, sans-serif;font-size:10px;} " +
				"a.gsl_pagenum{position:absolute;left:0px;color:gray;} " +
				"a.gsl_pagenum2{color:gray;padding:0 4px;} " +
				
			"</style>";

		docDirPath = DocUtils.class.getClassLoader().getResource("").getPath() + "docs" + File.separator;
	}

	/**
	 * Maps mime-types to file extension tokens.
	 * @param fileExt FORMAT: ".doc"
	 * @return the mime-type
	 * @throws IllegalArgumentException When no mime-type is found for the given
	 *         file extension
	 */
	public static String getMimeTypeFromFileExt(String fileExt) throws IllegalArgumentException {
		if(!fileExt.startsWith(".")) {
			fileExt = "." + fileExt;
		}
		fileExt = fileExt.trim().toLowerCase();
		if(".doc".equals(fileExt) || ".docx".equals(fileExt)) return "application/msword";
		if(".txt".equals(fileExt) || ".text".equals(fileExt)) return "text/html";
		if(".htm".equals(fileExt) || ".html".equals(fileExt)) return "text/html";
		throw new IllegalArgumentException("No mime-type mapped for file extension: " + fileExt);
	}

	/**
	 * Is the file named such that is implies it has html content?
	 * @param fname the non-path file name
	 * @return true/false
	 */
	public static boolean isHtmlFileByExtension(String fname) {
		if(fname.endsWith(".htm") || fname.endsWith(".html")) return true;
		return false;
	}

	/**
	 * Is the file named such that is implies it has textual content?
	 * @param fname the non-path file name
	 * @return true/false
	 */
	public static boolean isTextFileByExtension(String fname) {
		if(fname.endsWith(".txt") || fname.endsWith(".text")) return true;
		return false;
	}

	/**
	 * Is the file named such that it implies it is an ms-word doc file?
	 * @param fname the non-path file name
	 * @return true/false
	 */
	public static boolean isMsWordFileByExtension(String fname) {
		if(fname.endsWith(".doc") || fname.endsWith(".docx")) return true;
		return false;
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
		String fname = Integer.toString(Math.abs(doc.hashCode())) + ".html";
		File f = getDocFileRef(fname);
		String htmlContent = doc.getHtmlContent();
		FileUtils.writeStringToFile(f, htmlContent, "UTF-8");
		return f;
	}

	/**
	 * Serializes the state of the given doc ref into a single token.
	 * @param doc doc to serialize
	 * @return serialized doc token
	 */
	// TODO move to DocUploadServlet
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
				catch(/*ParseException*/Exception e) {
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
	 * Generic html-ization of arbitrary input text.
	 * @param sb buffer holding the input text
	 * @param title the title to use in the html header. May be <code>null</code>
	 *        in which case no title will be specified.
	 */
	public static void htmlizeText(StringBuilder sb, String title) {
		String prefix = htmlPrefixBlock.replace("${title}", title == null ? "" : title);
		sb.insert(0, prefix);
		sb.append(htmlSuffixBlock);
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
		conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US)");
		conn.setRequestProperty("Accept", "text/html");
		conn.setRequestProperty("Accept-Encoding", "deflate");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
		// conn.setRequestProperty("Accept-Language",
		// "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		conn.setRequestProperty("Accept-Language", "utf-8;q=0.7,*;q=0.3");

		return conn.getInputStream();
	}

	/**
	 * Fetches the content at a given http url
	 * @param anHttpUrl an http type url from which to fetch content
	 * @return the fetched content
	 * @throws IOException
	 */
	public static String fetch(URL anHttpUrl) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(openHttpUrl(anHttpUrl), "UTF-8"));
			StringBuilder sb = new StringBuilder(1024 * 1024); // 1MB initial capacity
			String line;
			while((line = br.readLine()) != null) {
				// System.out.println(line);
				sb.append(line);
			}
			return sb.toString();
		}
		finally {
			IOUtils.closeQuitely(br);
		}
		// System.out.println(fcontents);
		// if(fcontents.indexOf("</html>") == -1) throw new IllegalStateException();
		// return fcontents;
	}

	private DocUtils() {
	}
}
