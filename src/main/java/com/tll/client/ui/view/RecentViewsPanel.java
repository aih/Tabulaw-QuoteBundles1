/**
 * The Logic Lab
 * @author jpk Jan 3, 2008
 */
package com.tll.client.ui.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IViewChangeHandler;
import com.tll.client.mvc.view.ViewChangeEvent;
import com.tll.client.mvc.view.ViewRef;
import com.tll.client.ui.HtmlListPanel;

/**
 * RecentViewsPanel - Displays view links vertically that are currently in the
 * view cache that are NOT in the popped state.
 * @author jpk
 */
public final class RecentViewsPanel extends Composite implements IViewChangeHandler {

	/**
	 * Styles
	 * @author jpk
	 */
	protected static class Styles {

		/**
		 * Style applied to the widget containing the recent views listing.
		 */
		public static final String RECENT_VIEWS = "recentviews";
	}
	
	private final int capacity;

	/**
	 * The topmost (parent) ulPanel of this {@link Widget}.
	 */
	private final FlowPanel container = new FlowPanel();

	/**
	 * AbstractView history links.
	 */
	private final HtmlListPanel ulPanel = new HtmlListPanel(false);

	/**
	 * Constructor
	 * @param capacity the max number of view links to display
	 */
	public RecentViewsPanel(int capacity) {
		this.capacity = capacity;
		container.setStyleName(Styles.RECENT_VIEWS);
		container.add(ulPanel);
		initWidget(container);
	}

	public void onViewChange(ViewChangeEvent event) {
		// NOTE: rebuild the ulPanel (it's MUCH easier than trying to remove/insert)
		ulPanel.clear();

		final ViewRef[] refs = ViewManager.get().getViewRefs(capacity, false, false);
		final int count = refs.length;

		// re-build the recent view list
		// NOTE: ending at 1 before last element (skip the current view)
		for(int i = 0; i < count - 1; i++) {
			ulPanel.append(new ViewLink(refs[i]));
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		ViewManager.get().addViewChangeHandler(this);
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		ViewManager.get().removeViewChangeHandler(this);
	}

}