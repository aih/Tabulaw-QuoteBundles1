/**
 * The Logic Lab
 * @author jpk
 * @since Mar 25, 2009
 */
package com.tabulaw.client.mvc.view;

/**
 * StaticViewInitializer - Used for initializing static views. I.e.: views that
 * only require a {@link ViewClass} for initialization.
 * @author jpk
 */
public class StaticViewInitializer extends AbstractViewKeyProvider implements IViewInitializer {

	private final ViewKey key;

	/**
	 * Constructor
	 * @param viewClass the view class
	 */
	public StaticViewInitializer(ViewClass viewClass) {
		this.key = new ViewKey(viewClass);
	}

	@Override
	public ViewKey getViewKey() {
		return key;
	}

}
