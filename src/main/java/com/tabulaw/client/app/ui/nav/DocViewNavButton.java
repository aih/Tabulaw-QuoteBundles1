/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 26, 2010
 */
package com.tabulaw.client.app.ui.nav;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.view.DocViewInitializer;
import com.tabulaw.client.view.IViewInitializer;
import com.tabulaw.client.view.UnloadViewRequest;
import com.tabulaw.client.view.ViewKey;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.model.DocKey;
import com.tabulaw.model.IModelKeyProvider;
import com.tabulaw.model.ModelKey;

/**
 * Routes to an open document when clicked.
 * @author jpk
 */
public class DocViewNavButton extends AbstractNavButton implements IModelKeyProvider {

	private final DocKey documentKey;

	/**
	 * Constructor
	 * @param docKey The document model key
	 */
	public DocViewNavButton(DocKey docKey) {
		super();
		this.documentKey = docKey;
		setTitle(documentKey.descriptor());
		setDisplay(docKey.getName(), "doc", Resources.INSTANCE.XButton());
		// image goes last
		DOM.appendChild(getElement(), img.getElement());
		img.setTitle("Close");
		sinkEvents(Event.ONCLICK);
	}

	@Override
	public IViewInitializer getViewInitializer() {
		return new DocViewInitializer(documentKey);
	}

	@Override
	public ModelKey getModelKey() {
		return documentKey;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if(DOM.eventGetType(event) == Event.ONCLICK) {
			if(event.getEventTarget().cast() == img.getElement()) {
				ViewKey vk = getViewInitializer().getViewKey();
				ViewManager.get().dispatch(new UnloadViewRequest(vk, true, false));
				event.stopPropagation();
				return;
			}
		}
		super.onBrowserEvent(event);
	}
}
