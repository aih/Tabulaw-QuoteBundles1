package com.tabulaw.service.convert;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class ConverterHttpClient {
	public static final String KEY = "converterhttpclient";
	private HttpClient httpClient;
	private String converterUrl;
	private URI converterURI;

	public ConverterHttpClient(String converterUrl) {
		this.converterUrl = converterUrl;
	}

	public String getConverterUrl() {
		return this.converterUrl;
	}

	private void init() throws URISyntaxException {
		converterURI = new URI(converterUrl);

		HttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(converterURI.getScheme(), PlainSocketFactory.getSocketFactory(),
				converterURI.getPort()));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		httpClient = new DefaultHttpClient(cm, params);
	}

	public HttpResponse execute(HttpPost httppost) throws ClientProtocolException, IOException, URISyntaxException {
		if (httpClient == null) {
			synchronized (this) {
				if (httpClient == null) {
					init();
				}
			}
		}
		httppost.setURI(converterURI);
		return httpClient.execute(httppost);
	}

	public void shutdown() {
		if (httpClient != null) { 
			httpClient.getConnectionManager().shutdown();
		}
	}

}
