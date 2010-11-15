package com.tabulaw.oauth;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.tabulaw.model.User;
import com.tabulaw.server.UserContext;

@SuppressWarnings("serial")
public class OAuthPersistAccessTokenServlet extends HttpServlet {

	private final static Log log = LogFactory
			.getLog(OAuthPersistAccessTokenServlet.class);

	private OAuthParametersProvider authParametersProvider = new GoogleAnonymousOAuthParametersProvider();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IOUtils.write(
				"<script> window.opener.eval('onPopupWindowClose();');</script>",
				response.getOutputStream());
		IOUtils.write("<script> self.close(); </script>",
				response.getOutputStream());

		if (request.getSession().getAttribute(
				OAuthParametersProvider.OAUTH_PARAMETERS) != null) {
			getAndPersistAccessToken(request);
		}
	}

	private void getAndPersistAccessToken(HttpServletRequest request) {
		try {
			authParametersProvider.setHttpServletRequest(request);
			OAuthParameters oauthParameters = new OAuthParameters(
					authParametersProvider.getGoogleDocumentsOAuthParameters());

			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
					new OAuthHmacSha1Signer());

			oauthHelper.getOAuthParametersFromCallback(
					request.getQueryString(), oauthParameters);
			oauthHelper.getAccessToken(oauthParameters);
			oauthParameters.getOAuthTokenSecret();
			request.getSession().setAttribute(
					OAuthParametersProvider.OAUTH_PARAMETERS, null);
			if (OAuthParametersProvider.DATABASE_BASED_ACCESS_TOKEN) {
				UserContext uc = (UserContext) request.getSession(false)
						.getAttribute(UserContext.KEY);
				User user = uc.getUser();
				user.setOAuthParameters(new HashMap<String,String>(oauthParameters.getBaseParameters()));
				user.setOAuthParametersExtra(new HashMap<String,String>(oauthParameters
						.getExtraParameters()));
			} else {
				request.getSession().setAttribute(
						OAuthParametersProvider.OAUTH_ACCESS_PARAMETERS,
						oauthParameters);
			}
		} catch (OAuthException e) {
			log.error("OAuth - persist access token error", e);
		}
	}
}
