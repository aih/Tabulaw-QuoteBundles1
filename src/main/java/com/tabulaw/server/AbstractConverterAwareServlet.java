package com.tabulaw.server;

import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.tabulaw.service.convert.ConverterHttpClient;

@SuppressWarnings("serial")
public class AbstractConverterAwareServlet extends HttpServlet {
	@Override
	public void init() throws ServletException {
		super.init();
		ConverterHttpClient httpClient = (ConverterHttpClient) getServletContext().getAttribute(ConverterHttpClient.KEY);
		try {
			httpClient.init();
		} catch (URISyntaxException e) {
			new ServletException("Invalid Converter URL");
		}
		
		
	}

}
