package com.tabulaw.openid;


import com.google.inject.Inject;
import com.google.step2.discovery.UrlHostMetaFetcher;
import com.google.step2.http.HttpFetcher;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Fetches host-meta files from a Google-hosted location.
 */
public class GoogleHostedHostMetaFetcher extends UrlHostMetaFetcher {

    private static final String template = "https://www.google.com/accounts/o8/.well-known/host-meta?hd=%s";

    @Inject
    public GoogleHostedHostMetaFetcher(HttpFetcher fetcher) {
        super(fetcher);
    }

    @Override
    protected URI getHostMetaUriForHost(String host) throws URISyntaxException {
        try {
            host = URLEncoder.encode(host, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String uri = String.format(template, host);
        return new URI(uri);
    }
} 