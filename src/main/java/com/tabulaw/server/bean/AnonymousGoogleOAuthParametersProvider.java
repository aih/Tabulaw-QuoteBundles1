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
		String CONSUMER_KEY = "anonymous";
		String CONSUMER_SECRET = "anonymous";
		String host = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort();
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
		oauthParameters.setScope("https://docs.google.com/feeds/");
		if (request.getSession().getAttribute(ACCESS_TOKEN) != null
				&& request.getSession().getAttribute(ACCESS_TOKEN_SECRET) != null) {
			oauthParameters.setOAuthToken((String) request.getSession()
					.getAttribute(ACCESS_TOKEN));
			oauthParameters.setOAuthTokenSecret((String) request.getSession()
					.getAttribute(ACCESS_TOKEN_SECRET));
		} else {
			oauthParameters.setOAuthCallback(host
					+ OAuthAuthorizeServlet.REDIRECT_URL);
			if (request.getSession().getAttribute(TOKEN) != null) {
				oauthParameters.setOAuthToken((String) request.getSession()
						.getAttribute(TOKEN));
			}
			if (request.getSession().getAttribute(TOKEN_SECRET) != null) {
				oauthParameters.setOAuthTokenSecret((String) request
						.getSession().getAttribute(TOKEN_SECRET));
			}
		}
		return oauthParameters;
	}
}
