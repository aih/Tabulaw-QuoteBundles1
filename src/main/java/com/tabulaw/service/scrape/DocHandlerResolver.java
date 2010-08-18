package com.tabulaw.service.scrape;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * DocHandlerResolver
 * @author ??
 */
public class DocHandlerResolver {

	private static final Set<IDocHandler> handlers =
			Collections.unmodifiableSet(new HashSet<IDocHandler>(Arrays.asList(new GoogleScholarDocHandler())));

	/**
	 * Resolves the appropriate doc handler based on the given remote url.
	 * @param remoteUrl
	 * @return the resolved doc handler
	 * @throws IllegalArgumentException When the given remote url does not resolve
	 *         to a doc handler
	 */
	public static IDocHandler resolveHandlerFromRemoteUrl(String remoteUrl) throws IllegalArgumentException {
		for(IDocHandler handler : handlers) {
			if(handler.isSupportedUrl(remoteUrl)) return handler;
		}
		throw new IllegalArgumentException("Un-supported remote url: " + remoteUrl);
	}

	/**
	 * Resolves the appropriate doc handler based on the given data provider.
	 * @param dataProvider
	 * @return the resolved doc handler
	 * @throws IllegalArgumentException When the given data provider does not
	 *         resolve to a doc handler
	 */
	public static IDocHandler resolveHandlerFromDataProvider(String dataProvider) throws IllegalArgumentException {
		DocDataProvider ddp = Enum.valueOf(DocDataProvider.class, dataProvider);
		for(IDocHandler handler : handlers) {
			if(handler.getDocDataType() == ddp) return handler;
		}
		throw new IllegalArgumentException("Unknown doc data provider: " + dataProvider);
	}
}
