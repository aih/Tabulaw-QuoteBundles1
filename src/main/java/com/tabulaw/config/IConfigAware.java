/**
 * The Logic Lab
 * @author jpk Apr 7, 2009
 */
package com.tabulaw.config;

/**
 * IConfigAware - Generic way to indicate an object is {@link Config} aware.
 * @author jpk
 */
public interface IConfigAware {

	/**
	 * Sets the {@link Config} instance.
	 * @param config the config instance to set
	 */
	void setConfig(Config config);
}
