/**
 * The Logic Lab
 * @author jpk Jan 30, 2009
 */
package com.tabulaw.server;

import java.io.Serializable;

import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;

import com.google.inject.Inject;
import com.tabulaw.mail.MailManager;

/**
 * Servlet/web level context providing resources used at this the servlet
 * processing level.
 * @author jpk
 */
public final class WebAppContext implements Serializable {

	private static final long serialVersionUID = 2701260921155680582L;

	/**
	 * The key identifying the {@link WebAppContext} in the {@link ServletContext}
	 * .
	 */
	public static final String KEY = Long.toString(serialVersionUID);

	private final MailManager mailManager;
	private final VelocityEngine velocityEngine;

	/**
	 * Constructor
	 * @param mailManager
	 * @param velocityEngine
	 */
	@Inject
	public WebAppContext(MailManager mailManager, VelocityEngine velocityEngine) {
		super();
		this.mailManager = mailManager;
		this.velocityEngine = velocityEngine;
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}
}
