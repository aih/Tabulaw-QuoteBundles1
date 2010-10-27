package com.tabulaw.server.bean;

import javax.servlet.http.HttpServletRequest;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;

public interface IGoogleOAuthParametersProvider {

	final static String OAUTH_PARAMETERS = "oauth-parameters";
	final static String OAUTH_ACCESS_PARAMETERS = "oauth-access-parameters";

	final static boolean DATABASE_BASED_ACCESS_TOKEN = false;

	void setHttpServletRequest(HttpServletRequest request);

	GoogleOAuthParameters getGoogleDocumentsOAuthParameters();
}
