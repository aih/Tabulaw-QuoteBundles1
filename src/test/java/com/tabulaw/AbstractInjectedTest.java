package com.tabulaw;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * AbstractInjectedTest - Abstract base class for tests wishing to leverge
 * dependency injection via Guice.
 * @author jpk
 */
public abstract class AbstractInjectedTest {

	/**
	 * Builds a new {@link Injector} from one or more {@link Module}s.
	 * @param modules The {@link Module}s to bind
	 * @return A new {@link Injector}
	 */
	protected static final Injector buildInjector(Module... modules) {
		return Guice.createInjector(Stage.DEVELOPMENT, modules);
	}

	protected final Log logger = LogFactory.getLog(this.getClass());

	/**
	 * The dependency injector.
	 */
	protected Injector injector;

	/**
	 * Constructor
	 */
	public AbstractInjectedTest() {
		super();
	}

	/**
	 * Sets member property injector used for running the tests.
	 */
	protected final void buildTestInjector() {
		assert injector == null : "The injector was already built";
		logger.debug("Building test dependency injector..");
		final Module[] modules = getModules();
		if(modules != null && modules.length > 0) {
			this.injector = buildInjector(modules);
		}
	}

	/**
	 * @return List of {@link Module}s for available for the derived tests.
	 */
	protected final Module[] getModules() {
		final List<Module> list = new ArrayList<Module>();
		addModules(list);
		return list.toArray(new Module[list.size()]);
	}

	/**
	 * Sub-classes should override this method to add the needed dependency
	 * injection modules.
	 * @param modules the modules to add
	 */
	protected void addModules(List<Module> modules) {
	}

	/**
	 * Before class hook.
	 */
	protected void beforeClass() {
		buildTestInjector();
	}

	/**
	 * After class hook.
	 */
	protected void afterClass() {
	}

	/**
	 * Before method hook
	 */
	protected void beforeMethod() {
		// no-op
	}

	/**
	 * After method hook.
	 */
	protected void afterMethod() {
		// no-op
	}
}
