package com.tabulaw.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

@SuppressWarnings("serial")
public class OAuthAuthorize extends HttpServlet {

	private final static Log log = LogFactory.getLog(OAuthAuthorize.class);

	private final static String REDIRECT_URL = "/oauthpersistaccesstoken";

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String accessToken = (String) request.getSession().getAttribute(
				"access-token");
		String accessTokenSecret = (String) request.getSession().getAttribute(
				"access-token-secret");

		if (accessToken == null || accessTokenSecret == null
				|| "true".equals(request.getParameter("relogin"))) {
			authorize(request, response);
		} else {
			forward(response);
		}
	}

	private void authorize(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			request.getSession().setAttribute("access-token", null);
			request.getSession().setAttribute("access-token-secret", null);

			String CONSUMER_KEY = "anonymous";
			String CONSUMER_SECRET = "anonymous";

			String host = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort();

			GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
			oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
			oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
			oauthParameters.setScope("https://docs.google.com/feeds/");
			oauthParameters.setOAuthCallback(host + REDIRECT_URL);
			request.getSession().setAttribute("oauth-parameters",
					oauthParameters);
			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
					new OAuthHmacSha1Signer());
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);
			String approvalPageUrl = oauthHelper
					.createUserAuthorizationUrl(oauthParameters);
			log.debug(approvalPageUrl);
			response.sendRedirect(approvalPageUrl);
		} catch (OAuthException e) {
			log.error("OAuth authorization error", e);
		}
	}

	private void forward(HttpServletResponse response) throws IOException,
			ServletException {
		response.sendRedirect(REDIRECT_URL);
	}
}
