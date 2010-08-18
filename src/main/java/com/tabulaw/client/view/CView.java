package com.tabulaw.client.view;

import com.tabulaw.client.ui.view.ViewContainer;

/**
 * CView - Simple encapsulation of a view and its initializer ensuring
 * that the view key in the view equals that in the initializer.
 * @author jpk
 */
final class CView extends AbstractViewKeyProvider {

	final ViewContainer vc;
	final IViewInitializer init;

	/**
	 * Constructor
	 * @param vc
	 * @param init
	 */
	public CView(ViewContainer vc, IViewInitializer init) {
		if(vc == null || init == null) throw new IllegalArgumentException("Null view and/or init.");
		if(!vc.getViewKey().equals(init.getViewKey())) {
			throw new IllegalArgumentException("Un-equal view keys");
		}
		this.vc = vc;
		this.init = init;
	}

	public ViewKey getViewKey() {
		return vc.getViewKey();
	}

	@Override
	public String toString() {
		return "CView[" + init.getViewKey() + "]";
	}
}