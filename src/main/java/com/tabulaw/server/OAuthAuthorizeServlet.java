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
import com.tabulaw.server.bean.AnonymousGoogleOAuthParametersProvider;
import com.tabulaw.server.bean.IGoogleOAuthParametersProvider;

@SuppressWarnings("serial")
public class OAuthAuthorizeServlet extends HttpServlet {

	private final static Log log = LogFactory
			.getLog(OAuthAuthorizeServlet.class);

	public final static String REDIRECT_URL = "/poc/oauthpersistaccesstoken";

	private IGoogleOAuthParametersProvider authParametersProvider = new AnonymousGoogleOAuthParametersProvider();

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String accessToken = (String) request.getSession().getAttribute(
				IGoogleOAuthParametersProvider.ACCESS_TOKEN);
		String accessTokenSecret = (String) request.getSession().getAttribute(
				IGoogleOAuthParametersProvider.ACCESS_TOKEN_SECRET);

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
			resetSession(request);

			authParametersProvider.setHttpServletRequest(request);
			GoogleOAuthParameters oauthParameters = authParametersProvider
					.getGoogleDocumentsOAuthParameters();

			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
					new OAuthHmacSha1Signer());
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);
			String approvalPageUrl = oauthHelper
					.createUserAuthorizationUrl(oauthParameters);

			persistToken(request, oauthParameters);

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

	private void persistToken(HttpServletRequest request,
			GoogleOAuthParameters oauthParameters) {
		request.getSession().setAttribute(IGoogleOAuthParametersProvider.TOKEN,
				oauthParameters.getOAuthToken());
		request.getSession().setAttribute(
				IGoogleOAuthParametersProvider.TOKEN_SECRET,
				oauthParameters.getOAuthTokenSecret());
	}

	private void resetSession(HttpServletRequest request) {
		request.getSession().setAttribute(IGoogleOAuthParametersProvider.TOKEN,
				null);
		request.getSession().setAttribute(
				IGoogleOAuthParametersProvider.TOKEN_SECRET, null);
		request.getSession().setAttribute(
				IGoogleOAuthParametersProvider.ACCESS_TOKEN, null);
		request.getSession().setAttribute(
				IGoogleOAuthParametersProvider.ACCESS_TOKEN_SECRET, null);
	}
}
