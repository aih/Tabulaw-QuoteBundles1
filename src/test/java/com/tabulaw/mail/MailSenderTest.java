package com.tabulaw.mail;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.tabulaw.config.Config;
import com.tabulaw.di.MailModule;

/**
 * {@link IMailSender} test.
 * @author jpk
 */
@Test(groups = {
	"mail",
	"send" })
public class MailSenderTest {

	/**
	 * VelocityModule - Ad hoc velocity module for testing purposes.
	 * @author jpk
	 */
	protected static class VelocityModule extends AbstractModule {

		@Override
		protected void configure() {
			// VelocityEngine
			bind(VelocityEngine.class).toProvider(new Provider<VelocityEngine>() {

				public VelocityEngine get() {
					try {
						final VelocityEngine ve = new VelocityEngine();
						ve.init();
						return ve;
					}
					catch(final Exception e) {
						throw new IllegalStateException("Unable to instantiate the velocity engine: " + e.getMessage(), e);
					}
				}

			});
		}
	}

	private Config config;

	private MailManager mailManager;

	private MailRouting mailRouting;

	@BeforeClass(groups = "mail")
	protected void onSetUp() throws Exception {
		config = Config.load();
		Injector injector = Guice.createInjector(new MailModule(config), new VelocityModule());
		mailManager = injector.getInstance(MailManager.class);
		Assert.assertNotNull(mailManager, "Unable to obtain the MailManager bean from the application context.");

		NameEmail sendEmail = new NameEmail("jopaki-send", "jopaki@gmail.com");
		NameEmail rcvEmail = new NameEmail("jopaki-recieve", "jopaki@gmail.com");
		mailRouting = new MailRouting(sendEmail, rcvEmail);
		Assert.assertNotNull(mailRouting, "Unable to obtain the test.MailRouting from the application context.");
	}

	@Test(groups = "mail")
	public void testSendSimpleText() {
		IMailContext mc =
				mailManager
						.buildSimpleContext(mailRouting, "Test Subject", "This is a test of the emergency broadcast system.");
		mailManager.sendEmail(mc);
	}

	@Test(groups = "mail")
	public void testSendTextTemplate() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subject", "Test Subject");
		params.put("emailAddress", "email@address.com");
		params.put("password", "pizzlestizzle");
		IMailContext mc = mailManager.buildTextTemplateContext(mailRouting, "forgot-password", params);
		mailManager.sendEmail(mc);
	}

	public void testSendHtmlTemplate() {
	}
}
