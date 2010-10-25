package com.tabulaw.server.bean;

import javax.servlet.http.HttpServletRequest;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;

public interface IGoogleOAuthParametersProvider {

	public final static String OAUTH_PARAMETERS = "oauth-parameters";
	public final static String OAUTH_ACCESS_PARAMETERS = "oauth-access-parameters";

	void setHttpServletRequest(HttpServletRequest request);
	GoogleOAuthParameters getGoogleDocumentsOAuthParameters();
}
