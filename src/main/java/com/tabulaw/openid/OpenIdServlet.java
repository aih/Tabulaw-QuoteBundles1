package com.tabulaw.openid;

import com.tabulaw.service.LoginService;
import java.io.IOException;
import java.net.URL;
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
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthParameters.OAuthType;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.inject.Inject;
import com.google.step2.AuthRequestHelper;
import com.google.step2.AuthResponseHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.openid.ui.UiMessageRequest;
import com.google.step2.servlet.InjectableServlet;
import com.tabulaw.model.User;
import com.tabulaw.oauth.OAuthParameters;


@SuppressWarnings("serial")
public class OpenIdServlet extends InjectableServlet {
	private static final String REDIRECT_URL = "/OpenIdRegister.html"; 

	private final static Log log = LogFactory.getLog(OpenIdServlet.class);

	@Inject
	protected ConsumerHelper consumerHelper;

	private String realm;
	private String returnToPath;
	private String homePath;
	private String consumerKey;
	private String consumerSecret;
	private String redirectOnSuccess;
	private String oauthHybrid;

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
		returnToPath = getInitParameter("return_to_path", "openid");
		homePath = getInitParameter("home_path", "/");
		realm = getInitParameter("realm", null);
		consumerKey = getInitParameter("consumerKey", "http://127.0.0.1:8888");
		consumerSecret = getInitParameter("consumerSecret", "");
		redirectOnSuccess = getInitParameter("redirectOnSuccess", "true");
		oauthHybrid = getInitParameter("oauthHybrid", null);
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
				log.debug("returnToPathKey=" + returnToPath);
				log.debug("consumerKey=" + consumerKey);
				log.debug("consumerSecret=" + consumerSecret);
				log.debug("realm=" + realm(req));
				log.debug("oauthHybrid=" + realm(req));
				resp.sendRedirect(url);
			} catch (OpenIDException e) {
				log.error("", e);
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
                        LoginService.checkRegisterationAndPutToSession(req.getSession(), openIdUser.getEmail());
			if ("true".equalsIgnoreCase(redirectOnSuccess)) {
                                StringBuilder registerUrl = new StringBuilder();
                                registerUrl
                                            .append(req.getContextPath())
                                            .append(REDIRECT_URL)
                                            .append("?userEmail=")
                                            .append(openIdUser.getEmail());
                                
				resp.sendRedirect(addGWTHosted(req, registerUrl.toString()));
			}
		} catch (OpenIDException e) {
			log.error("", e);
			throw new ServletException("Error processing OpenID response", e);
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

		if ("true".equalsIgnoreCase(oauthHybrid)) {
			try {
				OAuthAccessor accessor = createOAuthAccessor();
				helper.requestOauthAuthorization(accessor.consumer.consumerKey,
						"http://docs.google.com/feeds/");
			} catch (Exception e) {
				log.error("", e);
			}
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

	private UserInfo completeAuthentication(HttpServletRequest request)
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
	private void addAttributes(AuthRequestHelper helper) {
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
	private String currentUrl(HttpServletRequest request) {
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
	private String realm(HttpServletRequest request) {
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
	private String returnTo(HttpServletRequest request) {
		String returnTo = new StringBuffer(baseUrl(request))
				.append(request.getContextPath()).append("/")
				.append(returnToPath).toString();
		return addGWTHosted(request, returnTo);
	}

	/**
	 * Dynamically constructs the base URL for the applicaton based on the
	 * current request
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Base URL (path to servlet context)
	 */
	private String baseUrl(HttpServletRequest request) {
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

	private String addGWTHosted(HttpServletRequest request, String url) {
		String gwt_codesvr = request.getParameter("gwt.codesvr");
		if (gwt_codesvr != null) {
			if (url.contains("?")) {
				url += "&";
			} else {
				url += "?";
			}
			url += "gwt.codesvr=" + gwt_codesvr;
		}
		return url;
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
	private UserInfo onSuccess(AuthResponseHelper helper,
			HttpServletRequest request) {
		log.debug("OpenId success");
		for (Object o : request.getParameterMap().entrySet()) {
			Map.Entry e = (Map.Entry) o;
			log.debug(e.getKey() + " -> " + e.getValue());
		}
		if (helper.hasHybridOauthExtension()) {
			hybridOpenIdOAuth(helper, request);
		} else {
			log.warn("No OpenID + OAuth Hybrid !!!");
		}
		String claimedId = helper.getClaimedId().toString();
		log.debug("OpenId ClaimedId: " + claimedId);
		return new UserInfo(claimedId,
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
	private UserInfo onFail(AuthResponseHelper helper,
			HttpServletRequest request) {
		log.debug("OpenId Failure");
		return null;
	}

	private void hybridOpenIdOAuth(AuthResponseHelper helper,
			HttpServletRequest request) {
		try {
			log.info("Processing OpenID + OAuth Hybrid ...");
			ParameterList params = helper.getHybridOauthResponse()
					.getParameters();
			for (Object o : params.getParameters()) {
				Parameter param = (Parameter) o;
				log.debug("OpenID + OAuth Hybrid param: " + param.getKey()
						+ " -> " + param.getValue());
			}
			String requestToken = params.getParameter("request_token")
					.getValue();
			String scope = params.getParameter("scope").getValue();
			getDocuments(scope);
			changeOpenIdOAuthHybridRequestTokenToAccessToken(request,
					requestToken, scope);
		} catch (MessageException e) {
			log.error("", e);
		}
	}

	private void getDocuments(String scope) {
		// it doesn't work for this user (grhh):
		String user = "radek@olesiak.biz";
		// it works for this user and when realm=http://dev.imdzone.biz :
		// String user = "radek@dev.imdzone.biz";
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(consumerKey);
		oauthParameters.setOAuthConsumerSecret(consumerSecret);
		oauthParameters.setScope(scope);
		oauthParameters.setOAuthType(OAuthType.TWO_LEGGED_OAUTH);
		try {
			OAuthSigner signer = new OAuthHmacSha1Signer();
			DocsService client = new DocsService("tabulaw-webapp-2");
			client.setOAuthCredentials(oauthParameters, signer);
			String url = "http://docs.google.com/feeds/default/private/full";
			log.debug("2-legged OAuth; scope: " + scope);
			log.debug("2-legged OAuth; consumerKey: " + consumerKey);
			log.debug("2-legged OAuth; consumerSecret: " + consumerSecret);
			log.debug("2-legged OAuth; url: " + url);
			log.debug("2-legged OAuth; user: " + user);
			URL feedUrl = new URL(url);
			DocumentQuery query = new DocumentQuery(feedUrl);
			query.setStringCustomParameter("xoauth_requestor_id", user);
			log.debug("2-legged OAuth; feed url: " + query.getFeedUrl());
			DocumentListFeed resultFeed = client.getFeed(query,
					DocumentListFeed.class);
			log.debug("2-legged OAuth; #docs was read: "
					+ +resultFeed.getEntries().size());
			for (DocumentListEntry entry : resultFeed.getEntries()) {
				log.debug("2-legged OAuth; document title: "
						+ entry.getTitle().getPlainText());
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	private void changeOpenIdOAuthHybridRequestTokenToAccessToken(
			HttpServletRequest request, String requestToken, String scope) {
		try {
			OAuthParameters oauthParameters = new OAuthParameters();
			OAuthSigner signer = new OAuthHmacSha1Signer();
			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(signer);
			oauthParameters.setOAuthConsumerKey(consumerKey);
			oauthParameters.setOAuthConsumerSecret(consumerSecret);
			oauthParameters.setRealm(realm);
			oauthParameters.setOAuthToken(requestToken);
			oauthParameters.setOAuthTokenSecret("");
			oauthParameters.setScope(scope);
			log.debug("OpenId+OAuth hybrid; authorizationUrl: "
					+ oauthHelper.createUserAuthorizationUrl(oauthParameters));
			log.debug("OpenId+OAuth hybrid; request queryString: "
					+ request.getQueryString());
			log.debug("OpenId+OAuth hybrid; oauth_verifier: "
					+ request.getParameter("oauth_verifier"));
			log.debug("OpenId+OAuth hybrid; baseParameters: "
					+ oauthParameters.getBaseParameters());
			log.debug("OpenId+OAuth hybrid; extraParameters: "
					+ oauthParameters.getExtraParameters());
			log.debug("OpenId+OAuth hybrid; accessTokenUrl: "
					+ oauthHelper.getAccessTokenUrl());
			log.debug("OpenId+OAuth hybrid; accessToken: "
					+ oauthHelper.getAccessToken(oauthParameters));
			log.debug("OpenId+OAuth hybrid; OAuthTokenSecret: "
					+ oauthParameters.getOAuthTokenSecret());
		} catch (Exception e) {
			log.error("", e);
		}
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
	private String getInitParameter(String key, String defaultValue) {
		String value = getInitParameter(key);
		return StringUtils.isBlank(value) ? defaultValue : value;
	}
}
