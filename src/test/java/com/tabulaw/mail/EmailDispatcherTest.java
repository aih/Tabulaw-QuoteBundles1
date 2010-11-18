/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jopaki
 * @since Aug 16, 2010
 */
package com.tabulaw.mail;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.tabulaw.AbstractInjectedTest;
import com.tabulaw.config.Config;
import com.tabulaw.di.MailModule;
import com.tabulaw.mail.EmailDispatcher;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.MailManager;
import com.tabulaw.mail.MailRouting;
import com.tabulaw.mail.NameEmail;

/**
 * EmailDispatcherTest
 * @author jopaki
 */
@Test(groups = "mail")
public class EmailDispatcherTest extends AbstractInjectedTest {
	
	private MailManager mailManager;
	
	private EmailDispatcher emailDispatcher;
	
	public void testFastFive() throws Exception {
		for(int i = 0; i < 5; i++) {
			MailRouting routing = new MailRouting(new NameEmail("tabulaw test", "test@tabulaw.com"), new NameEmail("jpk", "jopaki@gmail.com"));
			IMailContext job = mailManager.buildSimpleContext(routing, "subject", "body");
			emailDispatcher.queueEmail(job);
		}
		Thread.sleep(35 * 1000);
	}

	@BeforeClass
	void init() {
		super.beforeClass();
		mailManager = injector.getInstance(MailManager.class);
		emailDispatcher = injector.getInstance(EmailDispatcher.class);
		new Thread(emailDispatcher).start();
	}

	@Override
	protected void addModules(List<Module> modules) {
		super.addModules(modules);
		Config config = Config.load();
		modules.add(new MailModule(config));
	}	
}
