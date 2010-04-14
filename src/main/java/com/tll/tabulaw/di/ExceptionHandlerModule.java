/**
 * The Logic Lab
 * @author jpk
 * @since Apr 28, 2009
 */
package com.tll.tabulaw.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tll.config.Config;
import com.tll.config.IConfigAware;
import com.tll.config.IConfigKey;
import com.tll.mail.NameEmail;
import com.tll.server.rpc.IExceptionHandler;
import com.tll.tabulaw.server.rpc.ExceptionHandler;

/**
 * @author jpk
 */
public class ExceptionHandlerModule extends AbstractModule implements IConfigAware {

	private static final Log log = LogFactory.getLog(ExceptionHandlerModule.class);

	/**
	 * ConfigKeys - Configuration property keys for the app context.
	 * @author jpk
	 */
	public static enum ConfigKeys implements IConfigKey {

		ONERROR_SEND_EMAIL("server.onerror.ToAddress"),
		ONERROR_SEND_NAME("server.onerror.ToName");

		private final String key;

		/**
		 * Constructor
		 * @param key
		 */
		private ConfigKeys(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	/**
	 * OnErrorEmail annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface OnErrorEmail {
	}

	Config config;

	/**
	 * Constructor
	 */
	public ExceptionHandlerModule() {
		super();
	}

	/**
	 * Constructor
	 * @param config
	 */
	public ExceptionHandlerModule(Config config) {
		super();
		setConfig(config);
	}

	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		if(config == null) throw new IllegalStateException("No config instance set.");
		log.info("Employing exception handler module");

		bind(Key.get(NameEmail.class, OnErrorEmail.class)).toProvider(new Provider<NameEmail>() {

			final String onErrorName = config.getString(ConfigKeys.ONERROR_SEND_NAME.getKey());
			final String onErrorEmail = config.getString(ConfigKeys.ONERROR_SEND_EMAIL.getKey());
			final NameEmail email = new NameEmail(onErrorName, onErrorEmail);

			@Override
			public NameEmail get() {
				return email;
			}
		}).in(Scopes.SINGLETON);

		bind(IExceptionHandler.class).to(ExceptionHandler.class).in(Scopes.SINGLETON);
	}

}
