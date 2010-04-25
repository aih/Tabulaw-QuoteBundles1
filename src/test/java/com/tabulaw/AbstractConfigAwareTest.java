/**
 * The Logic Lab
 * @author jpk
 * @since Sep 18, 2009
 */
package com.tabulaw;

import com.tabulaw.config.Config;
import com.tabulaw.config.ConfigRef;

/**
 * AbstractConfigAwareTest
 * @author jpk
 */
public class AbstractConfigAwareTest extends AbstractInjectedTest {

	private Config config;

	/**
	 * Constructor - Config instance is lazily loaded
	 */
	public AbstractConfigAwareTest() {
		super();
	}

	/**
	 * Constructor - Config instance is eagerly loaded
	 * @param configRefs - Loads a {@link Config} instance via
	 *        {@link Config#load(ConfigRef...)}
	 */
	public AbstractConfigAwareTest(ConfigRef... configRefs) {
		super();
		this.config = Config.load(configRefs);
	}

	/**
	 * Constructor - Config instance is set to that given
	 * @param config - The config instance to employ
	 */
	public AbstractConfigAwareTest(Config config) {
		super();
		this.config = config;
	}

	/**
	 * @return A new {@link Config} instance. May be overridden.
	 */
	protected Config doGetConfig() {
		return Config.load(); // the default impl
	}

	/**
	 * @return The test config instance that is lazily loaded if not config
	 *         created upon construction.
	 */
	protected final Config getConfig() {
		if(config == null) {
			config = doGetConfig();
		}
		return config;
	}
}
