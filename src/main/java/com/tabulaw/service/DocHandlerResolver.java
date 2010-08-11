package com.tabulaw.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.service.scrape.GoogleScholarDocHandler;
import com.tabulaw.service.scrape.IDocHandler;

public class DocHandlerResolver {
	
	private static final Set<IDocHandler> handlers = 
		Collections.unmodifiableSet(
				new HashSet<IDocHandler>(Arrays.asList(
					new GoogleScholarDocHandler()	
				))
		);

	public static IDocHandler resolveHandler(String surl) {
		for(IDocHandler handler : handlers) {
			if(handler.isSupportedUrl(surl)) return handler;
		}
		return null;
	}

	public static IDocHandler resolveHandler(DocDataProvider dataProviderType) {
		for(IDocHandler handler : handlers) {
			if(handler.getDocDataType() == dataProviderType) return handler;
		}
		return null;
	}
}
