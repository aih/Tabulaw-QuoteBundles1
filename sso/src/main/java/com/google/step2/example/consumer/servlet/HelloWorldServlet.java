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

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;
import com.google.step2.Step2;
import com.google.step2.servlet.InjectableServlet;

import net.oauth.OAuthAccessor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Example Servlet to display results from OpenID Authentication
 *
 * @author Dirk Balfanz (dirk.balfanz@gmail.com)
 * @author Breno de Medeiros (breno.demedeiros@gmail.com)
 */
public class HelloWorldServlet extends InjectableServlet {

  private static String templateFile = "/WEB-INF/hello.jsp";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession();

    // if user clicked "logout", force them to the login page by setting
    // user attribute to null
    if (req.getParameter("logout") != null) {
      session.setAttribute("user", null);

      // Clean up stale session state if any
      for (Step2.AxSchema schema : Step2.AxSchema.values()) {
        session.removeAttribute(schema.getShortName());
      }
      session.removeAttribute("request_token");
      session.removeAttribute("access_token");
      session.removeAttribute("access_token_secret");
      session.removeAttribute("accessor");
    }

    if (session.getAttribute("user") == null && !"short".equals(req.getParameter("size"))) {
      // redirect to login servlet
      resp.sendRedirect(req.getRequestURI().replaceAll("/hello$", "/login"));
    } else {

      getOAuthData(req);

      RequestDispatcher d = req.getRequestDispatcher(templateFile);
      resp.setHeader("Pragma", "no-cache");
      resp.setHeader("Cache-Control", "no-cache");
      resp.setDateHeader("Expires", 0);
      resp.setDateHeader("Date", new Date().getTime());
      d.forward(req, resp);
    }
  }

  private void getOAuthData(HttpServletRequest request) throws ServletException {

    HttpSession session = request.getSession();

    try {
      OAuthAccessor accessor = (OAuthAccessor)session.getAttribute("accessor");

      if (accessor == null) {
        return;
      }

      URL feedUrl = new URL("http://www.google.com/m8/feeds/contacts/default/thin");
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
