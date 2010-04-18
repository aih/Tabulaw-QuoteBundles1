/**
 * The Logic Lab
 * @author jpk
 * @since Oct 24, 2009
 */
package com.tll.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * ClassUtil - Class and ClassLoader related utility methods.
 * @author jpk
 */
public class ClassUtil {

	/**
	 * Provides the {@link URL} for a classpath resource supporting those
	 * contained in JARs in addition to those that aren't in to following order:
	 * <ol>
	 * <li>package-level (no forward '/' char in the path string)
	 * <li>root level ('/' char prefix in the path string)
	 * </ol>
	 * @param name the non-path name of a resource expected to be at the root of
	 *        the classpath
	 * @return The {@link URL} guaranteed to resolve to an existing resource or
	 *         <code>null</code> if it wasn't found
	 * @throws IllegalArgumentException When the given name is <code>null</code>
	 *         or empty
	 */
	public static URL getResource(String name) throws IllegalArgumentException {
		if(name == null || name.length() < 1) throw new IllegalArgumentException("Null or empty name");
		URL url = null;
		final String rootPath = name.charAt(0) == '/' ? name : '/' + name;
		final String packagePath = name.charAt(0) == '/' ? name.substring(1) : name;
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if(cl != null) {
			url = cl.getResource(packagePath);
			if(url == null) {
				url = cl.getResource(rootPath);
			}
		}
		if(url == null) {
			// fallback on the other classloader..
			final ClassLoader ocl = ClassUtil.class.getClassLoader();
			if(ocl != cl) {
				url = ocl.getResource(packagePath);
				if(url == null) {
					url = ocl.getResource(rootPath);
				}
			}
		}
		return url;
	}

	/**
	 * Provides an array of {@link URL}s for classpath root resources having a
	 * given name supporting those contained in JARs in addition to those that
	 * aren't.
	 * @param name the non-path name of a resource expected to be at the root of
	 *        the classpath
	 * @return The {@link URL} guaranteed to resolve to an existing resource or
	 *         <code>null</code> if it wasn't found
	 * @throws IllegalArgumentException When the given name is <code>null</code>
	 *         or empty
	 */
	public static URL[] getResources(String name) throws IllegalArgumentException {
		if(name == null || name.length() < 1) throw new IllegalArgumentException("Null or empty name");
		Enumeration<URL> urls = null;
		try {
			final String packagePath = name.charAt(0) == '/' ? name.substring(1) : '/' + name;
			final String rootPath = name.charAt(0) == '/' ? name : '/' + name;
			final ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if(cl != null) {
				urls = cl.getResources(packagePath);
				if(urls == null) {
					urls = cl.getResources(rootPath);
				}
			}
			if(urls == null) {
				// fallback on the other classloader..
				final ClassLoader ocl = ClassUtil.class.getClassLoader();
				if(ocl != cl) {
					urls = ocl.getResources(packagePath);
					if(urls == null) {
						ocl.getResources(rootPath);
					}
				}
			}
		}
		catch(final IOException e) {
			throw new IllegalStateException(e);
		}
		if(urls == null) return null;
		final ArrayList<URL> list = new ArrayList<URL>(3);
		while(urls.hasMoreElements()) {
			list.add(urls.nextElement());
		}
		return list.toArray(new URL[list.size()]);
	}

}
