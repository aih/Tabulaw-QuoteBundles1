/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 */
package com.tabulaw.server.rpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.tabulaw.common.data.Status;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.server.IExceptionHandler;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.RequestContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.server.WebAppContext;

/**
 * RpcServlet
 * @author jpk
 */
public abstract class RpcServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = 5032508084607776181L;

	protected final Log log = LogFactory.getLog(this.getClass());

	/**
	 * Adds an error message to the given status.
	 * @param emsg the error message to add
	 * @param status
	 */
	public static final void addError(String emsg, Status status) {
		status.addMsg(emsg, MsgLevel.ERROR, MsgAttr.EXCEPTION.flag);
	}

	/**
	 * Adds a fatal error message to the given status.
	 * @param emsg the error message to add
	 * @param status
	 */
	public static final void addFatalError(String emsg, Status status) {
		status.addMsg(emsg, MsgLevel.FATAL, MsgAttr.EXCEPTION.flag);
	}

	/**
	 * Appends ui-friendly message(s) to the given {@link Status} instance from
	 * the given exception.
	 * @param t the exception
	 * @param status the status instance
	 */
	public static final void exceptionToStatus(Throwable t, Status status) {
		String emsg = t.getMessage();
		if(emsg == null) {
			emsg = t.getClass().getSimpleName();
		}
		if(t instanceof RuntimeException) {
			addFatalError(emsg, status);
		}
		else {
			addError(emsg, status);
		}
	}

	/**
	 * Handles the given exception by handing it off the the
	 * {@link IExceptionHandler} held in the given {@link WebAppContext}.
	 * @param webAppContext
	 * @param t the exception
	 */
	public static final void handleException(WebAppContext webAppContext, Throwable t) {
		webAppContext.getExceptionHandler().handleException(t);
	}

	/**
	 * @return The request context for the current request.
	 */
	protected final RequestContext getRequestContext() {
		return new RequestContext(getThreadLocalRequest(), getThreadLocalResponse());
	}

	/**
	 * @return The sole web app context instance.
	 */
	protected final WebAppContext getWebAppContext() {
		return (WebAppContext) getRequestContext().getServletContext().getAttribute(WebAppContext.KEY);
	}

	/**
	 * @return The sole persist context instance.
	 */
	protected final UserContext getUserContext() {
		return (UserContext) getRequestContext().getSession().getAttribute(UserContext.KEY);
	}

	/**
	 * @return The sole persist context instance.
	 */
	protected final PersistContext getPersistContext() {
		return (PersistContext) getRequestContext().getServletContext().getAttribute(PersistContext.KEY);
	}

	/**
	 * Delegates to {@link #handleException(WebAppContext, Throwable)} by
	 * extracting the web app context.
	 * @param t the exception
	 */
	protected final void handleException(Throwable t) {
		handleException(getWebAppContext(), t);
	}
}
