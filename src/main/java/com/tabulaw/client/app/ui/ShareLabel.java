package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;

public class ShareLabel extends Label implements MouseUpHandler, MouseOutHandler, MouseOverHandler {
	public static class SharePopupPanel extends PopupPanel{
		public void setPopupPosition(Widget parent) {
			int top = parent.getAbsoluteTop() + parent.getOffsetHeight();
			int left = parent.getAbsoluteLeft();
			setPopupPosition(left, top);
		}
		
	}
	
	public static class SharedByPopupPanel extends SharePopupPanel{
		public SharedByPopupPanel(QuoteBundle bundle) {
			User owner = ClientModelCache.get().getQuoteBundleOwner(bundle.getParentBundleId());
			String email =owner.getEmailAddress();
			add(new HTML(email));
		} 
	}
	
	public static class SharedWithPopupPanel extends SharePopupPanel{
		public SharedWithPopupPanel(QuoteBundle bundle) {

			List<User> recipients = ClientModelCache.get().getQuoteBundlesOwners(bundle.getChildQuoteBundles());
			
			FlowPanel contents = new FlowPanel(); 

			for (User recipient : recipients) {
				String email = recipient.getEmailAddress();
				HTML row = new HTML(email); 
				contents.add(row);
			}
			add(contents);
		} 
	}
	
	private QuoteBundle bundle;
	SharePopupPanel popup = null;

	public ShareLabel() {
		addStyleName("share-label");

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
		if (popup != null) {
			popup.hide();
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (popup != null) {
			popup.setPopupPosition(this);
			popup.show();
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (popup != null) {
			popup.hide();
		}
		ShareBundleDialog shareBundleDialog = new ShareBundleDialog("Share Quote Bundle...");
		shareBundleDialog.setBundle(bundle);
		shareBundleDialog.center();

	}

	private void setShareLabelText() {
		if (bundle != null) {
			if (bundle.getParentBundleId()!=null) {
				setText("Shared by");
				popup = new SharedByPopupPanel(bundle);
			} else if (bundle.getChildQuoteBundles().size() > 0) {
				setText("Shared with");
				popup = new SharedWithPopupPanel(bundle);
			} else {
				setText("Share");
			}
		}
	}

}
