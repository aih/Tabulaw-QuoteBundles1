/**
 * The Logic Lab
 * @author jpk
 * @since Mar 25, 2010
 */
package com.tabulaw.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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

import com.tabulaw.common.model.CaseRef;
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
				"<script type=\"text/javascript\">" + "window.onload = function(){window.parent.onFrameLoaded(document);}"
						+ "</script>";

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
	 * Creates {@link File} instance given a doc id.
	 * @param docId the doc hash
	 * @return new {@link File} instance pointing to the doc on disk
	 * @throws IllegalArgumentException when the op fails
	 */
	public static File getDocRef(String docId) throws IllegalArgumentException {
		File f = new File(docDirPath + docId);
		return f;
	}

	/**
	 * Serializes the state of the given doc ref into a single token.
	 * @param doc doc to serialize
	 * @return serialized doc token
	 */
	public static String serializeDocument(DocRef doc) {
		StringBuilder sb = new StringBuilder(1024);

		// doc: "title", "hash",
		sb.append("title::");
		sb.append(doc.getTitle());
		sb.append("|date::");
		sb.append(dateFormat.format(doc.getDate()));
		sb.append("|hash::");
		sb.append(doc.getHash());

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
	 * Extracts the serialized doc ref token from a locally cached doc file.
	 * @param fdoc ref to a locally cached doc file
	 * @return the contained serialized doc ref token or <code>null</code> if not
	 *         found in the doc file's contents
	 * @throws IOException
	 */
	public static String getSerializedDocToken(File fdoc) throws IOException {
		// for now just load the entire doc as a string for simplicity's sake
		String s = FileUtils.readFileToString(fdoc, "UTF-8");

		// ensure we hava a doc serialize token at head
		if(!s.startsWith("[doc]") && !s.startsWith("[casedoc]")) return null;

		int index = s.indexOf('\n');
		if(index == -1 || index >= s.length() - 2) return null;
		return s.substring(0, index + 1); // include the newline char
	}

	/**
	 * @param fdoc
	 * @return
	 * @throws IOException
	 */
	public static DocRef deserializeDocument(File fdoc) throws IOException {
		String stoken = getSerializedDocToken(fdoc);
		return stoken == null ? null : deserializeDocToken(stoken);
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
		String title = null, hash = null;

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
			else if("hash".equals(name)) {
				hash = value;
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
			return EntityFactory.get().buildCaseDoc(title, hash, date, parties, reftoken, docLoc, court, url, year);
		else if("doc".equals(type))
			return EntityFactory.get().buildDoc(title, hash, date);
		else
			throw new IllegalArgumentException("Unhandled doc type: " + type);
	}

	/**
	 * Creates a int hashcode that is intended to be unique against all other
	 * locally cached docs.
	 * @param remoteUrl the remove doc url
	 * @return hash code of the given remote url
	 */
	// TODO this routine may not return unique ids as is really intended because
	// it relies on the java string hashCode function!
	public static int docHash(String remoteUrl) {
		return Math.abs(remoteUrl.hashCode());
	}
	
	/**
	 * Removes a doc file from disk
	 * @param filename i.e. the doc hash
	 */
	public static void deleteDoc(String filename) {
		if(filename == null) throw new NullPointerException();
		if(!getDocRef(filename).delete()) {
			throw new IllegalArgumentException("Doc of filename: '" + filename + "' was not deleted.");
		}
	}

	/**
	 * Creates a new file on disk given a unique doc hash.
	 * @param docHash doc hash for which to create a new file
	 * @param docRef the doc to write to disk
	 * @throws IllegalArgumentException When the doc file already exists
	 * @throws IOException Upon error writing doc to disk
	 */
	public static void createNewDoc(int docHash, DocRef docRef) throws IllegalArgumentException, IOException {
		if(docRef == null) throw new NullPointerException();

		String filename = localDocFilename(docHash);
		File fdoc = new File(docDirPath + File.separator + filename);
		if(fdoc.exists()) throw new IllegalArgumentException("Doc already exists for hash: " + docHash);
		docRef.setHash(filename);

		// write to disk
		String html = docRef.getHtmlContent();
		if(html == null) html = "";
		StringBuilder sb = new StringBuilder(html);
		localizeDoc(sb, docRef.getTitle());

		// pre-pend serialized doc token at start of file
		String stoken = serializeDocument(docRef);
		sb.insert(0, stoken);
		
		FileUtils.writeStringToFile(fdoc, sb.toString());
	}

	/**
	 * [Re-]sets the html document content on disk.
	 * @param localDocFilename name of the doc file
	 * @param htmlContent the html content
	 * @throws IOException
	 */
	public static void setDocContent(String localDocFilename, String htmlContent) throws IOException {
		if(localDocFilename == null || htmlContent == null) throw new NullPointerException();

		File fdoc = new File(docDirPath + File.separator + localDocFilename);
		if(!fdoc.exists()) throw new IllegalArgumentException("Doc of name: '" + localDocFilename + "' not found.");

		DocRef docRef = deserializeDocument(fdoc);

		// clear out existing doc file contents
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fdoc);
			fos.write((new String()).getBytes());
		}
		finally {
			if(fos != null) try {
				fos.close();
			}
			catch(IOException e) {
				// eat it
			}
		}

		// re-write it
		StringBuilder sb = new StringBuilder(htmlContent);
		String docTitle = docRef.getTitle();
		localizeDoc(sb, docTitle);
		String stoken = serializeDocument(docRef);
		sb.insert(0, stoken);
		FileUtils.writeStringToFile(fdoc, sb.toString());
	}

	/**
	 * Creates the local doc filename given the raw hash code.
	 * <p>
	 * This return value is what is used as the actual doc hash in the context of
	 * the doc ref entity.
	 * @param docHash
	 * @return
	 */
	public static String localDocFilename(int docHash) {
		return "doc_" + docHash + ".htm";
	}

	/**
	 * "Localizes" doc html content by injecting local css and js blocks needed
	 * for client-side doc functionality if not already present.
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
