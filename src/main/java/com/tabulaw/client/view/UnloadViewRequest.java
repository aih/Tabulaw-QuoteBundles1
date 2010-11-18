/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Jan 17, 2008
 */
package com.tabulaw.client.view;


/**
 * UnloadViewRequest
 * @author jpk
 */
public final class UnloadViewRequest extends ViewOpRequest {

	private final boolean destroy;
	private final boolean erradicate;

	/**
	 * Constructor
	 * @param viewKey
	 * @param destroy Destroy the view?
	 * @param erradicate Permanantly remove from cache?
	 */
	public UnloadViewRequest(ViewKey viewKey, boolean destroy, boolean erradicate) {
		super(viewKey);
		this.destroy = destroy;
		this.erradicate = erradicate;
	}

	@Override
	public boolean addHistory() {
		return false;
	}

	/**
	 * Remove the view from queue of current views? Subsequent view requests will
	 * have to completely re-load the view.
	 * @return true/false
	 */
	public boolean isDestroy() {
		return destroy;
	}

	/**
	 * Remove the view from the view cache entirely?
	 * @return the erradicate
	 */
	public boolean isErradicate() {
		return erradicate;
	}

}