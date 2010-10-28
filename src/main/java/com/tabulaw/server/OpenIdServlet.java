package com.tabulaw.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;

@SuppressWarnings("serial")
public class OpenIdServlet extends HttpServlet {

	private final static Log log = LogFactory.getLog(OpenIdServlet.class);

	private final static String homeUrl = "/";
	private final static String realm = "http://www.tabulaw.com/";

	private ConsumerManager manager;

	public OpenIdServlet() throws ConsumerException {
		manager = new ConsumerManager();
	}

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
