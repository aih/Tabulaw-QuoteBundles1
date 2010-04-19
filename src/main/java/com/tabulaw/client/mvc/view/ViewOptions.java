/**
 * The Logic Lab
 * @author jpk
 * Feb 23, 2008
 */
package com.tabulaw.client.mvc.view;

/**
 * ViewOptions
 * @author jpk
 */
public class ViewOptions {

	/**
	 * The default view options.
	 */
	public static final ViewOptions DEFAULT_VIEW_OPTIONS = new ViewOptions(true, true, true, true, false, false);

	private final boolean closable;
	private final boolean minimizable;
	private final boolean refreshable;
	private final boolean popable;
	private final boolean initiallyPopped;
	private final boolean keepInDom;

	/**
	 * Constructor
	 * @param closable
	 * @param minimizable
	 * @param refreshable
	 * @param popable
	 * @param initiallyPopped
	 * @param keepInDom Flag indicating that view's should not be removed from the
	 *        DOM when uloading the view rather to set the view's visibility to
	 *        false.
	 */
	public ViewOptions(boolean closable, boolean minimizable, boolean refreshable, boolean popable,
			boolean initiallyPopped, boolean keepInDom) {
		super();
		this.closable = closable;
		this.minimizable = minimizable;
		this.refreshable = refreshable;
		this.popable = popable;
		this.initiallyPopped = initiallyPopped;
		this.keepInDom = keepInDom;
	}

	/**
	 * @return the minimizable
	 */
	public boolean isMinimizable() {
		return minimizable;
	}

	/**
	 * @return the popable
	 */
	public boolean isPopable() {
		return popable;
	}

	/**
	 * @return the initiallyPopped
	 */
	public boolean isInitiallyPopped() {
		return initiallyPopped;
	}

	/**
	 * @return the closable
	 */
	public boolean isClosable() {
		return closable;
	}

	/**
	 * @return the refreshable
	 */
	public boolean isRefreshable() {
		return refreshable;
	}

	/**
	 * @return <code>true</code> if view's of this class should always be kept in
	 *         the DOM.
	 */
	public boolean isKeepInDom() {
		return keepInDom;
	}
}
