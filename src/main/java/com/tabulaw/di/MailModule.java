/*
 * The Logic Lab 
 */
package com.tabulaw.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.tabulaw.config.Config;
import com.tabulaw.config.IConfigAware;
import com.tabulaw.config.IConfigKey;
import com.tabulaw.mail.IComposer;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.IMailSender;
import com.tabulaw.mail.MailRouting;
import com.tabulaw.mail.MailSender;
import com.tabulaw.mail.NameEmail;
import com.tabulaw.mail.SimpleComposer;
import com.tabulaw.mail.TemplateComposer;

/**
 * MailModule - Module for programmatic email distribution.
 * @author jpk
 */
public final class MailModule extends AbstractModule implements IConfigAware {

	private static final Log log = LogFactory.getLog(MailModule.class);

	/**
	 * ConfigKeys - Config keys for the mail module.
	 * @author jpk
	 */
	private static enum ConfigKeys implements IConfigKey {

		DEFAULT_FROM_NAME("mail.default.FromName"),
		DEFAULT_FROM_ADDRESS("mail.default.FromAddress"),
		DEFAULT_TO_NAME("mail.default.ToName"),
		DEFAULT_TO_ADDRESS("mail.default.ToAddress"),
		PRIMARY_HOST("mail.host.primary"),
		PRIMARY_HOST_USERNAME("mail.host.primary.username"),
		PRIMARY_HOST_PASSWORD("mail.host.primary.password"),
		SECONDARY_HOST("mail.host.secondary"),
		SECONDARY_HOST_USERNAME("mail.host.secondary.username"),
		SECONDARY_HOST_PASSWORD("mail.host.secondary.password"),
		NUM_SEND_RETRIES("mail.numberOfSendRetries"),
		SEND_RETRY_DELAY_MILIS("mail.sendRetryDelayMilis"),
		TEMPLATE_PATH("mail.template.baseTemplatePath"),
		TEMPLATE_SUFFIX_TEXT("mail.template.textTemplateSuffix"),
		TEMPLATE_SUFFIX_HTML("mail.template.htmlTemplateSuffix");

		private final String key;

		private ConfigKeys(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}
	}

	/**
	 * MailTemplatePath annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface MailTemplatePath {
	}

	/**
	 * MailTemplateSuffixText annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface MailTemplateSuffixText {
	}

	/**
	 * MailTemplateSuffixHtml annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface MailTemplateSuffixHtml {
	}

	/**
	 * DefaultMailRouting annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface DefaultMailRouting {
	}

	/**
	 * PrimaryMailSender annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface PrimaryMailSender {
	}

	/**
	 * SecondaryMailSender annotation
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( {
		ElementType.FIELD, ElementType.PARAMETER
	})
	@BindingAnnotation
	public @interface SecondaryMailSender {
	}

	Config config;

	/**
	 * Constructor
	 */
	public MailModule() {
		super();
	}

	/**
	 * Constructor
	 * @param config
	 */
	public MailModule(Config config) {
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
		log.info("Employing mail module");

		// default mail routing object
		bind(Key.get(MailRouting.class, DefaultMailRouting.class)).toProvider(new Provider<MailRouting>() {

			public MailRouting get() {
				String dfltFromName = config.getString(ConfigKeys.DEFAULT_FROM_NAME.getKey());
				String dfltFromAddress = config.getString(ConfigKeys.DEFAULT_FROM_ADDRESS.getKey());
				String dfltToName = config.getString(ConfigKeys.DEFAULT_TO_NAME.getKey());
				String dfltToAddress = config.getString(ConfigKeys.DEFAULT_TO_ADDRESS.getKey());

				final NameEmail dfltSender = new NameEmail(dfltFromName, dfltFromAddress);
				final NameEmail dfltRecipient = new NameEmail(dfltToName, dfltToAddress);
				final MailRouting mr = new MailRouting();
				mr.setSender(dfltSender);
				mr.addRecipient(dfltRecipient);
				return mr;
			}
		}).in(Scopes.SINGLETON);

		// PrimaryMailSender
		bind(Key.get(JavaMailSender.class, PrimaryMailSender.class)).toProvider(new Provider<JavaMailSender>() {

			public JavaMailSender get() {
				String host = config.getString(ConfigKeys.PRIMARY_HOST.getKey());
				String username = config.getString(ConfigKeys.PRIMARY_HOST_USERNAME.getKey());
				String password = config.getString(ConfigKeys.PRIMARY_HOST_PASSWORD.getKey());
				final JavaMailSenderImpl impl = new JavaMailSenderImpl();
				impl.setHost(host);
				impl.setUsername(username);
				impl.setPassword(password);
				return impl;
			}
		}).in(Scopes.SINGLETON);

		// SecondaryMailSender
		bind(Key.get(JavaMailSender.class, SecondaryMailSender.class)).toProvider(new Provider<JavaMailSender>() {

			public JavaMailSender get() {
				String host = config.getString(ConfigKeys.SECONDARY_HOST.getKey());
				String username = config.getString(ConfigKeys.SECONDARY_HOST_USERNAME.getKey());
				String password = config.getString(ConfigKeys.SECONDARY_HOST_PASSWORD.getKey());
				final JavaMailSenderImpl impl = new JavaMailSenderImpl();
				impl.setHost(host);
				impl.setUsername(username);
				impl.setPassword(password);
				return impl;
			}
		}).in(Scopes.SINGLETON);

		// bind template config props
		final String tmpPath = config.getString(ConfigKeys.TEMPLATE_PATH.getKey());
		final String tmpSfxTxt = config.getString(ConfigKeys.TEMPLATE_SUFFIX_TEXT.getKey());
		final String tmpSfxHtm = config.getString(ConfigKeys.TEMPLATE_SUFFIX_HTML.getKey());
		bindConstant().annotatedWith(MailTemplatePath.class).to(tmpPath);
		bindConstant().annotatedWith(MailTemplateSuffixText.class).to(tmpSfxTxt);
		bindConstant().annotatedWith(MailTemplateSuffixHtml.class).to(tmpSfxHtm);

		// IMailSender
		bind(IMailSender.class).toProvider(new Provider<IMailSender>() {

			public IMailSender get() {
				int numberOfSendRetries = config.getInt(ConfigKeys.NUM_SEND_RETRIES.getKey());
				int sendRetryDelayMilis = config.getInt(ConfigKeys.SEND_RETRY_DELAY_MILIS.getKey());
				final List<JavaMailSender> javaMailSenders = Arrays.asList(primary, secondary);

				final List<IComposer<? extends IMailContext>> composers = new ArrayList<IComposer<? extends IMailContext>>(2);
				composers.add(simpleComposer);
				if(templateComposer != null) composers.add(templateComposer);

				return new MailSender(javaMailSenders, numberOfSendRetries, sendRetryDelayMilis, composers);
			}

			@Inject
			@PrimaryMailSender
			JavaMailSender primary;
			@Inject
			@SecondaryMailSender
			JavaMailSender secondary;
			@Inject
			SimpleComposer simpleComposer;
			@Inject(optional = true)
			TemplateComposer templateComposer;

		}).in(Scopes.SINGLETON);

	}

}
