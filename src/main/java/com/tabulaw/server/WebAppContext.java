/**
 * The Logic Lab
 * @author jpk Jan 30, 2009
 */
package com.tabulaw.server;

import java.io.Serializable;

import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;

import com.google.inject.Inject;
import com.tabulaw.mail.EmailDispatcher;
import com.tabulaw.server.rpc.IExceptionHandler;

/**
 * Servlet/web level context providing resources used at this the servlet
 * processing level.
 * @author jpk
 */
public final class WebAppContext implements Serializable {

	private static final long serialVersionUID = 2701260921155680582L;

	/**
	 * Classpath root relative path to the app's velocity templates.
	 */
	public static final String VELOCITY_TEMPLATE_PATH = "vtemplates/";

	/**
	 * The key identifying the {@link WebAppContext} in the {@link ServletContext}
	 * .
	 */
	public static final String KEY = Long.toString(serialVersionUID);

	private final EmailDispatcher emailDispatcher;

	private final VelocityEngine velocityEngine;

	private final IExceptionHandler exceptionHandler;

	/**
	 * Constructor
	 * @param emailDispatcher
	 * @param velocityEngine
	 * @param exceptionHandler
	 */
	@Inject
	public WebAppContext(EmailDispatcher emailDispatcher, VelocityEngine velocityEngine,
			IExceptionHandler exceptionHandler) {
		super();
		this.emailDispatcher = emailDispatcher;
		this.velocityEngine = velocityEngine;
		this.exceptionHandler = exceptionHandler;
	}

	public EmailDispatcher getEmailDispatcher() {
		return emailDispatcher;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}
}
