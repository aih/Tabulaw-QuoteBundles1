/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tll.tabulaw.client.ui.nav;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ViewClass;
import com.tll.client.mvc.view.ViewKey;
import com.tll.tabulaw.client.view.IPocView;

/**
 * Widget contained in the navCol.
 * @author jpk
 */
public class NavColPanel extends AbstractNavPanel {

	static class Styles {

		/**
		 * Style added to all child widgets.
		 */
		public static final String WIDGET = "widget";
	}
	
	private ViewKey currentViewKey;

	/**
	 * Constructor
	 */
	public NavColPanel() {
		initWidget(new VerticalPanel());
	}
	
	public void clear() {
		((VerticalPanel)getWidget()).clear();
	}
	
	public void addWidget(Widget w) {
		w.addStyleName(Styles.WIDGET);
		((VerticalPanel)getWidget()).add(w);
	}
	
	@Override
	protected void handleViewChange() {
		ViewKey key = ViewManager.get().getCurrentViewKey();
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

}
