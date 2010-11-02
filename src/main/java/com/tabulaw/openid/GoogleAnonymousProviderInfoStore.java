package com.tabulaw.openid;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;

public class GoogleAnonymousProviderInfoStore implements OAuthProviderInfoStore {

	public GoogleAnonymousProviderInfoStore() {
	}

	@Override
	public OAuthAccessor getOAuthAccessor(String providerKey)
			throws ProviderInfoNotFoundException {
		if (!"google".equals(providerKey)) {
			throw new ProviderInfoNotFoundException("no such provider: "
					+ providerKey);
		}
		return createAccessor();
	}

	private OAuthAccessor createAccessor() {
		OAuthServiceProvider provider = new OAuthServiceProvider("", "", "");
		OAuthConsumer consumer = new OAuthConsumer("/", "127.0.0.1:8888",
				"anonymous", provider);
		return new OAuthAccessor(consumer);
	}
}