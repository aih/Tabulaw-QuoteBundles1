/**
 * The Logic Lab
 * @author jpk Jan 7, 2008
 */
package com.tll.client.ui.toolbar;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Toolbar - Simple extension of {@link FlowPanel} that sets common
 * styles/properties relating to a "toolbar" widget implementation.
 * <p>
 * Refer to <code>toolbar.css</code> toolbar Style styles.
 * @author jpk
 */
public class Toolbar extends Composite {

	/**
	 * Styles - (toolbar.css)
	 * @author jpk
	 */
	protected static class Styles {

		/**
		 * Style for a toolbar.
		 */
		public static final String TOOLBAR = "toolbar";
		/**
		 * Style for a toolbar separator widget.
		 */
		public static final String SPLIT = "separator";
		/**
		 * Style for a toolbar button.
		 */
		public static final String BUTTON = "button";
	}

	private final HorizontalPanel pnl = new HorizontalPanel();

	/**
	 * Constructor
	 */
	public Toolbar() {
		super();
		pnl.setStyleName(Styles.TOOLBAR);
		initWidget(pnl);
	}

	/**
	 * Adds a child widget.
	 * @param w
	 */
	public void add(Widget w) {
		pnl.add(w);
	}

	/**
	 * Inserts a child widget.
	 * @param w
	 * @param beforeIndex
	 */
	public void insert(Widget w, int beforeIndex) {
		pnl.insert(w, beforeIndex);
	}

	/**
	 * Removes a child widget.
	 * @param w
	 */
	public void remove(Widget w) {
		pnl.remove(w);
	}

	public final int getWidgetCount() {
		return pnl.getWidgetCount();
	}

	public final Widget getWidget(int index) {
		return pnl.getWidget(index);
	}

	public final int getWidgetIndex(Widget child) {
		return pnl.getWidgetIndex(child);
	}

	/**
	 * Inserts a button before the given index.
	 * @param b
	 * @param beforeIndex
	 */
	public void insertButton(ButtonBase b, int beforeIndex) {
		insertButton(b, null, beforeIndex);
	}

	/**
	 * Inserts a button before the given index.
	 * @param b
	 * @param title
	 * @param beforeIndex
	 */
	public void insertButton(ButtonBase b, String title, int beforeIndex) {
		pnl.insert(b, beforeIndex);
		buttonize(b, title);
	}

	/**
	 * Adds a button to the toolbar.
	 * @param b the button
	 */
	public void addButton(ButtonBase b) {
		addButton(b, null);
	}

	/**
	 * Adds a button to the toolbar with a title that is applied to the button.
	 * @param b The button to add
	 * @param title Optional title text shown on hover. May be <code>null</code>.
	 */
	public final void addButton(ButtonBase b, String title) {
		pnl.add(b);
		buttonize(b, title);
	}

	/**
	 * Styles a just added button.
	 * @param b
	 * @param title
	 */
	private void buttonize(ButtonBase b, String title) {
		final Element td = b.getElement().getParentElement();
		td.setClassName(Styles.BUTTON);
		b.setStylePrimaryName(Styles.BUTTON);
		if(title != null) {
			b.setTitle(title);
		}
	}

	/**
	 * Shows or hides a child Widget.
	 * @param w the target widget
	 * @param show show or hide?
	 */
	public final void show(Widget w, boolean show) {
		if(pnl.getWidgetIndex(w) >= 0) {
			final Element td = w.getElement().getParentElement();
			td.getStyle().setProperty("display", show ? "" : "none");
		}
	}

	/**
	 * Sets the width of the parent Widget of the given Widget.
	 * @param w The Widget
	 * @param width The width
	 */
	public final void setWidgetContainerWidth(Widget w, String width) {
		pnl.setCellWidth(w, width);
	}
}
