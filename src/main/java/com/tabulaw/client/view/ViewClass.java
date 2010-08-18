/**
 * The Logic Lab
 * @author jpk
 * Mar 12, 2008
 */
package com.tabulaw.client.view;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewClass - Serves as a view definition enabling factory style view
 * instantiation.
 * @author jpk
 */
public abstract class ViewClass {

	/**
	 * List of all registered classes in the app.
	 */
	private static final List<ViewClass> classes = new ArrayList<ViewClass>();

	/**
	 * Add a view class
	 * @param vclass The view class
	 */
	public static void addClass(ViewClass vclass) {
		assert vclass != null && vclass.getName() != null;
		if(findClassByViewName(vclass.getName()) != null) {
			throw new IllegalArgumentException("Name: " + vclass.getName()
					+ " already exists in one of the existing view classes");
		}
		classes.add(vclass);
	}

	/**
	 * Finds the ViewClass with the given view name.
	 * @param viewName The view name to search for. If <code>null</code>,
	 *        <code>null</code> is returned.
	 * @return The found ViewClass or
	 *         <code>null<code> of no ViewClass exists for the given view name.
	 */
	public static final ViewClass findClassByViewName(String viewName) {
		if(viewName != null) {
			for(final ViewClass vc : classes) {
				if(viewName.equals(vc.getName())) return vc;
			}
		}
		return null;
	}

	/**
	 * @return the name of the view
	 */
	public abstract String getName();

	/**
	 * May be overridden.
	 * @return The default view options for the associated view.
	 */
	public ViewOptions getViewOptions() {
		return ViewOptions.DEFAULT_VIEW_OPTIONS;
	}

	/**
	 * @return New instance of the view this class defines.
	 */
	public abstract IView<?> newView();

	@Override
	public final boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof ViewClass == false) return false;
		return this.getName().equals(((ViewClass) obj).getName());
	}

	@Override
	public final int hashCode() {
		return getName().hashCode();
	}

	@Override
	public final String toString() {
		return getName();
	}
}
