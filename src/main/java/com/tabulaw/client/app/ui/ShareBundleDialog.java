package com.tabulaw.client.app.ui;


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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.listing.IListingHandler;
import com.tabulaw.client.ui.listing.ListingEvent;
import com.tabulaw.common.data.rpc.ModelPayload;
import com.tabulaw.model.BundleUserBinding;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;

public class ShareBundleDialog extends Dialog implements ClickHandler, IListingHandler<User> {
	private final Set<User> usersWithPermission = new HashSet<User>(); 
	private final PermissionsListingWidget listing;
	private Button addButton = new Button("Add", this);
	private Button closeButton = new Button("Close", this);
	private SuggestBox suggestbox = new SuggestBox(new UsernameSuggestOracle(usersWithPermission));
	private QuoteBundle bundle;
	private FlowPanel buttonsPanel = new FlowPanel();
	private Label listTitle; 


	public void setBundle(QuoteBundle bundle) {
		this.bundle = bundle;
		listing.setBundleId(bundle);
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
		listing.addListingHandler(this);
		
		listTitle = new Label("Shared with:");
		listTitle.addStyleName("add-permission-list-title");
		
		panel.add(suggestPanel);
		panel.add(listTitle);
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
            		final User user = payload.getModel();
	            	if	(user!=null && !usersWithPermission.contains(user)) {
	            		Poc.getUserDataService().shareBundleForUser(user, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

							@Override
							public void onFailure(Throwable caught) {
				            	Window.alert(caught.getLocalizedMessage());
							}

							@Override
							public void onSuccess(ModelPayload<QuoteBundle> result) {
								bundle.addChildQuoteBundle(result.getModel());
								updateClientCache(result.getModel().getId(), user);
								suggestbox.setValue(null);
								listing.refresh();
								Poc.fireModelChangeEvent(new ModelChangeEvent(ShareBundleDialog.this, ModelChangeOp.UPDATED, bundle, null));
								
							}
							private void updateClientCache(String bundleId, User user) {
								BundleUserBinding bundleUserBinding = new BundleUserBinding (bundleId, user);
								ClientModelCache.get().persist(bundleUserBinding, ShareBundleDialog.this);
							}
						});
	            	}
	            }
	        });
			
		}
	}
	@Override
	public void onListingEvent(ListingEvent<User> event) {
		if (event.getPageElements() != null) {
			listTitle.setVisible(true);
			usersWithPermission.addAll(event.getPageElements());
		}
		if (event.getPageElements() == null || event.getPageElements().size()==0) {
			listTitle.setVisible(false);
		}
	}

}
