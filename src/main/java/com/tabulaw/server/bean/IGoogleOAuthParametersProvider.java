package com.tabulaw.server.bean;

import javax.servlet.http.HttpServletRequest;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;

public interface IGoogleOAuthParametersProvider {

	public final static String TOKEN = "oauth-token";
	public final static String TOKEN_SECRET = "oauth-token-secret";
	public final static String ACCESS_TOKEN = "oauth-access-token";
	public final static String ACCESS_TOKEN_SECRET = "oauth-access-token-secret";

	void setHttpServletRequest(HttpServletRequest request);
	GoogleOAuthParameters getGoogleDocumentsOAuthParameters();
}
