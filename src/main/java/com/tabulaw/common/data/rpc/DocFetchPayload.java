/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;

/**
 * Remote doc fetch payload.
 * @author jpk
 */
public class DocFetchPayload extends Payload {

	/**
	 * The url pointing to the original remote document.
	 */
	private String remoteUrl;

	private String localUrl;

	// private String docHtml;

	/**
	 * Constructor
	 */
	public DocFetchPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public DocFetchPayload(Status status) {
		super(status);
	}

	/**
	 * @return The url of the localized (fetched) version of hte document.
	 */
	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	/**
	 * @return The http url that sourced this doc content. This can be used as a
	 *         unique hash for the doc.
	 */
	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}
}
