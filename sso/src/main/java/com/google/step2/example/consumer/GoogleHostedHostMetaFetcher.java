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

import com.google.inject.Inject;
import com.google.step2.discovery.UrlHostMetaFetcher;
import com.google.step2.http.HttpFetcher;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Fetches host-meta files from a Google-hosted location.
 */
public class GoogleHostedHostMetaFetcher extends UrlHostMetaFetcher {

  private static final String SOURCE_PARAM = "step2.hostmeta.google.source";
  private static final String DEFAULT_SOURCE = "https://www.google.com";
  private static final String HOST_META_PATH = "/accounts/o8/.well-known/host-meta";
  private static final String DOMAIN_PARAM = "hd";

  @Inject
  public GoogleHostedHostMetaFetcher(HttpFetcher fetcher) {
    super(fetcher);
  }

  @Override
  protected URI getHostMetaUriForHost(String host) throws URISyntaxException {
    String source = System.getProperty(SOURCE_PARAM, DEFAULT_SOURCE);
    String uri = source + HOST_META_PATH + "?" + DOMAIN_PARAM + "=" + host;
    return new URI(uri);
  }
}
