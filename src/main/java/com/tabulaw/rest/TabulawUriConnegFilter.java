package com.tabulaw.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.container.filter.UriConnegFilter;

/**
 * Container request filter which is used to specify requested media-type 
 * as url extension.<br><br>
 * 
 * For example: /quotes/13.xml (text/xml),
 * 				/quotes/13.json (application/json) *  
 *  
 * @author yuri
 *
 */
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
