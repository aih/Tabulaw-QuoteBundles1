/**
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.step2.example.consumer.servlet;

import com.google.inject.Inject;
import com.google.step2.AuthRequestHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.example.consumer.OAuthConsumerUtil;
import com.google.step2.servlet.InjectableServlet;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.pape.PapeRequest;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Example Servlet that redirects to an IDP login page
 *
 * @author Dirk Balfanz (dirk.balfanz@gmail.com)
 * @author Breno de Medeiros (breno.demedeiros@gmail.com)
 */
public class LoginServlet extends InjectableServlet {
  private Log log = LogFactory.getLog(LoginServlet.class);
  private static final String TEMPLATE_FILE = "/WEB-INF/login.jsp";
  private static final String REDIRECT_PATH = "/checkauth?foo=bar";

  private ConsumerHelper consumerHelper;
  private OAuthProviderInfoStore providerStore;
  private OAuthConsumerUtil oauthConsumerUtil;
  private static final String YES_STRING = "yes";

  @Inject
  public void setConsumerHelper(ConsumerHelper helper) {
    this.consumerHelper = helper;
  }

  @Inject
  public void setProviderInfoStore(OAuthProviderInfoStore providerStore) {
    this.providerStore = providerStore;
  }

  @Inject
  public void setOAuthConsumerUtil(OAuthConsumerUtil util) {
    oauthConsumerUtil = util;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    RequestDispatcher d = req.getRequestDispatcher(TEMPLATE_FILE);
    d.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    log.info("Login Servlet Post");

    // posted means they're sending us an OpenID4
    StringBuffer realmBuf = new StringBuffer(req.getScheme())
        .append("://").append(req.getServerName());

    if ((req.getScheme().equalsIgnoreCase("http")
         && req.getServerPort() != 80)
        || (req.getScheme().equalsIgnoreCase("https")
            && req.getServerPort() != 443)) {
      realmBuf.append(":").append(req.getServerPort());
    }

    String realm = realmBuf.toString();
    String returnToUrl = new StringBuffer(realm)
        .append(req.getContextPath()).append(REDIRECT_PATH).toString();

    // this is magic - normally this would also fall out of the discovery:
    OAuthAccessor accessor = null;

    // Fetch an unauthorized OAuth request token to test authorizing
    if (YES_STRING.equals(req.getParameter("oauth"))) {
      try {
        accessor = providerStore.getOAuthAccessor("google");
        accessor = oauthConsumerUtil.getRequestToken(accessor);

        // TODO(sweis): Put this string contstant somewhere that makes sense
        String oauthTestEndpoint =
          (String) accessor.getProperty("oauthTestEndpoint");
        if (oauthTestEndpoint != null) {
          realm = oauthTestEndpoint;
          returnToUrl = oauthTestEndpoint;
        }
      } catch (ProviderInfoNotFoundException e) {
        throw new ServletException(e);
      } catch (OAuthException e) {
        throw new ServletException(e);
      } catch (URISyntaxException e) {
        throw new ServletException(e);
      }
    }

    // we assume that the user typed an identifier for an IdP, not for a user
    IdpIdentifier openId = new IdpIdentifier(req.getParameter("openid"));

    AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(
        openId, returnToUrl.toString());

    helper.requestUxIcon(true);

    if (accessor != null) {
      log.debug("Requesting OAuth scope : " +
          (String) accessor.getProperty("scope"));
      helper.requestOauthAuthorization(accessor.consumer.consumerKey,
          (String) accessor.getProperty("scope"));
    }

    if (YES_STRING.equals(req.getParameter("email"))) {
      log.debug("Requesting AX email");
      helper.requestAxAttribute(Step2.AxSchema.EMAIL, true);
    }

    if (YES_STRING.equals(req.getParameter("country"))) {
      log.debug("Requesting AX country");
      helper.requestAxAttribute(Step2.AxSchema.COUNTRY, true);
    }

    if (YES_STRING.equals(req.getParameter("language"))) {
      log.debug("Requesting AX country");
      helper.requestAxAttribute(Step2.AxSchema.LANGUAGE, true);
    }

    if (YES_STRING.equals(req.getParameter("firstName"))) {
      log.debug("Requesting AX country");
      helper.requestAxAttribute(Step2.AxSchema.FIRST_NAME, true);
    }

    if (YES_STRING.equals(req.getParameter("lastName"))) {
      log.debug("Requesting AX country");
      helper.requestAxAttribute(Step2.AxSchema.LAST_NAME, true);
    }

    HttpSession session = req.getSession();
    AuthRequest authReq = null;
    try {
      authReq = helper.generateRequest();
      authReq.setRealm(realm);

      // add PAPE, if requested
      if (YES_STRING.equals(req.getParameter("reauth"))) {
        log.debug("Requesting PAPE reauth");
        PapeRequest pape = PapeRequest.createPapeRequest();
        pape.setMaxAuthAge(1);
        authReq.addExtension(pape);
      }

      session.setAttribute("discovered", helper.getDiscoveryInformation());
    } catch (DiscoveryException e) {
      StringBuffer errorMessage =
        new StringBuffer("Could not discover OpenID endpoint.");
      errorMessage.append("\n\n").append("Check if URL is valid: ");
      errorMessage.append(openId).append("\n\n");
      errorMessage.append("Stack Trace:\n");
      for (StackTraceElement s : e.getStackTrace()) {
        errorMessage.append(s.toString()).append('\n');
      }
      resp.sendError(400, errorMessage.toString());
      return;
    } catch (MessageException e) {
      throw new ServletException(e);
    } catch (ConsumerException e) {
      throw new ServletException(e);
    }
    if (YES_STRING.equals(req.getParameter("usePost"))) {
      // using POST
      req.setAttribute("message", authReq);
      RequestDispatcher d =
        req.getRequestDispatcher("/WEB-INF/formredirection.jsp");
      d.forward(req, resp);
    } else {
      // using GET
      resp.sendRedirect(authReq.getDestinationUrl(true));
    }
  }
}
