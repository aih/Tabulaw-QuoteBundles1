package com.tabulaw.oauth;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.tabulaw.model.User;
import com.tabulaw.server.UserContext;

public class GoogleAnonymousOAuthParametersProvider implements
		OAuthParametersProvider {

	private final static Log log = LogFactory
			.getLog(GoogleAnonymousOAuthParametersProvider.class);

	private HttpServletRequest request;

	@Override
	public void setHttpServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public GoogleOAuthParameters getGoogleDocumentsOAuthParameters() {
		if (OAuthParametersProvider.DATABASE_BASED_ACCESS_TOKEN) {
			UserContext uc = (UserContext) request.getSession(false)
					.getAttribute(UserContext.KEY);
			User user = uc.getUser();
			if (user.getOAuthParameters() != null
					&& user.getOAuthParametersExtra() != null) {
				return new OAuthParameters(user.getOAuthParameters(),
						user.getOAuthParametersExtra());
			}
		} else {
			if (request.getSession().getAttribute(OAUTH_ACCESS_PARAMETERS) != null) {
				return (GoogleOAuthParameters) request.getSession()
						.getAttribute(OAUTH_ACCESS_PARAMETERS);
			}
		}
		if (request.getSession().getAttribute(OAUTH_PARAMETERS) != null) {
			return (GoogleOAuthParameters) request.getSession().getAttribute(
					OAUTH_PARAMETERS);
		} else {
			String CONSUMER_KEY = "dev.tabulaw.com";
			String CONSUMER_SECRET = "c72cJB/WIXJuAtc9Ob8yq4GT";
			GoogleOAuthParameters oauthParameters = new OAuthParameters();
			oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
			oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
			oauthParameters.setScope("https://docs.google.com/feeds/");
			String host = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort() + request.getContextPath();
			String callbackUrl = host + OAuthAuthorizeServlet.REDIRECT_URL;
			log.debug("AOUTH Callback URL: " + host);
			oauthParameters.setOAuthCallback(callbackUrl);
			return oauthParameters;
		}
	}
}
