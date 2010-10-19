package com.tabulaw.sso.step2.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuthAccessor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class DocsServlet extends HttpServlet {

	private final HttpClient client = new HttpClient();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IOUtils.write("<a href=\"/\">Home</a><br />",
				response.getOutputStream());
		IOUtils.write("Google Docs", response.getOutputStream());
		getOAuthData(request);
		IOUtils.write("<br />" + IOUtils.toString(request.getInputStream()),
				response.getOutputStream());
		Enumeration<?> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			IOUtils.write("<br />" + headers.nextElement(),
					response.getOutputStream());
		}
		Enumeration<?> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			IOUtils.write("<br />" + params.nextElement(),
					response.getOutputStream());
		}
		HttpSession session = request.getSession();
		GetMethod get = createGetMethod(
				"/feeds/default/private/full/-/document", "");
		try {
			client.executeMethod(get);
			IOUtils.write("<br />STATUS CODE: " + get.getStatusCode()
					+ "<br />", response.getOutputStream());
			IOUtils.write(get.getResponseBodyAsString(),
					response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			IOUtils.write("<br />ERROR", response.getOutputStream());
		}
	}

	private GetMethod createGetMethod(String path, String authKey) {
		GetMethod get = new GetMethod("https://docs.google.com" + path);
		get.addRequestHeader("Authorization", authKey);
		get.addRequestHeader("GData-Version", "3.0");
		return get;
	}

	private void getOAuthData(HttpServletRequest request)
			throws ServletException {

		HttpSession session = request.getSession();

		try {
			OAuthAccessor accessor = (OAuthAccessor) session
					.getAttribute("accessor");

			if (accessor == null) {
				return;
			}

			URL feedUrl = new URL(
					"http://www.google.com/m8/feeds/contacts/default/thin");
			GoogleService service = new ContactsService("step2");

			GoogleOAuthParameters params = new GoogleOAuthParameters();
			params.setOAuthConsumerKey(accessor.consumer.consumerKey);
			params.setOAuthConsumerSecret(accessor.consumer.consumerSecret);
			params.setOAuthToken(accessor.accessToken);
			params.setOAuthTokenSecret(accessor.tokenSecret);

			OAuthSigner signer = new OAuthHmacSha1Signer();

			service.setOAuthCredentials(params, signer);

			ContactFeed resultFeed;
			try {
				resultFeed = service.getFeed(feedUrl, ContactFeed.class);
			} catch (NullPointerException e) {
				request.setAttribute("contacts", null);
				return;
			}

			request.setAttribute("contacts", resultFeed);

		} catch (MalformedURLException e) {
			throw new ServletException(e);
		} catch (OAuthException e) {
			throw new ServletException(e);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (ServiceException e) {
			throw new ServletException(e);
		}
	}
}
