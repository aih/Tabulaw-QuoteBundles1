package com.tabulaw.rest;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Container request filter which is responsible to override
 * POST requests with additional parameter _method to
 * PUT or DELETE requests
 * 
 * @author yuri
 *
 */
public class FakeHttpMethodsFilter implements ContainerRequestFilter {

	private static final String FAKE_METHOD_PARAMETER = "_method"; 
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		if (request.getMethod().equals("POST")) {
			String fakeMethod =	request.getFormParameters().getFirst(FAKE_METHOD_PARAMETER);
			if (fakeMethod == null) {
				fakeMethod = request.getQueryParameters().getFirst(FAKE_METHOD_PARAMETER);				
			}
			fakeMethod = StringUtils.upperCase(fakeMethod);
			if ("PUT".equals(fakeMethod) || "DELETE".equals(fakeMethod)) {
				request.setMethod(fakeMethod);
			}
		}
		return request;
	}
}
