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
import com.google.step2.servlet.InjectableServlet;

import net.oauth.OAuthAccessor;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LsoLoginServlet extends InjectableServlet {

  private static final String TEMPLATE_FILE = "/WEB-INF/lso.jsp";
  private static final String REDIRECT_PATH = "/checkauth";
  private static final String YES_STRING = "yes";

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
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    RequestDispatcher d = req.getRequestDispatcher(TEMPLATE_FILE);
    d.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    if ("discovery".equals(req.getParameter("stage"))) {
      handleDiscovery(req, resp);
    } else {
      handlePasswordLogin(req, resp);
    }
  }

  private void handleDiscovery(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

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

    String openid = req.getParameter("openid");

    // if the user typed am email address, ignore the user part
    openid = openid.replaceFirst(".*@", "");

    // we assume that the user typed an identifier for an IdP, not for a user
    IdpIdentifier openId = new IdpIdentifier(openid);

    AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(
        openId, returnToUrl.toString());

    helper.requestUxIcon(true);

    if (YES_STRING.equals(req.getParameter("oauth"))) {
      try {
        OAuthAccessor accessor = providerStore.getOAuthAccessor("google");
        helper.requestOauthAuthorization(accessor.consumer.consumerKey,
            "http://www.google.com/m8/feeds/");
      } catch (ProviderInfoNotFoundException e) {
        log("could not find provider info for Google", e);
        // we'll just ignore the OAuth request and proceed without it.
      }
    }

    if (YES_STRING.equals(req.getParameter("email"))) {
      helper.requestAxAttribute(Step2.AxSchema.EMAIL, true);
    }

    if (YES_STRING.equals(req.getParameter("country"))) {
      helper.requestAxAttribute(Step2.AxSchema.COUNTRY, true);
    }

    if (YES_STRING.equals(req.getParameter("language"))) {
      helper.requestAxAttribute(Step2.AxSchema.LANGUAGE, true);
    }

    if (YES_STRING.equals(req.getParameter("firstName"))) {
      helper.requestAxAttribute(Step2.AxSchema.FIRST_NAME, true);
    }

    if (YES_STRING.equals(req.getParameter("lastName"))) {
      helper.requestAxAttribute(Step2.AxSchema.LAST_NAME, true);
    }

    HttpSession session = req.getSession();
    AuthRequest authReq = null;
    try {
      authReq = helper.generateRequest();
      authReq.setRealm(realm);
      session.setAttribute("discovered", helper.getDiscoveryInformation());
    } catch (DiscoveryException e) {
      throw new ServletException(e);
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

  private void handlePasswordLogin(HttpServletRequest req,
      HttpServletResponse resp) throws IOException {
    resp.getWriter().printf("<h2>you logged in with username %s and password %s</h2>",
        req.getParameter("openid"), req.getParameter("password"));
    resp.setStatus(200);
  }
}
