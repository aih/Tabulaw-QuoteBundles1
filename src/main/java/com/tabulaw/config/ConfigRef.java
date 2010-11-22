/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Apr 6, 2009
 */
package com.tabulaw.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * ConfigRef - Serves as a pointer to a classpath resource holding eligible
 * configuration properties.
 * @author jpk
 */
public final class ConfigRef {

	/**
	 * Resolves resource names to {@link URL}s via the current thread's
	 * {@link ClassLoader}.
	 * @param name the resource name
	 * @param loadAll flag indicating whether to employ
	 *        {@link ClassLoader#getResource(String)} or
	 *        {@link ClassLoader#getResources(String)}.
	 * @return the list of resovled {@link URL}s corresponding to the given
	 *         resource name
	 * @throws IllegalArgumentException When an error occurs while resolving the
	 *         resource name
	 */
	private static ArrayList<URL> resolve(String name, boolean loadAll) throws IllegalArgumentException {
		assert name != null;
		ArrayList<URL> list = new ArrayList<URL>();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if(loadAll) {
			Enumeration<URL> urls = null;
			try {
				if(cl != null) {
					urls = cl.getResources(name);
					if(urls == null || !urls.hasMoreElements()) {
						// try the root name..
						if(!name.startsWith("/")) {
							urls = cl.getResources('/' + name);
						}
					}
				}
				if(urls == null || !urls.hasMoreElements()) {
					// try the other class loader..
					cl = ConfigRef.class.getClassLoader();
					urls = cl.getResources(name);
					if(urls == null || !urls.hasMoreElements()) {
						// try the root name..
						if(!name.startsWith("/")) {
							urls = cl.getResources('/' + name);
						}
					}
				}
				if(urls == null || !urls.hasMoreElements()) {
					throw new IllegalArgumentException("Unable to find any config resources named: " + name);
				}
			}
			catch(IOException e) {
				throw new IllegalArgumentException("Unable to resolve resources: " + e.getMessage(), e);
			}
			while(urls.hasMoreElements()) {
				list.add(urls.nextElement());
			}
		}
		else {
			URL url = cl.getResource(name);
			if(url == null) {
				// try the root name..
				if(!name.startsWith("/")) {
					url = cl.getResource('/' + name);
				}
				if(url == null) {
					// try the other class loader..
					url = ConfigRef.class.getClassLoader().getResource(name);
					if(url == null) {
						throw new IllegalArgumentException("Unable to find config resource named: " + name);
					}
				}
			}
			list.add(url);
		}
		return list;
	}

	/**
	 * The default config properties resource name.
	 */
	public static final String DEFAULT_NAME = "config.properties";

	/**
	 * The default bahavior for handling delimeter parsing in config resources
	 * while loading.
	 */
	public static final boolean DEFAULT_DISABLE_DELIMETER_PARSING = true;

	/**
	 * The default bahavior for loading all or the "first" resource of a given
	 * name.
	 */
	public static final boolean DEFAULT_LOAD_ALL = false;

	/**
	 * Disable delimeter parsing when loading config properties?
	 */
	final boolean disableDelimeterParsing;

	/**
	 * The internally cached list of resolved urls.
	 */
	final ArrayList<URL> urls;

	/**
	 * Constructor
	 */
	public ConfigRef() {
		this(DEFAULT_NAME, DEFAULT_DISABLE_DELIMETER_PARSING, DEFAULT_LOAD_ALL);
	}

	/**
	 * Constructor
	 * @param resourceName
	 */
	public ConfigRef(String resourceName) {
		this(resourceName, DEFAULT_DISABLE_DELIMETER_PARSING, DEFAULT_LOAD_ALL);
	}

	/**
	 * Constructor
	 * @param resourceName The required name of the resource holding config
	 *        properties
	 * @param disableDelimeterParsing boolean indicating whether or not to parse
	 *        properties containing multiple values
	 * @param loadAll Load all found resources of the given name?
	 */
	public ConfigRef(String resourceName, boolean disableDelimeterParsing, boolean loadAll) {
		if(resourceName == null) throw new IllegalArgumentException("Null resource name.");
		this.urls = resolve(resourceName, loadAll);
		this.disableDelimeterParsing = disableDelimeterParsing;
	}

	/**
	 * Constructor
	 * @param loadAll
	 */
	public ConfigRef(boolean loadAll) {
		this(DEFAULT_NAME, DEFAULT_DISABLE_DELIMETER_PARSING, loadAll);
	}

	/**
	 * Constructor
	 * @param disableDelimeterParsing
	 * @param loadAll
	 */
	public ConfigRef(boolean disableDelimeterParsing, boolean loadAll) {
		this(DEFAULT_NAME, disableDelimeterParsing, loadAll);
	}

	/**
	 * Constructor
	 * @param resource The resolved resource
	 */
	public ConfigRef(URL resource) {
		this(resource, DEFAULT_DISABLE_DELIMETER_PARSING);
	}

	/**
	 * Constructor
	 * @param resource The resolved resource
	 * @param disableDelimeterParsing boolean indicating whether or not to parse
	 *        properties containing multiple values
	 */
	public ConfigRef(URL resource, boolean disableDelimeterParsing) {
		if(resource == null) throw new IllegalArgumentException("Null resource.");
		urls = new ArrayList<URL>(1);
		urls.add(resource);
		this.disableDelimeterParsing = disableDelimeterParsing;
	}
}
