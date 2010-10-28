package com.tabulaw.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class OpenIdServlet extends HttpServlet {

	private final static Log log = LogFactory.getLog(OpenIdServlet.class);

	/**
	 * Either initiates a login to a given provider or processes a response from
	 * an IDP.
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String domain = request.getParameter("hd");
		if (domain != null) {
		} else {
			// This is a response from the provider, go ahead and validate
			doPost(request, response);
		}
	}

	/**
	 * Handle the response from the OpenID Provider.
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
