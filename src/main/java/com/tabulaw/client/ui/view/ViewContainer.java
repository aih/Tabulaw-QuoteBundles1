/**
 * The Logic Lab
 * @author jpk Mar 13, 2008
 */
package com.tabulaw.client.ui.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.IView;
import com.tabulaw.client.mvc.view.UnloadViewRequest;
import com.tabulaw.client.mvc.view.ViewKey;
import com.tabulaw.client.mvc.view.ViewOptions;

/**
 * ViewContainer - UI container for {@link IView} implementations.
 * @author jpk
 */
@SuppressWarnings("synthetic-access")
public final class ViewContainer extends SimplePanel implements ClickHandler {

	/**
	 * Styles - (view.css)
	 * @author jpk
	 */
	protected static class Styles {

		/**
		 * Primary style applied to the widget that is the view container.
		 */
		public static final String VIEW_CONTAINER = "viewContainer";
		/**
		 * Secondary style for view container's in the popped state.
		 */
		public static final String POPPED = "popped";
		/**
		 * Secondary style for view container's in the pinned state.
		 */
		public static final String PINNED = "pinned";

	} // Styles

	/**
	 * The wrapped IView
	 */
	private final IView<?> view;

	private final ViewKey key;

	private final ViewToolbar toolbar;

	private final FlowPanel mainLayout = new FlowPanel();

	/**
	 * Constructor
	 * @param view The view def
	 * @param options The view display options
	 * @param key The view key
	 */
	public ViewContainer(IView<?> view, ViewOptions options, ViewKey key) {
		super();
		if(view == null || key == null) throw new IllegalArgumentException("Null view and/or view key");
		this.view = view;
		this.key = key;
		toolbar = new ViewToolbar(view.getLongViewName(), options, this);
		mainLayout.add(toolbar);
		mainLayout.add(view.getViewWidget());
		mainLayout.setStylePrimaryName(Styles.VIEW_CONTAINER);
		setWidget(mainLayout);
	}

	/**
	 * @return the view
	 */
	public IView<?> getView() {
		return view;
	}

	public ViewKey getViewKey() {
		return key;
	}

	/**
	 * Removes this instance from its parent and clears its state.
	 */
	public void close() {
		removeFromParent();
	}

	/**
	 * @return The contained view toolbar.
	 */
	public ViewToolbar getToolbar() {
		return toolbar;
	}

	public void onClick(ClickEvent event) {
		final Object sender = event.getSource();

		// close the view
		if(sender == toolbar.btnClose) {
			ViewManager.get().dispatch(new UnloadViewRequest(key, true, false));
		}

		// refresh the view
		else if(sender == toolbar.btnRefresh) {
			view.refresh();
		}
	}

	@Override
	public String toString() {
		return "ViewContainer [" + view.toString() + "]";
	}
}
