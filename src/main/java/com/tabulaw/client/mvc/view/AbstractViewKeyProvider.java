/**
 * The Logic Lab
 * @author jpk
 * @since Mar 26, 2009
 */
package com.tabulaw.client.mvc.view;

/**
 * AbstractViewKeyProvider - Consolidates {@link #equals(Object)} and
 * {@link #hashCode()} for {@link IViewKeyProvider}s.
 * @author jpk
 */
public abstract class AbstractViewKeyProvider implements IViewKeyProvider {
	
	@Override
	public final int compareTo(IViewKeyProvider other) {
		if(getViewKey().equals(other.getViewKey())) return 0;
		// un-comparable really so just return -1
		return -1;
	}

	@Override
	public final boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		return getViewKey().equals(((AbstractViewKeyProvider) obj).getViewKey());
	}

	@Override
	public final int hashCode() {
		return 31 + getViewKey().hashCode();
	}
}
