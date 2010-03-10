/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tll.tabulaw.client.ui.nav;

import com.google.gwt.user.client.ui.Composite;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IViewChangeHandler;
import com.tll.client.mvc.view.ViewChangeEvent;
import com.tll.client.mvc.view.ViewKey;

/**
 * A nav related widget relating to view navigation that recieves view change
 * events.
 * @author jpk
 */
public abstract class AbstractNavPanel extends Composite implements IViewChangeHandler {

	/**
	 * The retained current which is set <b>after</b> {@link #handleViewChange()}
	 * is invoked.
	 */
	protected ViewKey currentViewKey;

	@Override
	public final void onViewChange(ViewChangeEvent event) {
		ViewKey pending = ViewManager.get().getCurrentViewKey();
		if(currentViewKey != null && currentViewKey.equals(pending)) {
			return;
		}
		handleViewChange();
		currentViewKey = pending;
	}

	/**
	 * Responsible for updating its state if necessary upon a view change event.
	 */
	protected abstract void handleViewChange();
}
