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

		if (request.getSession().getAttribute(
				IGoogleOAuthParametersProvider.OAUTH_PARAMETERS) != null) {
			getAndPersistAccessToken(request);
		}
	}

	private void getAndPersistAccessToken(HttpServletRequest request) {
		try {
			authParametersProvider.setHttpServletRequest(request);
			GoogleOAuthParameters oauthParameters = new OAuthParameters(
					authParametersProvider.getGoogleDocumentsOAuthParameters());

			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
					new OAuthHmacSha1Signer());

			oauthHelper.getOAuthParametersFromCallback(
					request.getQueryString(), oauthParameters);
			oauthHelper.getAccessToken(oauthParameters);
			oauthParameters.getOAuthTokenSecret();
			request.getSession().setAttribute(
					IGoogleOAuthParametersProvider.OAUTH_PARAMETERS, null);
			request.getSession().setAttribute(
					IGoogleOAuthParametersProvider.OAUTH_ACCESS_PARAMETERS,
					oauthParameters);
		} catch (OAuthException e) {
			log.error("OAuth - persist access token error", e);
		}
	}
}
