package com.tabulaw.sso.oauth.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class DocsServlet extends HttpServlet {

	HttpClient client = new HttpClient();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IOUtils.write("<a href=\"/login\">Login</a></br>",
				response.getOutputStream());

		GoogleOAuthParameters oauthParameters = (GoogleOAuthParameters) request
				.getSession().getAttribute("oauth-parameters");

		System.out.println("Callback: " + request.getQueryString());

		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
				new OAuthHmacSha1Signer());
		oauthHelper.getOAuthParametersFromCallback(request.getQueryString(),
				oauthParameters);

		try {
			String accessToken = oauthHelper.getAccessToken(oauthParameters);
			String accessTokenSecret = oauthParameters.getOAuthTokenSecret();
			getDocuments(oauthParameters, oauthHelper, response);
		} catch (OAuthException e) {
			e.printStackTrace();
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
			System.out.println(header);
			System.out.println(get.getStatusCode());
			System.out.println(get.getStatusText());

			IOUtils.write(get.getResponseBodyAsString(),
					response.getOutputStream());
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
