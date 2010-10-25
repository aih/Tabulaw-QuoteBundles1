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
import com.tabulaw.server.bean.AnonymousGoogleOAuthParametersProvider;
import com.tabulaw.server.bean.IGoogleOAuthParametersProvider;

@SuppressWarnings("serial")
public class OAuthPersistAccessTokenServlet extends HttpServlet {

	private final static Log log = LogFactory
			.getLog(OAuthPersistAccessTokenServlet.class);

	private IGoogleOAuthParametersProvider authParametersProvider = new AnonymousGoogleOAuthParametersProvider();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IOUtils.write(
				"<script> window.opener.eval('onPopupWindowClose();');</script>",
				response.getOutputStream());
		IOUtils.write("<script> self.close(); </script>",
				response.getOutputStream());

		String accessToken = (String) request.getSession().getAttribute(
				"oauth-access-token");
		String accessTokenSecret = (String) request.getSession().getAttribute(
				"oauth-access-token-secret");

		authParametersProvider.setHttpServletRequest(request);
		GoogleOAuthParameters oauthParameters = authParametersProvider
				.getGoogleDocumentsOAuthParameters();

		log.debug("Callback: " + request.getQueryString());

		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
				new OAuthHmacSha1Signer());

		if (accessToken != null && accessTokenSecret != null) {
			persistAccessToken(oauthParameters, oauthHelper, request);
		}
	}

	private void persistAccessToken(GoogleOAuthParameters oauthParameters,
			GoogleOAuthHelper oauthHelper, HttpServletRequest request) {
		try {
			oauthHelper.getOAuthParametersFromCallback(
					request.getQueryString(), oauthParameters);
			String accessToken = oauthHelper.getAccessToken(oauthParameters);
			String accessTokenSecret = oauthParameters.getOAuthTokenSecret();
			request.getSession().setAttribute(
					IGoogleOAuthParametersProvider.TOKEN, null);
			request.getSession().setAttribute(IGoogleOAuthParametersProvider.TOKEN_SECRET, null);
			request.getSession()
					.setAttribute(IGoogleOAuthParametersProvider.ACCESS_TOKEN, accessToken);
			request.getSession().setAttribute(IGoogleOAuthParametersProvider.ACCESS_TOKEN_SECRET,
					accessTokenSecret);
		} catch (OAuthException e) {
			log.error("OAuth - persist access token error", e);
		}
	}
}
