/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.view.IPocView;
import com.tabulaw.client.view.ViewClass;
import com.tabulaw.client.view.ViewKey;
import com.tabulaw.client.view.ViewManager;

/**
 * Widget contained in the navCol.
 * @author jpk
 */
public class NavColPanel extends AbstractNavPanel {

	static class Styles {
		
		/**
		 * The wrapped vertical panel that contains the widgets.
		 */
		public static final String VPANEL = "vpanel";

		/**
		 * Style added to all child widgets.
		 */
		public static final String WIDGET = "widget";
	}
	
	private final VerticalPanel vp = new VerticalPanel();

	/**
	 * Constructor
	 */
	public NavColPanel() {
		vp.setStyleName(Styles.VPANEL);
		initWidget(vp);
	}
	
	public void clear() {
		vp.clear();
	}
	
	public void addWidget(Widget w) {
		w.addStyleName(Styles.WIDGET);
		vp.add(w);
	}
	
	public void removeWidget(Widget w) {
		if(vp.remove(w)) {
			w.removeStyleName(Styles.WIDGET);
		}
	}
	
	@Override
	protected void handleViewLoad(ViewKey key) {
		if(key == null || key.equals(currentViewKey)) return;

		// reset the nav row and col based on the new current view type and possibly
		// state

		ViewClass viewClass = key.getViewClass();

		boolean viewTypesDiffer = !viewClass.equals(currentViewKey);

		if(viewTypesDiffer) {
			IPocView<?> view = (IPocView<?>) ViewManager.get().resolveView(key);
			assert view != null;

			Widget[] wlist;

			// nav col
			clear();
			wlist = view.getNavColWidgets();
			if(wlist != null) {
				for(Widget w : wlist) {
					addWidget(w);
				}
			}
		}

		currentViewKey = key;
	}

	@Override
	protected void handleViewUnload(ViewKey key) {
	}
}
