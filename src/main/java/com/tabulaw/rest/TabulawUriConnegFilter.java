package com.tabulaw.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.container.filter.UriConnegFilter;

public class TabulawUriConnegFilter extends UriConnegFilter {

	private static final Map<String, MediaType> MEDIA_EXTENSIONS;
	static {
		Map<String, MediaType> extensions = new HashMap<String, MediaType>();
		extensions.put("xml", MediaType.TEXT_XML_TYPE);
		extensions.put("json", MediaType.APPLICATION_JSON_TYPE);
		
		MEDIA_EXTENSIONS = Collections.unmodifiableMap(extensions);		
	}
	
	public TabulawUriConnegFilter() {
		super(MEDIA_EXTENSIONS);		
	}	
}
