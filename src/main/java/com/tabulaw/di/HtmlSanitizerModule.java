package com.tabulaw.di;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tabulaw.config.Config;
import com.tabulaw.config.IConfigAware;
import com.tabulaw.config.IConfigKey;
import com.tabulaw.service.sanitizer.AntiSamySanitizer;
import com.tabulaw.service.sanitizer.ISanitizer;
import com.tabulaw.util.ClassUtil;

public class HtmlSanitizerModule extends AbstractModule implements IConfigAware{
	/**
	 * ConfigKeys - Config keys for the AntiSamy sanitizer.
	 * @author Andrey Levchenko
	 */
	private static enum ConfigKeys implements IConfigKey {

		POLICY_FILE_NAME("html.sanitizer.policy");

		private final String key;

		private ConfigKeys(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}
	}

	static final Log log = LogFactory.getLog(HtmlSanitizerModule.class);
	
	private Config config;

	
	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		log.info("Loading antisamy module...");
		bind(ISanitizer.class).to(AntiSamySanitizer.class);
		bind(AntiSamy.class).toProvider(new Provider<AntiSamy>() {

			public AntiSamy get() {
				String policyFileName = config.getString(ConfigKeys.POLICY_FILE_NAME.getKey());
				
				URL config = ClassUtil.getResource(policyFileName);

				Policy policy;
				try {
					policy = Policy.getInstance(config);
				} catch (PolicyException e) {
					log.error(e);
					throw new CreationException(null);
				}

				AntiSamy as = new AntiSamy(policy);

				return as;
			}

		}).in(Scopes.SINGLETON);
	}
	

}
