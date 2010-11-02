package com.tabulaw.oauth;

import javax.servlet.http.HttpServletRequest;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;

public interface OAuthParametersProvider {

	final static String OAUTH_PARAMETERS = "oauth-parameters";
	final static String OAUTH_ACCESS_PARAMETERS = "oauth-access-parameters";

	final static boolean DATABASE_BASED_ACCESS_TOKEN = true;

	void setHttpServletRequest(HttpServletRequest request);

	GoogleOAuthParameters getGoogleDocumentsOAuthParameters();
}
