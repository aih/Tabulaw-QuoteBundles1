/**
 * The Logic Lab
 * @author jpk
 * @since Mar 24, 2009
 */
package com.tll.client.mvc.view;

/**
 * ShowViewRequest
 * @author jpk
 */
public final class ShowViewRequest extends AbstractViewRequest {

	/**
	 * The view initializer responsible for providing the {@link ViewKey}.
	 */
	private final IViewInitializer init;

	/**
	 * Constructor - Use for dynamic views that will have default view options.
	 * @param init
	 */
	public ShowViewRequest(IViewInitializer init) {
		this.init = init;
	}

	/**
	 * Constructor - Use for static views that will have default view options.
	 * @param viewClass
	 */
	public ShowViewRequest(ViewClass viewClass) {
		this(new StaticViewInitializer(viewClass));
	}

	@Override
	public final boolean addHistory() {
		return true;
	}

	/**
	 * @return The view initializer.
	 */
	public IViewInitializer getViewInitializer() {
		return init;
	}

	@Override
	public ViewKey getViewKey() {
		return init.getViewKey();
	}
}
