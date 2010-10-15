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
package com.google.step2.example.consumer;

import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;
import com.google.step2.example.consumer.servlet.LoginServlet;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class SimpleProviderInfoStore implements OAuthProviderInfoStore {

  private static final String REQUEST_URL = "requestTokenURL";
  private static final String AUTH_URL = "userAuthorizationURL";
  private static final String ACCESS_URL = "accessTokenURL";
  private static final String CONSUMER_KEY = "consumerKey";
  private static final String CONSUMER_SECRET = "consumerSecret";
  private static final String SCOPE = "scope";
  private static final String OAUTH_TEST_ENDPOINT = "oauthTestEndpoint";

  private static final Logger log =
      Logger.getLogger(SimpleProviderInfoStore.class.getName());

  private final Map<String, OAuthAccessor> accessors =
    new HashMap<String, OAuthAccessor>();

  public SimpleProviderInfoStore() {
    ClassLoader loader = LoginServlet.class.getClassLoader();
    String propertiesFile =
      LoginServlet.class.getPackage().getName().replace(".", "/")
      + "/providers.properties";
    InputStream stream = loader.getResourceAsStream(propertiesFile);
    Properties properties = new Properties();
    try {
      properties.load(stream);
    } catch (IOException e) {
      log.severe("could not find providers.properties:" + e.getMessage());
      throw new RuntimeException("cannot make SimpleProviderInfoStore", e);
    }

    @SuppressWarnings("unchecked")
    Enumeration<String> propertyNames =
        (Enumeration<String>)properties.propertyNames();

    while (propertyNames.hasMoreElements()) {
      String propertyName = propertyNames.nextElement();
      String key = propertyName.split("\\.")[0];
      String parameter = propertyName.split("\\.")[1];

      OAuthAccessor accessor = accessors.get(key);
      if (accessor == null) {
        OAuthServiceProvider provider = new OAuthServiceProvider("", "", "");
        OAuthConsumer consumer = new OAuthConsumer("", "", "", provider);
        accessor = new OAuthAccessor(consumer);
        accessors.put(key, accessor);
      }

      if (parameter.equalsIgnoreCase(ACCESS_URL)) {
        OAuthServiceProvider provider = new OAuthServiceProvider(
            accessor.consumer.serviceProvider.requestTokenURL,
            accessor.consumer.serviceProvider.userAuthorizationURL,
            properties.getProperty(propertyName, "").trim());

        OAuthConsumer consumer = new OAuthConsumer("",
            accessor.consumer.consumerKey,
            accessor.consumer.consumerSecret,
            provider);
        OAuthAccessor newAccessor = new OAuthAccessor(consumer);
        copyProperties(newAccessor, accessor);
        accessors.put(key, newAccessor);

      } else if (parameter.equalsIgnoreCase(AUTH_URL)) {
        OAuthServiceProvider provider = new OAuthServiceProvider(
            accessor.consumer.serviceProvider.requestTokenURL,
            properties.getProperty(propertyName, "").trim(),
            accessor.consumer.serviceProvider.accessTokenURL);

        OAuthConsumer consumer = new OAuthConsumer("",
            accessor.consumer.consumerKey,
            accessor.consumer.consumerSecret,
            provider);
        OAuthAccessor newAccessor = new OAuthAccessor(consumer);
        copyProperties(newAccessor, accessor);
        accessors.put(key, newAccessor);


      } else if (parameter.equalsIgnoreCase(REQUEST_URL)) {
        OAuthServiceProvider provider = new OAuthServiceProvider(
            properties.getProperty(propertyName, "").trim(),
            accessor.consumer.serviceProvider.userAuthorizationURL,
            accessor.consumer.serviceProvider.accessTokenURL);

        OAuthConsumer consumer = new OAuthConsumer("",
            accessor.consumer.consumerKey,
            accessor.consumer.consumerSecret,
            provider);
        OAuthAccessor newAccessor = new OAuthAccessor(consumer);
        copyProperties(newAccessor, accessor);
        accessors.put(key, newAccessor);

      } else if (parameter.equalsIgnoreCase(CONSUMER_KEY)) {
        OAuthConsumer consumer = new OAuthConsumer("",
            properties.getProperty(propertyName, "").trim(),
            accessor.consumer.consumerSecret,
            accessor.consumer.serviceProvider);
        OAuthAccessor newAccessor = new OAuthAccessor(consumer);
        copyProperties(newAccessor, accessor);
        accessors.put(key, newAccessor);

      } else if (parameter.equalsIgnoreCase(CONSUMER_SECRET)) {
        OAuthConsumer consumer = new OAuthConsumer("",
            accessor.consumer.consumerKey,
            properties.getProperty(propertyName, "").trim(),
            accessor.consumer.serviceProvider);
        OAuthAccessor newAccessor = new OAuthAccessor(consumer);
        copyProperties(newAccessor, accessor);
        accessors.put(key, newAccessor);

      } else if (parameter.equalsIgnoreCase(SCOPE)) {
        accessor.setProperty(SCOPE,
            properties.getProperty(propertyName, "").trim());
      } else if (parameter.equalsIgnoreCase(OAUTH_TEST_ENDPOINT)) {
        accessor.setProperty(OAUTH_TEST_ENDPOINT,
            properties.getProperty(propertyName, "").trim());
      }
    }
  }

  /**
   * Copies all properties from old accessor to new accessor
   * @param newAccessor
   * @param accessor
   */
  private void copyProperties(OAuthAccessor newAccessor, OAuthAccessor accessor) {
    newAccessor.setProperty(SCOPE, accessor.getProperty(SCOPE));
    newAccessor.setProperty(OAUTH_TEST_ENDPOINT, accessor.getProperty(OAUTH_TEST_ENDPOINT));
  }

  public OAuthAccessor getOAuthAccessor(String providerKey)
      throws ProviderInfoNotFoundException {
    OAuthAccessor result = accessors.get(providerKey);

    if (result == null) {
      throw new ProviderInfoNotFoundException(
          "no such provider: " + providerKey);
    } else {
      return copyAccessor(result);
    }
  }

  private OAuthAccessor copyAccessor(OAuthAccessor accessor) {
      OAuthAccessor result = new OAuthAccessor(accessor.consumer);
      result.setProperty("scope", accessor.getProperty("scope"));
      return result;
  }
}