/**
 * The Logic Lab
 * @author jpk Jan 27, 2008
 */
package com.tabulaw.client.mvc.view;

/**
 * ViewRef - Uniquely identifies {@link IView}s at runtime.
 * @author jpk
 */
public class ViewKey {

	/**
	 * The ViewClass which may never be <code>null</code>.
	 */
	private final ViewClass viewClass;

	/**
	 * The runtime unique view id for the associated ViewClass.
	 */
	private int viewId;

	/**
	 * Constructor
	 * @param viewClass
	 */
	public ViewKey(ViewClass viewClass) {
		super();
		this.viewClass = viewClass;
		this.viewId = 0;
	}

	/**
	 * Constructor
	 * @param viewClass The ViewClass
	 * @param viewId The unique view id
	 */
	public ViewKey(ViewClass viewClass, int viewId) {
		super();
		if(viewClass == null) {
			throw new IllegalArgumentException("A view key must always specify a view class.");
		}
		this.viewClass = viewClass;
		this.viewId = viewId;
	}

	/**
	 * @return The view class - the "compile time" component of the view key.
	 */
	public ViewClass getViewClass() {
		return viewClass;
	}

	/**
	 * The "runtime" or dynamic component of the view key. Non-zero view ids imply
	 * the view is <em>dynamic</em> meaning its identifiability is only
	 * ascertainable at runtime.
	 * @return the unique view id. If <code>0</code>, the view to which this key
	 *         refers is considered "static". If non-zero, the view is said to be
	 *         dynamic.
	 */
	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != getClass()) return false;
		final ViewKey that = (ViewKey) obj;
		return that.viewClass.equals(this.viewClass) && that.viewId == viewId;
	}

	@Override
	public int hashCode() {
		return viewId * 31 + viewClass.hashCode();
	}

	@Override
	public String toString() {
		return "view Class: " + viewClass + ", hash: " + hashCode();
	}
}
