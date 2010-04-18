/**
 * The Logic Lab
 * @author jpk
 * @since Mar 25, 2009
 */
package com.tll.client.mvc.view;

/**
 * AbstractDynamicViewInitializer - Common base class for dynamic view
 * initializers.
 * @author jpk
 */
public abstract class AbstractDynamicViewInitializer extends AbstractViewKeyProvider implements IViewInitializer {
	
	private final ViewClass viewClass;

	private transient ViewKey key;

	/**
	 * Constructor
	 * @param viewClass
	 */
	public AbstractDynamicViewInitializer(ViewClass viewClass) {
		if(viewClass == null) throw new IllegalArgumentException();
		this.viewClass = viewClass;
	}

	/**
	 * @return The runtime view id that uniquely identifies this view among like
	 *         views having the same view class.
	 */
	protected abstract int getViewId();

	@Override
	public final ViewKey getViewKey() {
		if(key == null) {
			key = new ViewKey(viewClass, getViewId());
		}
		return key;
	}
}
