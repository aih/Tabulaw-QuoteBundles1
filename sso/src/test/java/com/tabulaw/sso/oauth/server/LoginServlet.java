package com.tabulaw.sso.oauth.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuthAccessor;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.step2.AuthRequestHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;
import com.google.step2.discovery.IdpIdentifier;

@SuppressWarnings("serial")
@Singleton
public class LoginServlet extends HttpServlet {

	private ConsumerHelper consumerHelper;
	private OAuthProviderInfoStore providerStore;

	@Inject
	public void setConsumerHelper(ConsumerHelper helper) {
		this.consumerHelper = helper;
	}

	@Inject
	public void setProviderInfoStore(OAuthProviderInfoStore store) {
		this.providerStore = store;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/login.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		 System.out.println("LOGIN SERVLET");
		handleDiscovery(request, response);
	}

	private void handleDiscovery(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {

		// posted means they're sending us an OpenID
		StringBuffer realmBuf = new StringBuffer(req.getScheme()).append("://")
				.append(req.getServerName());

		if ((req.getScheme().equalsIgnoreCase("http") && req.getServerPort() != 80)
				|| (req.getScheme().equalsIgnoreCase("https") && req
						.getServerPort() != 443)) {
			realmBuf.append(":").append(req.getServerPort());
		}

		String realm = realmBuf.toString();
		String returnToUrl = new StringBuffer(realm)
				.append(req.getContextPath()).append("/checkauth").toString();

		String openid = req.getParameter("openid");

		if (openid == null || openid.length() == 0) {
			System.out.println("EMPTY OPENID");
		}

		// if the user typed am email address, ignore the user part
		openid = openid.replaceFirst(".*@", "");

		// we assume that the user typed an identifier for an IdP, not for a
		// user
		IdpIdentifier openId = new IdpIdentifier(openid);

		AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(openId,
				returnToUrl.toString());

		helper.requestUxIcon(true);

		// if (YES_STRING.equals(req.getParameter("oauth"))) {
		if (true) {
			try {
				OAuthAccessor accessor = providerStore
						.getOAuthAccessor("google");
				helper.requestOauthAuthorization(accessor.consumer.consumerKey,
						"http://www.google.com/m8/feeds/");
			} catch (ProviderInfoNotFoundException e) {
				log("could not find provider info for Google", e);
				// we'll just ignore the OAuth request and proceed without it.
			}
		}

		HttpSession session = req.getSession();
		AuthRequest authReq = null;
		try {
			authReq = helper.generateRequest();
			authReq.setRealm(realm);
			session.setAttribute("discovered", helper.getDiscoveryInformation());
		} catch (DiscoveryException e) {
			System.out.println("PROBLEM");
			e.printStackTrace();
			return;
		} catch (MessageException e) {
			throw new ServletException(e);
		} catch (ConsumerException e) {
			throw new ServletException(e);
		}

	}
}
