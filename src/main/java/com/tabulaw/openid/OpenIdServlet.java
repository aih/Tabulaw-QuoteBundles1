package com.tabulaw.openid;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;

import com.google.inject.Inject;
import com.google.step2.AuthRequestHelper;
import com.google.step2.AuthResponseHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.openid.ui.UiMessageRequest;
import com.google.step2.servlet.InjectableServlet;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;

@SuppressWarnings("serial")
public class OpenIdServlet extends InjectableServlet {

	private final static Log log = LogFactory.getLog(OpenIdServlet.class);

	@Inject
	protected ConsumerHelper consumerHelper;

	private String realm;
	private String returnToPath;
	private String homePath;
	private String consumerKey;
	private String consumerSecret;
	private String redirectOnSuccess;

	/**
	 * Init the servlet. For demo purposes, we're just using an in-memory
	 * version of OpenID4Java's ConsumerAssociationStore. Production apps,
	 * particularly those in a clustered environment, should consider using an
	 * implementation backed by shared storage (memcache, DB, etc.)
	 * 
	 * @param config
	 * @throws ServletException
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		returnToPath = getInitParameter("return_to_path", "/openid");
		homePath = getInitParameter("home_path", "/");
		realm = getInitParameter("realm", null);
		consumerKey = getInitParameter("consumerKey", "http://127.0.0.1:8888");
		consumerSecret = getInitParameter("consumerSecret", "");
		redirectOnSuccess = getInitParameter("redirectOnSuccess", "true");
	}

	/**
	 * Either initiates a login to a given provider or processes a response from
	 * an IDP.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String domain = req.getParameter("hd");
		if (domain != null) {
			// User attempting to login with provided domain, build and OpenID
			// request and redirect
			try {
				AuthRequest authRequest = startAuthentication(domain, req);
				String url = authRequest.getDestinationUrl(true);
				resp.sendRedirect(url);
			} catch (OpenIDException e) {
				throw new ServletException("Error initializing OpenID request",
						e);
			}
		} else {
			// This is a response from the provider, go ahead and validate
			doPost(req, resp);
		}
	}

	/**
	 * Handle the response from the OpenID Provider.
	 * 
	 * @param req
	 *            Current servlet request
	 * @param resp
	 *            Current servlet response
	 * @throws ServletException
	 *             if unable to process request
	 * @throws IOException
	 *             if unable to process request
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			UserInfo openIdUser = completeAuthentication(req);
			User user = doSignInUser(req, resp, openIdUser);
			doReLoginUser(user, req, resp);
			// req.getSession().setAttribute("user", openIdUser);
			if (openIdUser == null) {
				IOUtils.write("Invalid OpenId authorization.",
						resp.getOutputStream());
			} else if (openIdUser.isHasOpenIdOAuth()) {
				IOUtils.write("Success: OpenId+OAuth works.",
						resp.getOutputStream());
			} else {
				IOUtils.write("Failure: OpenId+OAuth is not working.",
						resp.getOutputStream());
			}
			if ("true".equalsIgnoreCase(redirectOnSuccess)) {
				resp.sendRedirect(homePath);
			}
		} catch (OpenIDException e) {
			throw new ServletException("Error processing OpenID response", e);
		}
	}

	private User doSignInUser(HttpServletRequest req, HttpServletResponse resp,
			UserInfo openIdUser) {
		UserSignIn service = new UserSignIn((PersistContext) req
				.getSession(false).getServletContext()
				.getAttribute(PersistContext.KEY));
		return service.doUserSignIn(openIdUser);
	}

	private void doReLoginUser(User user, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		logout(req);
		if (user != null) {
			HttpSession session = req.getSession();
			UserContext context = new UserContext();
			context.setUser(user);
			session.setAttribute(UserContext.KEY, context);
		}
	}

	private void logout(HttpServletRequest req) {
		try {
			HttpSession session = req.getSession(false);
			if (session != null) {
				session.invalidate();
			}
		} catch (IllegalStateException e) {
		}
	}

	/**
	 * Builds an auth request for a given OpenID provider.
	 * 
	 * @param op
	 *            OpenID Provider URL. In the context of Google Apps, this can
	 *            be a naked domain name such as "saasycompany.com". The length
	 *            of the domain can exceed 100 chars.
	 * @param request
	 *            Current servlet request
	 * @return Auth request
	 * @throws org.openid4java.OpenIDException
	 *             if unable to discover the OpenID endpoint
	 */
	private AuthRequest startAuthentication(String op,
			HttpServletRequest request) throws OpenIDException {
		IdpIdentifier openId = new IdpIdentifier(op);

		String realm = realm(request);
		String returnToUrl = returnTo(request);

		AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(openId,
				returnToUrl);
		addAttributes(helper);

		try {
			OAuthAccessor accessor = createOAuthAccessor();
			helper.requestOauthAuthorization(accessor.consumer.consumerKey,
			// "http://docs.google.com/feeds/");
			// "http://www.google.com/docs/feeds/");
					"http://www.google.com/m8/feeds/");
		} catch (Exception e) {
			log.error("", e);
		}

		AuthRequest authReq = helper.generateRequest();

		UiMessageRequest uiExtension = new UiMessageRequest();
		uiExtension.setIconRequest(true);

		authReq.addExtension(uiExtension);
		authReq.setRealm(realm);

		request.getSession().setAttribute("discovered",
				helper.getDiscoveryInformation());

		return authReq;
	}

	private OAuthAccessor createOAuthAccessor() {
		OAuthServiceProvider provider = new OAuthServiceProvider("", "", "");
		OAuthConsumer consumer = new OAuthConsumer(homePath, consumerKey,
				consumerSecret, provider);
		return new OAuthAccessor(consumer);
	}

	/**
	 * Validates the response to an auth request, returning an authenticated
	 * user object if successful.
	 * 
	 * @param request
	 *            Current servlet request
	 * @return User
	 * @throws org.openid4java.OpenIDException
	 *             if unable to verify response
	 */

	UserInfo completeAuthentication(HttpServletRequest request)
			throws OpenIDException {
		HttpSession session = request.getSession();
		ParameterList openidResp = Step2.getParameterList(request);
		String receivingUrl = currentUrl(request);
		DiscoveryInformation discovered = (DiscoveryInformation) session
				.getAttribute("discovered");

		AuthResponseHelper authResponse = consumerHelper.verify(receivingUrl,
				openidResp, discovered);
		if (authResponse.getAuthResultType() == AuthResponseHelper.ResultType.AUTH_SUCCESS) {
			return onSuccess(authResponse, request);
		}
		return onFail(authResponse, request);
	}

	/**
	 * Adds the requested AX attributes to the request
	 * 
	 * @param helper
	 *            Request builder
	 */
	void addAttributes(AuthRequestHelper helper) {
		helper.requestAxAttribute(Step2.AxSchema.EMAIL, true)
				.requestAxAttribute(Step2.AxSchema.FIRST_NAME, true)
				.requestAxAttribute(Step2.AxSchema.LAST_NAME, true);
	}

	/**
	 * Reconstructs the current URL of the request, as sent by the user
	 * 
	 * @param request
	 *            Current servlet request
	 * @return URL as sent by user
	 */
	String currentUrl(HttpServletRequest request) {
		return Step2.getUrlWithQueryString(request);
	}

	/**
	 * Gets the realm to advertise to the IDP. If not specified in the servlet
	 * configuration. it dynamically constructs the realm based on the current
	 * request.
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Realm
	 */
	String realm(HttpServletRequest request) {
		if (StringUtils.isNotBlank(realm)) {
			return realm;
		} else {
			return baseUrl(request);
		}
	}

	/**
	 * Gets the <code>openid.return_to</code> URL to advertise to the IDP.
	 * Dynamically constructs the URL based on the current request.
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Return to URL
	 */
	String returnTo(HttpServletRequest request) {
		return new StringBuffer(baseUrl(request))
				.append(request.getContextPath()).append(returnToPath)
				.toString();
	}

	/**
	 * Dynamically constructs the base URL for the applicaton based on the
	 * current request
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Base URL (path to servlet context)
	 */
	String baseUrl(HttpServletRequest request) {
		StringBuffer url = new StringBuffer(request.getScheme()).append("://")
				.append(request.getServerName());
		if ((request.getScheme().equalsIgnoreCase("http") && request
				.getServerPort() != 80)
				|| (request.getScheme().equalsIgnoreCase("https") && request
						.getServerPort() != 443)) {
			url.append(":").append(request.getServerPort());
		}
		return url.toString();
	}

	/**
	 * Map the OpenID response into a user for our app.
	 * 
	 * @param helper
	 *            Auth response
	 * @param request
	 *            Current servlet request
	 * @return User representation
	 */
	@SuppressWarnings("rawtypes")
	UserInfo onSuccess(AuthResponseHelper helper, HttpServletRequest request) {
		for (Object o : request.getParameterMap().entrySet()) {
			Map.Entry e = (Map.Entry) o;
			log.debug(e.getKey() + " -> " + e.getValue());
		}
		try {
			if (helper.hasHybridOauthExtension()) {
				ParameterList params = helper.getHybridOauthResponse()
						.getParameters();
				for (Object param : params.getParameters()) {
					System.out.println(param + " -> "
							+ params.getParameterValue(param.toString()));
				}
			} else {
				log.warn("No OpenID + OAuth Hybrid !!!");
			}
		} catch (MessageException e) {
			log.error("", e);
		}
		return new UserInfo(helper.getClaimedId().toString(),
				helper.getAxFetchAttributeValue(Step2.AxSchema.EMAIL),
				helper.getAxFetchAttributeValue(Step2.AxSchema.FIRST_NAME),
				helper.getAxFetchAttributeValue(Step2.AxSchema.LAST_NAME),
				helper.hasHybridOauthExtension());
	}

	/**
	 * Handles the case where authentication failed or was canceled. Just a
	 * no-op here.
	 * 
	 * @param helper
	 *            Auth response
	 * @param request
	 *            Current servlet request
	 * @return User representation
	 */
	UserInfo onFail(AuthResponseHelper helper, HttpServletRequest request) {
		return null;
	}

	/**
	 * Small helper for fetching init params with default values
	 * 
	 * @param key
	 *            Parameter to fetch
	 * @param defaultValue
	 *            Default value to use if not set in web.xml
	 * @return
	 */
	protected String getInitParameter(String key, String defaultValue) {
		String value = getInitParameter(key);
		return StringUtils.isBlank(value) ? defaultValue : value;
	}
}
