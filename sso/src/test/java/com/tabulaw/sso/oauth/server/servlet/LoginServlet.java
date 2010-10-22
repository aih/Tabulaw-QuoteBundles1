package com.tabulaw.sso.oauth.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class LoginServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String CONSUMER_KEY = "anonymous";
		String CONSUMER_SECRET = "anonymous";

		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
		oauthParameters.setScope("https://docs.google.com/feeds/");
		oauthParameters.setOAuthCallback("http://127.0.0.1:8888/docs");
		request.getSession().setAttribute("oauth-parameters", oauthParameters);

		String accessToken = (String) request.getSession().getAttribute(
				"access-token");
		String accessTokenSecret = (String) request.getSession().getAttribute(
				"access-token-secret");

		if (accessToken == null || accessTokenSecret == null
				|| "true".equals(request.getParameter("relogin"))) {
			authorize(oauthParameters, request, response);
		} else {
			forward(response);
		}
	}

	private void authorize(GoogleOAuthParameters oauthParameters,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			request.getSession().setAttribute("access-token", null);
			request.getSession().setAttribute("access-token-secret", null);
			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
					new OAuthHmacSha1Signer());
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);
			String approvalPageUrl = oauthHelper
					.createUserAuthorizationUrl(oauthParameters);
			System.out.println(approvalPageUrl);
			response.sendRedirect(approvalPageUrl);
		} catch (OAuthException e) {
			e.printStackTrace();
		}
	}

	private void forward(HttpServletResponse response) throws IOException,
			ServletException {
		response.sendRedirect("/docs");
	}
}
