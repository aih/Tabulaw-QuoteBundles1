/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.ui.nav;

import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tll.client.mvc.view.IViewChangeHandler;
import com.tll.client.mvc.view.ViewChangeEvent;
import com.tll.client.mvc.view.ViewKey;

/**
 * A nav related widget relating to view navigation that recieves view change
 * events.
 * @author jpk
 */
public abstract class AbstractNavPanel extends AbstractModelChangeAwareWidget implements IViewChangeHandler {

	/**
	 * The retained current view key.
	 */
	protected ViewKey currentViewKey;

	@Override
	public final void onViewChange(ViewChangeEvent event) {
		if(event.getOp() == ViewChangeEvent.ViewOp.LOAD) {
			handleViewLoad(event.getKey());
			if(currentViewKey == null || !currentViewKey.equals(event.getKey())) {
				currentViewKey = event.getKey();
			}
		}
		else if(event.getOp() == ViewChangeEvent.ViewOp.UNLOAD) {
			handleViewUnload(event.getKey());
		}
	}

	/**
	 * Handles caught view load events.
	 * @param key the view key of the loaded view
	 */
	protected abstract void handleViewLoad(ViewKey key);
	
	/**
	 * Handles caught view unload events.
	 * @param key the view key of the unloaded view
	 */
	protected abstract void handleViewUnload(ViewKey key);
}
