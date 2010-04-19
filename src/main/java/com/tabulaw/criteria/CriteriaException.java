package com.tabulaw.criteria;

/**
 * CriteriaException
 * @author jpk
 */
public class CriteriaException extends Exception {

	private static final long serialVersionUID = -6844992027997513090L;

	public CriteriaException(String message, Throwable t) {
		super(message, t);
	}

	public CriteriaException(String message) {
		super(message);
	}
}
