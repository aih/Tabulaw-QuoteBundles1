package com.tabulaw.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

@SuppressWarnings("serial")
public class OAuthPersistAccessToken extends HttpServlet {

	private final static Log log = LogFactory.getLog(OAuthPersistAccessToken.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IOUtils.write(
				"<script> window.opener.eval('onPopupWindowClose();');</script>",
				response.getOutputStream());
		IOUtils.write("<script> self.close(); </script>",
				response.getOutputStream());

		String accessToken = (String) request.getSession().getAttribute(
				"access-token");
		String accessTokenSecret = (String) request.getSession().getAttribute(
				"access-token-secret");

		GoogleOAuthParameters oauthParameters = (GoogleOAuthParameters) request
				.getSession().getAttribute("oauth-parameters");

		log.debug("Callback: " + request.getQueryString());

		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
				new OAuthHmacSha1Signer());

		if (accessToken == null || accessTokenSecret == null) {
			persistAccessToken(oauthParameters, oauthHelper, request);
		} else {
			oauthParameters.setOAuthToken(accessToken);
			oauthParameters.setOAuthTokenSecret(accessTokenSecret);
		}
	}

	private void persistAccessToken(GoogleOAuthParameters oauthParameters,
			GoogleOAuthHelper oauthHelper, HttpServletRequest request) {
		try {
			oauthHelper.getOAuthParametersFromCallback(
					request.getQueryString(), oauthParameters);
			String accessToken = oauthHelper.getAccessToken(oauthParameters);
			String accessTokenSecret = oauthParameters.getOAuthTokenSecret();
			request.getSession().setAttribute("access-token", accessToken);
			request.getSession().setAttribute("access-token-secret",
					accessTokenSecret);
		} catch (OAuthException e) {
			log.error("OAuth - persist access token error", e);
		}
	}
}
