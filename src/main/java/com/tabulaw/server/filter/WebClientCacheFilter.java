/*
 * Copyright 2006 Mat Gessel <mat.gessel@gmail.com> Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.tabulaw.server.filter;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <em>IMPT:</em>This file was derived from Mat Gessel's gwtTk framework java
 * class GWTCacheFilter (TODO verify class name) class.
 * <p>
 * Adds HTTP response headers according to the configured caching policy.
 * Considers GWT specific resources that include "cache" in the resource name.
 * <p>
 * Initial code for this filter from GWTTk.
 * <p>
 * Usage notes
 * <ul>
 * <li>You can verify that the filter is being applied with Firefox's Web
 * Developer Extension. Click Tools > Web Developer > Information > View
 * Response Headers.
 * <li>If you are running an Apache httpd/Jk/Tomcat server configuration you
 * need to ensure that Tomcat is serving HTML files, otherwise the filter will
 * not be applied.
 * <li>One reason that this filter exists is that you cannot use
 * <code>*.nocache.html</code> or <code>*.cache.html</code> for url
 * patterns. According to the 2.3 servlet spec, an extension is defined as the
 * characters after the <strong>last</strong> period.
 * <li>The header is modified <em>before</em> passing control down the filter
 * chain.
 * </ul>
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">Cache-control
 *      directive</a>
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21">Expires
 *      directive</a>
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.32">Pragma
 *      directive</a>
 */
public class WebClientCacheFilter implements Filter {

	public static final String INITPARAM_GWT_NOCACHE = "gwtNoCachePattern";
	public static final String INITPARAM_GWT_CACHE = "gwtCachePattern";
	public static final String INITPARAM_ONEDAY_CACHE_FILEEXTS = "oneDayCacheFileExts";

	private static final String GWT_NOCACHE_DEFAULT = ".+\\.nocache.*";
	private static final String GWT_CACHE_DEFAULT = ".+\\.cache.*";

	/**
	 * GWT's nocache pattern match string. Matches shall NOT be cached on the
	 * client.
	 */
	private Pattern gwtNoCachePattern;

	/**
	 * GWT's cache pattern match string. Matches shall be cached on the client.
	 */
	private Pattern gwtCachePattern;

	/**
	 * The file extension tokens intended for 1-day caching.
	 */
	private String[] oneDayCacheFileExts;

	public void init(FilterConfig filterConfig) /* throws ServletException */{
		String dontCachePatternString = filterConfig.getInitParameter(INITPARAM_GWT_NOCACHE);
		String cachePatternString = filterConfig.getInitParameter(INITPARAM_GWT_CACHE);
		if(dontCachePatternString == null) {
			dontCachePatternString = GWT_NOCACHE_DEFAULT;
		}
		if(cachePatternString == null) {
			cachePatternString = GWT_CACHE_DEFAULT;
		}
		gwtNoCachePattern = Pattern.compile(dontCachePatternString);
		gwtCachePattern = Pattern.compile(cachePatternString);

		final String token = filterConfig.getInitParameter(INITPARAM_ONEDAY_CACHE_FILEEXTS);
		if(token != null) {
			final StringTokenizer st = new StringTokenizer(token.trim());
			final int num = st.countTokens();
			if(num > 0) {
				oneDayCacheFileExts = new String[st.countTokens()];
				for(int i = 0; i < oneDayCacheFileExts.length; i++) {
					oneDayCacheFileExts[i] = st.nextToken();
				}
			}
		}
	}

	private enum CacheStatus {
		CACHE_1YEAR,
		CACHE_1DAY,
		NOCACHE,
		INDIFFERENT;
	}

	/**
	 * Central method that decides if we want the client to cache the requested
	 * resource.
	 * @param url The resource url of the http request.
	 * @return true/false
	 */
	private CacheStatus getCacheStatus(String url) {
		if(gwtNoCachePattern.matcher(url).matches()) {
			return CacheStatus.NOCACHE;
		}
		else if(gwtCachePattern.matcher(url).matches()) {
			return CacheStatus.CACHE_1YEAR;
		}

		// handle one day caching
		if(oneDayCacheFileExts != null) {
			for(final String ext : oneDayCacheFileExts) {
				if(url.endsWith(ext)) {
					return CacheStatus.CACHE_1DAY;
				}
			}
		}

		// we are indifferent for any other request types
		return CacheStatus.INDIFFERENT;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	ServletException {
		if(request instanceof HttpServletRequest) {
			final HttpServletRequest hRequest = (HttpServletRequest) request;
			final HttpServletResponse hResponse = (HttpServletResponse) response;
			switch(getCacheStatus(hRequest.getRequestURL().toString())) {
				case CACHE_1YEAR:
					// the w3c spec requires a maximum age of 1 year
					hResponse.addHeader("Cache-Control", "max-age=31536000");
					hResponse.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
					break;
				case CACHE_1DAY:
					hResponse.addHeader("Cache-Control", "max-age=86400");
					hResponse.setDateHeader("Expires", System.currentTimeMillis() + 86400000L);
					break;
				case NOCACHE:
					hResponse.addHeader("Cache-Control", "no-cache no-store must-revalidate");
					hResponse.addHeader("Pragma", "no-cache"); // HTTP/1.0
					hResponse.setDateHeader("Expires", 0l);
					break;
			}
		}
		chain.doFilter(request, response);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
}
