package com.google.step2.example.consumer;

import com.google.inject.Inject;
import com.google.step2.Step2OAuthClient;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Utility class for requesting OAuth tokens from accessors
 *
 * @author sweis@google.com (Steve Weis)
 */
public class OAuthConsumerUtil {

  private static Log log = LogFactory.getLog(OAuthConsumerUtil.class);
  private final Step2OAuthClient client;

  @Inject
  public OAuthConsumerUtil(Step2OAuthClient client) {
    this.client = client;
  }

  public OAuthAccessor getRequestToken(OAuthAccessor accessor)
      throws IOException, OAuthException, URISyntaxException {
    OAuthAccessor accessorCopy = new OAuthAccessor(accessor.consumer);

    OAuthMessage response = client.invoke(accessor,
        accessor.consumer.serviceProvider.requestTokenURL,
        OAuth.newList("scope", accessor.getProperty("scope").toString()));
    log.info("Successfully got OAuth request token");
    accessorCopy.requestToken = response.getParameter("oauth_token");
    accessorCopy.tokenSecret = response.getParameter("oauth_token_secret");
    return accessor;
  }

  public OAuthAccessor getAccessToken(OAuthAccessor accessor)
      throws IOException, OAuthException, URISyntaxException {
    OAuthAccessor accessorCopy = new OAuthAccessor(accessor.consumer);

    OAuthMessage response = client.invoke(accessor,
        accessor.consumer.serviceProvider.accessTokenURL,
        OAuth.newList("oauth_token", accessor.requestToken,
            "scope", accessor.getProperty("scope").toString()));
    log.info("Successfully got OAuth access token");
    accessorCopy.accessToken = response.getParameter("oauth_token");
    accessorCopy.tokenSecret = response.getParameter("oauth_token_secret");
    return accessorCopy;
  }
}
