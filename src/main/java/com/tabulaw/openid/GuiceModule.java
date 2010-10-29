package com.tabulaw.openid;

import net.oauth.client.OAuthClient;

import org.openid4java.consumer.ConsumerAssociationStore;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.step2.discovery.DefaultHostMetaFetcher;
import com.google.step2.discovery.HostMetaFetcher;
import com.google.step2.hybrid.HybridOauthMessage;
import com.google.step2.openid.ax2.AxMessage2;
import com.google.step2.xmlsimplesign.CertValidator;
import com.google.step2.xmlsimplesign.CnConstraintCertValidator;
import com.google.step2.xmlsimplesign.DefaultCertValidator;
import com.google.step2.xmlsimplesign.DisjunctiveCertValidator;

/**
 * Guice module for configuring the Step2 library. Modified from the original
 * example consumer in the Step2 library to be slightly simpler.
 */
public class GuiceModule extends AbstractModule {

	public GuiceModule() {
	}

	@Override
	protected void configure() {
		try {
			Message.addExtensionFactory(AxMessage2.class);
		} catch (MessageException e) {
			throw new CreationException(null);
		}

		try {
			Message.addExtensionFactory(HybridOauthMessage.class);
		} catch (MessageException e) {
			throw new CreationException(null);
		}

		bind(OAuthClient.class).toInstance(getOAuthClient());

		bind(ConsumerAssociationStore.class).to(
				InMemoryConsumerAssociationStore.class).in(Scopes.SINGLETON);
	}

	private OAuthClient getOAuthClient() {
		return new OAuthClient(new net.oauth.client.httpclient4.HttpClient4());
	}

	@Provides
	@Singleton
	public CertValidator provideCertValidator(
			DefaultCertValidator defaultValidator) {
		CertValidator hardCodedValidator = new CnConstraintCertValidator() {
			@Override
			protected String getRequiredCn(String authority) {
				// Trust Google for signing discovery documents
				return "hosted-id.google.com";
			}
		};

		return new DisjunctiveCertValidator(defaultValidator,
				hardCodedValidator);
	}

	@Provides
	@Singleton
	public HostMetaFetcher provideHostMetaFetcher(
			DefaultHostMetaFetcher fetcher1,
			GoogleHostedHostMetaFetcher fetcher2) {
		// Domains may opt to host their own host-meta documents instead of
		// outsourcing
		// to Google. To try the domain's own host-meta, uncomment the
		// SerialHostMetaFetcher
		// line to adopt a strategy that tries the domain's own version first
		// then falls back
		// on the Google hosted version if that fails. A parallel fetching
		// strategy can also
		// be used to speed up fetching.
		// return new SerialHostMetaFetcher(fetcher1, fetcher2);
		return fetcher2;
	}

}