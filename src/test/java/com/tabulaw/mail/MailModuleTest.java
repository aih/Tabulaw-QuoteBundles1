/**
 * The Logic Lab
 * @author jpk
 * Apr 20, 2009
 */
package com.tabulaw.mail;

import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.tabulaw.config.Config;
import com.tabulaw.di.MailModule;

/**
 * MailModuleTest - Verifies the dependency injection of the {@link MailModule}.
 * @author jpk
 */
@Test(groups = "mail")
public class MailModuleTest {

	/**
	 * Tests the di of the mail module.
	 * @throws Exception
	 */
	public void test() throws Exception {
		Config c = Config.load();
		Injector i;
		try {
			i = Guice.createInjector(Stage.DEVELOPMENT, new MailModule(c));
		}
		catch(Throwable t) {
			throw new Exception(t.getMessage());
		}
		MailManager m = i.getInstance(MailManager.class);
		assert m != null;
	}
}
