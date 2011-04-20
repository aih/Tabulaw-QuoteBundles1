package com.tabulaw.client.ui;


import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.PermissionsListingWidget;
import com.tabulaw.client.app.ui.UsernameSuggestOracle;
import com.tabulaw.client.ui.listing.IListingHandler;
import com.tabulaw.client.ui.listing.ListingEvent;
import com.tabulaw.common.data.rpc.ModelPayload;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;

public class ShareBundleDialog extends Dialog implements ClickHandler {
	private class RefreshHandler implements IListingHandler<User> {

		@Override
		public void onListingEvent(ListingEvent<User> event) {
			if (event.getPageElements() != null) {
				usersWithPermission.addAll(event.getPageElements());
			}
		}
		
	}
	private final Set<User> usersWithPermission = new HashSet<User>(); 
	private final PermissionsListingWidget listing;
	private Button addButton = new Button("Add", this);
	private Button closeButton = new Button("Close", this);
	private SuggestBox suggestbox = new SuggestBox(new UsernameSuggestOracle(usersWithPermission));
	private QuoteBundle bundle;
	private FlowPanel buttonsPanel = new FlowPanel();


	public void setBundle(QuoteBundle bundle) {
		this.bundle = bundle;
		listing.setBundleId(bundle.getId());
	}
	public ShareBundleDialog(String title) {
		setText(title);
		//current user to permission list
		User currentUser = ClientModelCache.get().getUser();
		usersWithPermission.add(currentUser);
		FlowPanel panel = new FlowPanel(); 
		FlowPanel suggestPanel = new FlowPanel ();
		suggestPanel.addStyleName("suggest-user-panel"); 
		addButton.addStyleName("add-permission-button");
		suggestPanel.add(suggestbox);
		suggestPanel.add(addButton);

		listing = new PermissionsListingWidget();
		listing.addListingHandler(new RefreshHandler());
		
		panel.add(suggestPanel);
		panel.add(listing );
		panel.add(buttonsPanel);

		buttonsPanel.add(closeButton);
		buttonsPanel.addStyleName("share-permission-panel");
		this.setWidget(panel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		listing.refresh();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				suggestbox.setFocus(true);
			}
		});
	}

	@Override
	public void onClick(ClickEvent clkEvt) {
		if (clkEvt.getSource() == closeButton) {
			hide();
		}
		if (clkEvt.getSource() == addButton) {
	        Poc.getUserDataService().getUserByEmail(suggestbox.getValue(),  new AsyncCallback<ModelPayload<User>>() {
	            public void onFailure(Throwable caught) {
	            	Window.alert(caught.getLocalizedMessage());
	            }
	            public void onSuccess(ModelPayload<User> payload) {
            		User user = payload.getModel();
	            	if	(user!=null && !usersWithPermission.contains(user)) {
	            		Poc.getUserDataService().shareBundleForUser(user, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

							@Override
							public void onFailure(Throwable caught) {
				            	Window.alert(caught.getLocalizedMessage());
							}

							@Override
							public void onSuccess(ModelPayload<QuoteBundle> result) {
								listing.refresh();
								suggestbox.setValue(null);
							}
						});
	            	}
	            }
	        });
			
		}
	}

}
