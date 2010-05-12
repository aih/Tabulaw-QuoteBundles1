/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;

/**
 * Transport for a single doc hash token.
 * @author jpk
 */
public class DocHashPayload extends Payload {

	/**
	 * The url pointing to the original remote document.
	 */
	private String remoteUrl;

	/**
	 * The unique doc id.
	 */
	private String docHash;

	// private String docHtml;

	/**
	 * Constructor
	 */
	public DocHashPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public DocHashPayload(Status status) {
		super(status);
	}

	/**
	 * @return The unique local doc id which is the non-path filename of the local
	 *         doc on disk.
	 */
	public String getDocHash() {
		return docHash;
	}

	public void setDocHash(String docHash) {
		this.docHash = docHash;
	}

	/**
	 * @return The http url that sourced this doc content.
	 */
	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}
}
