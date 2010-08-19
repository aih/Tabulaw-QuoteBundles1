/**
 * The Logic Lab
 * @author jpk Jan 27, 2008
 */
package com.tabulaw.client.view;

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
	 * Unique token that disambiguates between {@link IView} instances (at
	 * runtime) of like type (having the same {@link ViewClass}).
	 */
	private final String instanceToken;

	/**
	 * Constructor
	 * @param viewClass
	 */
	public ViewKey(ViewClass viewClass) {
		super();
		this.viewClass = viewClass;
		this.instanceToken = null;
	}

	/**
	 * Constructor
	 * @param viewClass The ViewClass
	 * @param instanceToken Unique token that disambiguates between {@link IView}
	 *        instances (at runtime) of like type (having the same
	 *        {@link ViewClass}). The instance token may be <code>null</code>.
	 */
	public ViewKey(ViewClass viewClass, String instanceToken) {
		super();
		if(viewClass == null) throw new NullPointerException();
		this.viewClass = viewClass;
		this.instanceToken = instanceToken;
	}

	/**
	 * @return The view class - the "compile time" component of the view key.
	 */
	public ViewClass getViewClass() {
		return viewClass;
	}

	/**
	 * @return The full state of this view key expressed as a String.
	 */
	public String getToken() {
		StringBuilder sb = new StringBuilder();
		sb.append(viewClass.getName());
		if(instanceToken != null) {
			//sb.append('_');
			sb.append(instanceToken);
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instanceToken == null) ? 0 : instanceToken.hashCode());
		result = prime * result + ((viewClass == null) ? 0 : viewClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		ViewKey other = (ViewKey) obj;
		if(instanceToken == null) {
			if(other.instanceToken != null) return false;
		}
		else if(!instanceToken.equals(other.instanceToken)) return false;
		if(viewClass == null) {
			if(other.viewClass != null) return false;
		}
		else if(!viewClass.equals(other.viewClass)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "ViewKey [viewClass=" + viewClass + ", instanceToken=" + instanceToken + "]";
	}
}
