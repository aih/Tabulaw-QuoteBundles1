/**
 * The Logic Lab
 * @author jpk Jan 1, 2008
 */
package com.tll.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * HtmlListPanel - Corresponds to either a
 * <ul>
 * or
 * <ol>
 * html tag.
 * @author jpk
 */
public class HtmlListPanel extends Composite {

	/**
	 * Li - HTML list item tag in widget form.
	 * @author jpk
	 */
	static final class Li extends SimplePanel {

		public Li(Widget w) {
			super(DOM.createElement("li"));
			add(w);
		}
	}

	/**
	 * HtmlList
	 * @author jpk
	 */
	static final class HtmlList extends ComplexPanel {

		/**
		 * Constructor
		 * @param ordered
		 */
		HtmlList(boolean ordered) {
			setElement(ordered ? DOM.createElement("ol") : DOM.createElement("ul"));
		}

		int getNumItems() {
			return getWidgetCount();
		}

		void insert(Widget w, int beforeIndex) {
			super.insert(new Li(w), getElement(), beforeIndex, true);
		}
	}

	private final HtmlList list;

	/**
	 * Constructor
	 * @param ordered
	 */
	public HtmlListPanel(boolean ordered) {
		list = new HtmlList(ordered);
		initWidget(list);
	}

	/**
	 * Creates an li widget adding the given widget as its only child then adds
	 * the li widget at the end of this html list.
	 * @param liWidget
	 */
	public void append(Widget liWidget) {
		list.insert(liWidget, list.getNumItems());
	}

	/**
	 * Creates an li widget adding the given widget as its only child then adds
	 * the li widget at the beginning of this html list.
	 * @param liWidget
	 */
	public void prepend(Widget liWidget) {
		list.insert(liWidget, 0);
	}

	/**
	 * Removes the li widget from this list containing the given widget.
	 * @param liWidget
	 */
	public void remove(Widget liWidget) {
		for(final Widget w : list) {
			if(((Li) w).getWidget() == liWidget) {
				list.remove(w);
				break;
			}
		}
	}

	public void clear() {
		list.clear();
	}

	/**
	 * @return The number of list items in this list.
	 */
	public int size() {
		return list.getWidgetCount();
	}
}
