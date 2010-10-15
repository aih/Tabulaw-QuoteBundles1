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
import com.google.step2.AuthResponseHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.Step2OAuthClient;
import com.google.step2.VerificationException;
import com.google.step2.AuthResponseHelper.ResultType;
import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;
import com.google.step2.servlet.InjectableServlet;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

import org.openid4java.association.AssociationException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Example Servlet to handle and check the response from IDP authentication
 *
 * @author Dirk Balfanz (dirk.balfanz@gmail.com)
 * @author Breno de Medeiros (breno.demedeiros@gmail.com)
 */
public class CheckAuthServlet extends InjectableServlet {
  private ConsumerHelper helper;
  private OAuthProviderInfoStore providerStore;
  private Step2OAuthClient oauthClient;
  private static final String NO_TOKEN = "None";
  private static final String UNKNOWN = "Unknown";
  private static final String TEMPLATE_FILE = "/WEB-INF/checkauth.jsp";

  private static final List<Step2.AxSchema> SUPPORTED_AX_SCHEMAS =
    Arrays.asList(Step2.AxSchema.values());

  @Inject
  public void setConsumerHelper(ConsumerHelper helper) {
    this.helper = helper;
  }

  @Inject
  public void setProviderInfoStore(OAuthProviderInfoStore store) {
    this.providerStore = store;
  }

  @Inject
  void setOAuthHttpClient(Step2OAuthClient client) {
    this.oauthClient = client;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    HttpSession session = req.getSession();
    ParameterList openidResp = Step2.getParameterList(req);
    String receivingUrl = Step2.getUrlWithQueryString(req);
    DiscoveryInformation discovered =
      (DiscoveryInformation) session.getAttribute("discovered");

    String requestToken = NO_TOKEN;

    // Try to get the OpenId, AX, and OAuth values from the auth response
    try {
      AuthResponseHelper authResponse =
        helper.verify(receivingUrl, openidResp, discovered);

      // Clean up stale session state if any
      for (Step2.AxSchema schema : SUPPORTED_AX_SCHEMAS) {
        session.removeAttribute(schema.getShortName());
      }
      session.removeAttribute("request_token");
      session.removeAttribute("access_token");
      session.removeAttribute("access_token_secret");
      session.removeAttribute("accessor");
      session.removeAttribute("user");

      // Get Claimed Identifier
      Identifier claimedId = authResponse.getClaimedId();
      session.setAttribute("user",
          (claimedId == null) ? UNKNOWN : claimedId.getIdentifier());


      if (authResponse.getAuthResultType() == ResultType.SETUP_NEEDED) {
        throw new ServletException("setup needed");
      }

      if (authResponse.getAuthResultType() == ResultType.AUTH_FAILURE) {
        throw new ServletException("auth failure");
      }

      if (authResponse.getAuthResultType() == ResultType.AUTH_SUCCESS) {
        Class<? extends AxMessage> axExtensionType =
            authResponse.getAxExtensionType();
        if (axExtensionType != null) {
          if (axExtensionType.equals(FetchResponse.class)) {
            FetchResponse fetchResponse = authResponse.getAxFetchResponse();
            List<String> aliases = fetchResponse.getAttributeAliases();
            for (String alias : aliases) {
              String typeUri = fetchResponse.getAttributeTypeUri(alias);
              String value = fetchResponse.getAttributeValueByTypeUri(typeUri);

              // check if it's a known type
              Step2.AxSchema schema = Step2.AxSchema.ofTypeUri(typeUri);
              if (null != schema) {
                session.setAttribute(schema.getShortName(), value);
              } else {
                session.setAttribute(alias + " (" + typeUri + ")", value);
              }
            }
          }
        }
        if (authResponse.hasHybridOauthExtension()) {
          requestToken = authResponse.getHybridOauthResponse().getRequestToken();
          session.setAttribute("request_token", "yes (" + requestToken + ")");
        }
      }
    } catch (MessageException e) {
      throw new ServletException(e);
    } catch (DiscoveryException e) {
      throw new ServletException(e);
    } catch (AssociationException e) {
      throw new ServletException(e);
    } catch (VerificationException e) {
      throw new ServletException(e);
    }

    String accessToken = NO_TOKEN;
    String accessTokenSecret = NO_TOKEN;
    if (!NO_TOKEN.equals(requestToken)) {
      // Try getting an acess token from this request token.
      try {
        OAuthAccessor accessor = providerStore.getOAuthAccessor("google");

        OAuthMessage response = oauthClient.invoke(accessor,
            accessor.consumer.serviceProvider.accessTokenURL,
            OAuth.newList(OAuth.OAUTH_TOKEN, requestToken));

        if (response != null) {
          accessToken = response.getParameter(OAuth.OAUTH_TOKEN);
          accessTokenSecret = response.getParameter(OAuth.OAUTH_TOKEN_SECRET);

          session.setAttribute("access_token", "yes (" + accessToken + ")");
          session.setAttribute("access_token_secret",
              "yes (" + accessTokenSecret + ")");

          // store the whole OAuth accessor in the session
          accessor.accessToken = accessToken;
          accessor.tokenSecret = accessTokenSecret;
          session.setAttribute("accessor", accessor);
        }
      } catch (ProviderInfoNotFoundException e) {
        e.printStackTrace();
      } catch (OAuthException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }

    String type = req.getParameter(LOGIN_TYPE.getParameterName());
    if (LOGIN_TYPE.POPUP.getType().equals(type)) {
      RequestDispatcher d = req.getRequestDispatcher(TEMPLATE_FILE);
      d.forward(req, resp);
    } else {
      resp.sendRedirect(req.getRequestURI()
          .replaceAll("/checkauth$", "/hello"));
    }
  }

  private static enum LOGIN_TYPE {
    POPUP("popup"),
    UNKNOWN("");

    private final String type;

    private LOGIN_TYPE(String type) {
      this.type = type;
    }

    public String getType() {
      return type;
    }

    public static final String getParameterName() {
      return "login_type";
    }
  }
}
