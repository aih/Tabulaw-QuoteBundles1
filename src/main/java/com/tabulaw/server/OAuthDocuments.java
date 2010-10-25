package com.tabulaw.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

@SuppressWarnings("serial")
public class OAuthDocuments extends HttpServlet {

	private final static Log log = LogFactory.getLog(OAuthDocuments.class);
	
	private final static HttpClient client = new HttpClient();

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
		getDocuments(oauthParameters, oauthHelper, response);
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

	private void getDocuments(GoogleOAuthParameters oauthParameters,
			GoogleOAuthHelper oauthHelper, HttpServletResponse response)
			throws HttpException, IOException {
		try {
			String url = "https://docs.google.com/feeds/default/private/full/-/document";
			GetMethod get = new GetMethod(url);
			String header = oauthHelper.getAuthorizationHeader(url, "GET",
					oauthParameters);
			get.setRequestHeader("Authorization", header);
			get.addRequestHeader("GData-Version", "3.0");

			client.executeMethod(get);
			log.debug(header);
			log.debug(get.getStatusCode());
			log.debug(get.getStatusText());

			IOUtils.write(get.getResponseBodyAsString(),
					response.getOutputStream());
		} catch (OAuthException e) {
			log.error("OAuth Google Docs get doocuments error", e);

		}
	}
}
