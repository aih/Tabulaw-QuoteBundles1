/**
 * The Logic Lab
 * @author jpk Sep 3, 2007
 */
package com.tabulaw.client.view;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.ui.view.ViewToolbar;

/**
 * AbstractView - Base view class for all defined views in the app.
 * @author jpk
 * @param <I> the view initializer type
 */
public abstract class AbstractView<I extends IViewInitializer> extends Composite implements IView<I> {

	/**
	 * The view key uniquely indentifying the view at runtime.
	 */
	private ViewKey viewKey;

	/**
	 * The view container ref set at initialization.
	 */
	private Widget viewContainerRef;

	/**
	 * The wrapped Widget
	 */
	private final FlowPanel pnl = new FlowPanel();

	/**
	 * Constructor
	 */
	public AbstractView() {
		super();
		pnl.setStylePrimaryName(Styles.VIEW);
		initWidget(pnl);
	}

	@Override
	public final Widget getViewWidget() {
		return this;
	}

	public final ViewKey getViewKey() {
		return viewKey;
	}

	@Override
	public String getShortViewName() {
		return getLongViewName();
	}

	/**
	 * Adds a Widget to this view's UI layout.
	 * @param widget
	 */
	protected final void addWidget(Widget widget) {
		pnl.add(widget);
	}

	/**
	 * Override this method when the impl needs a css style callout added to the
	 * view Widget. The default is <code>null</code>.
	 * @return AbstractView impl specific style.
	 */
	protected String getViewStyle() {
		return null;
	}

	/**
	 * @return The widget ref of the widget that contains this view entirely.
	 *         <p>
	 *         NOTE: this method is only valid during and after
	 *         {@link #apply(Widget, ViewToolbar)} is called.
	 */
	public final Widget getViewContainerRef() {
		return viewContainerRef;
	}
	
	@Override
	public String getElementId() {
		if(viewKey == null) throw new IllegalStateException();
		return getElement().getId();
	}

	@Override
	public final void initialize(I initializer) {
		if(initializer == null || initializer.getViewKey() == null)
			throw new IllegalArgumentException("Null or invalid view initializer.");
		viewKey = initializer.getViewKey();

		// set unique dom element id
		getElement().setId("view_" + viewKey.getToken());

		// add view specific style to the view's widget
		if(getViewStyle() != null) {
			addStyleName(getViewStyle());
		}

		// do impl specific initialization
		doInitialization(initializer);
	}

	/**
	 * Performs impl specific initialization just after the ViewKey has been set.
	 * @param initializer the view initializer
	 */
	protected abstract void doInitialization(I initializer);

	@Override
	public final void apply(Widget viewCntnrRef, ViewToolbar toolbar) {
		this.viewContainerRef = viewCntnrRef;
		loaded();
		decorateToolbar(toolbar);
	}

	/**
	 * Called when the view is fully wired up but not refreshed. At a minimum,
	 * this method indicates when the {@link #getViewContainerRef()} method may be
	 * validly called.
	 */
	protected void loaded() {
		// base impl no-op
	}

	/**
	 * Sub-classes may override this method to do custom toolbar decorations.
	 * @param toolbar the view toolbar to decorate
	 */
	protected void decorateToolbar(ViewToolbar toolbar) {
		// base impl no-op
	}

	@Override
	public final void refresh() {
		doRefresh();
	}

	/**
	 * This is how the views are refreshed.
	 */
	protected void doRefresh() {
		// base impl no-op
	}

	/**
	 * Life-cycle provision for view implementations to perform clean-up before
	 * this view looses reference-ability. This could mean, for example, to issue
	 * an RPC cache clean up type command.
	 */
	@Override
	public final void onDestroy() {
		Log.debug("Destroying view " + toString());
		doDestroy();
	}

	/**
	 * AbstractView impls use this hook to perform any necessary clean up just
	 * before this view looses reference-ability
	 */
	protected abstract void doDestroy();

	@Override
	public final String toString() {
		return getViewClass() + " [" + (viewKey == null ? "-nokey-" : viewKey) + "]";
	}
}
