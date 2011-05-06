package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;

public class ShareLabel extends Label implements MouseUpHandler, MouseOutHandler, MouseOverHandler {
	private QuoteBundle bundle;
	PopupPanel popup = new PopupPanel();
	HTML popupContent = new HTML("some text");

	public ShareLabel() {
		addStyleName("share-label");

		popup.add(popupContent);

		addMouseUpHandler(this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);
	}

	public void setBundle(QuoteBundle bundle) {
		this.bundle = bundle;
		setShareLabelText();
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		popup.hide();
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		int popupTop = getAbsoluteTop() + getOffsetHeight();
		int popupLeft = getAbsoluteLeft();
		popup.setPopupPosition(popupLeft, popupTop);
		popup.show();

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		popup.hide();
		ShareBundleDialog shareBundleDialog = new ShareBundleDialog("Share Quote Bundle...");
		shareBundleDialog.setBundle(bundle);
		shareBundleDialog.center();

	}

	private void setShareLabelText() {
		if (bundle != null) {
			if (bundle.getParentBundleId()!=null) {
				setText("Shared by");
				User owner = ClientModelCache.get().getQuoteBundleOwner(bundle.getParentBundleId());
				popupContent.setHTML(owner.getEmailAddress());
			} else if (bundle.getChildQuoteBundles().size() > 0) {
				setText("Shared with");
			} else {
				setText("Share");
			}
		}
	}

}
