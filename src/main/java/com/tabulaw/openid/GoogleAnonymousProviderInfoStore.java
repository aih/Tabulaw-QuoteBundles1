package com.tabulaw.openid;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;

public class GoogleAnonymousProviderInfoStore implements OAuthProviderInfoStore {

	private final OAuthAccessor accessor;

	public GoogleAnonymousProviderInfoStore() {
		OAuthServiceProvider provider = new OAuthServiceProvider("", "", "");
		OAuthConsumer consumer = new OAuthConsumer("/", "http://127.0.0.1:8888", "anonymous", provider);
		accessor = new OAuthAccessor(consumer);
	}

	@Override
	public OAuthAccessor getOAuthAccessor(String providerKey)
			throws ProviderInfoNotFoundException {
		if (!"google".equals(providerKey)) {
			throw new ProviderInfoNotFoundException("no such provider: "
					+ providerKey);
		} else {
			return copyAccessor(accessor);
		}
	}

	private OAuthAccessor copyAccessor(OAuthAccessor accessor) {
		OAuthAccessor result = new OAuthAccessor(accessor.consumer);
		result.setProperty("scope", accessor.getProperty("scope"));
		return result;
	}
}