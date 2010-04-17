package com.tabulaw.criteria;

import com.tll.ApplicationException;

/**
 * CriteriaException
 * @author jpk
 */
public class CriteriaException extends ApplicationException {

	private static final long serialVersionUID = -6844992027997513090L;

	public CriteriaException(String message, String var, Throwable t) {
		super(message, var, t);
	}

	public CriteriaException(String message, String var) {
		super(message, var);
	}

	public CriteriaException(String message, String[] vars, Throwable t) {
		super(message, vars, t);
	}

	public CriteriaException(String message, String[] vars) {
		super(message, vars);
	}

	public CriteriaException(String message, Throwable t) {
		super(message, t);
	}

	public CriteriaException(String message) {
		super(message);
	}
}
