package com.tabulaw.server.bean;

import javax.servlet.http.HttpServletRequest;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.tabulaw.server.OAuthAuthorizeServlet;

public class AnonymousGoogleOAuthParametersProvider implements
		IGoogleOAuthParametersProvider {

	private HttpServletRequest request;

	@Override
	public void setHttpServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public GoogleOAuthParameters getGoogleDocumentsOAuthParameters() {
		if (request.getSession().getAttribute(OAUTH_ACCESS_PARAMETERS) != null) {
			return (GoogleOAuthParameters) request.getSession().getAttribute(
					OAUTH_ACCESS_PARAMETERS);
		} else if (request.getSession().getAttribute(OAUTH_PARAMETERS) != null) {
			return (GoogleOAuthParameters) request.getSession().getAttribute(
					OAUTH_PARAMETERS);
		} else {
			String CONSUMER_KEY = "anonymous";
			String CONSUMER_SECRET = "anonymous";
			GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
			oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
			oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
			oauthParameters.setScope("https://docs.google.com/feeds/");
			String host = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort();
			oauthParameters.setOAuthCallback(host
					+ OAuthAuthorizeServlet.REDIRECT_URL);
			return oauthParameters;
		}
	}
}
