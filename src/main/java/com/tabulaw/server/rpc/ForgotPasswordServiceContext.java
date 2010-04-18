/**
 * The Logic Lab
 * @author jpk Jan 30, 2009
 */
package com.tabulaw.server.rpc;

import javax.servlet.ServletContext;

import com.tabulaw.service.IForgotPasswordHandler;
import com.tll.mail.MailManager;
import com.tll.server.rpc.IExceptionHandler;

/**
 * AppContext - An instance of this type is stored in the {@link ServletContext}
 * providing references to app scoped constructs for use by servlets to fulfill
 * requests.
 * @author jpk
 */
public class ForgotPasswordServiceContext {
	
	public static final String KEY = ForgotPasswordServiceContext.class.getName();

	private final IForgotPasswordHandler handler;
	private final MailManager mailManager;
	private final IExceptionHandler exceptionHandler;

	/**
	 * Constructor
	 * @param handler
	 * @param mailManager
	 * @param exceptionHandler
	 */
	public ForgotPasswordServiceContext(IForgotPasswordHandler handler, MailManager mailManager,
			IExceptionHandler exceptionHandler) {
		super();
		this.handler = handler;
		this.mailManager = mailManager;
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * @return the userService
	 */
	protected IForgotPasswordHandler getForgotPasswordHandler() {
		return handler;
	}

	/**
	 * @return the mailManager
	 */
	protected MailManager getMailManager() {
		return mailManager;
	}

	/**
	 * @return the exceptionHandler
	 */
	protected IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

}
