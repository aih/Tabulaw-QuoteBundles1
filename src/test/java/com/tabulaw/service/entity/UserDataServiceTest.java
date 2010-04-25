/**
 * The Logic Lab
 * @author jpk
 * @since Apr 24, 2010
 */
package com.tabulaw.service.entity;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tabulaw.AbstractConfigAwareTest;
import com.tabulaw.config.Config;
import com.tabulaw.config.ConfigRef;
import com.tabulaw.di.Db4oDaoModule;
import com.tabulaw.di.ModelModule;

/**
 * @author jpk
 */
@Test(groups = "service.entity")
public class UserDataServiceTest extends AbstractConfigAwareTest {
	
	static Injector getTestContext() {
		ConfigRef cref = new ConfigRef("com/tabulaw/service/entity/userDataServiceTestConfig.properties");
		Config cfg = Config.load(cref);
		return Guice.createInjector(new ModelModule(), new Db4oDaoModule(cfg));
	}

	/*
	@Override
	protected void addModules(List<Module> modules) {
		super.addModules(modules);
		modules.add(new ModelModule());
		modules.add(new Db4oDaoModule(getConfig()));
	}
	*/
	
	/**
	 * Verifies ids are properly stored between object container life-cycles.
	 * @throws Exception
	 */
	@Test
	public void testIdGeneration() throws Exception {
		Injector testContext = null;
		try {
			testContext = getTestContext();
			UserDataService svc = testContext.getInstance(UserDataService.class);
			long[] idrng = svc.generateQuoteBundleIds(10);
			long start = idrng[0], end = idrng[1];
			testContext.getInstance(ObjectContainer.class).close();
			testContext = null;
			
			testContext = getTestContext();
			svc = testContext.getInstance(UserDataService.class);
			idrng = svc.generateQuoteBundleIds(10);
			long start2 = idrng[0], end2 = idrng[1];
			
			Assert.assertTrue(start2 == start + 10 + 1);
			Assert.assertTrue(end2 == end + 10 + 1);
		}
		finally {
			if(testContext != null) testContext.getInstance(ObjectContainer.class).close();
		}
	}
}
