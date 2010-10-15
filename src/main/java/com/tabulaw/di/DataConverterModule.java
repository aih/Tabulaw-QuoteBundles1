package com.tabulaw.di;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tabulaw.config.Config;
import com.tabulaw.config.IConfigAware;
import com.tabulaw.config.IConfigKey;
import com.tabulaw.service.convert.ConverterHttpClient;

/**
 * DataConverterModule - Module for working with standalone converter.
 * @author Andrey Levchenko
 */

public class DataConverterModule extends AbstractModule implements IConfigAware {
	/**
	 * ConfigKeys - Config keys for the dataconverter module.
	 * @author Andrey Levchenko
	 */
	private static enum ConfigKeys implements IConfigKey {

		CONVERTER_URL("converter.url");

		private final String key;

		private ConfigKeys(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}
	}

	static final Log log = LogFactory.getLog(DataConverterModule.class);
	
	private Config config;

	
	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		log.info("Loading converter HttpClient module...");
		bind(ConverterHttpClient.class).toProvider(new Provider<ConverterHttpClient>() {

			public ConverterHttpClient get() {
				String converterUrl = config.getString(ConfigKeys.CONVERTER_URL.getKey());

				return new ConverterHttpClient(converterUrl);
			}

		}).in(Scopes.SINGLETON);
	}

}
